package islm.agenten;

import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;

import java.util.*;

import islm.SessionManager;

public class Unternehmen {
	private double liquiditaet;
	private double preis = 1;
    private double gehalt = 1; // set it to one unit in the start so that the model does not brake
    private int inventar = 1; //keeping track of it product and not in money
    
    private double nachfrageLetzterMonat = 0; 
    private double marginaleKosten = 0;
    
    private int consecutiveMonthsAllJobsFilled = 0; //anzahl an monaten in denen durchgehend alle Positionen gefüllt wurden
    
    private int openPositions = 0;
    
    
    private static final int GAMMA = 24; //anzahl an aufeinanderfolgenden monaten in denen konsequent leute eingestellt wurden
    private static final double DELTA = 0.019; //boundries of distribution to increase wage
    private static final double UPPER_PHI_INVENTORIES = 1.0;
    private static final double LOWER_PHI_INVENTORIES = 0.25;
    private static final double MINIMUM_WAGE = 1.0;
    private static final double MINIMUM_PRICE = 1.0;
    
    private static final double UPPER_PHI_PRICE = 120;
    private static final double LOWER_PHI_PRICE = 1.025;
    private static final double LAMBDA = 3.0;
    
    private static final double THETA = 0.75; //propability of changing price if inventory is not in bounds
    private static final double theta = 0.02;
    private static final double HI = 0.1;
    
    private List<Haushalt> typeBPartners = new ArrayList<>(); // employment
    
    private boolean workerNeedsToBeFired = false;
    
    private double profit = 0;
    
    public Unternehmen(double liquiditaet) {
		this.liquiditaet = liquiditaet;
	}
    
    
    //put it there because the descriptionof what the firms do come after the description of what the people do
    @ScheduledMethod(start = 1, interval = 1, priority = 0)
    public void dayStep() {
    	//each firm produces according to the production function
    	double numberOfWorkers = typeBPartners.size();
    	
    	inventar += LAMBDA * numberOfWorkers;
    }

    
    
    
    
    @ScheduledMethod(start = 1, interval = 21, priority = -3)
    public void beginningOfMonth() {
    	//fire people if you have to do so because of last periods
    	if(workerNeedsToBeFired) {
    		fireRandomWorker();
    		workerNeedsToBeFired = false;
    	}
    	
    	if (consecutiveMonthsAllJobsFilled == 0) {
    		//im letzen monat wurde niemand eingestellt obwohl es offene stellen gibt
            adjustWage(true); // increase wage
        } else if (consecutiveMonthsAllJobsFilled >= GAMMA ) {
            adjustWage(false); // decrease wage
        }
    	
    	
    	
    	
    	//adjust number of employees and price
    	
    	double upperBarrierInventory = UPPER_PHI_INVENTORIES * nachfrageLetzterMonat;
    	double lowerBarrierInventory = LOWER_PHI_INVENTORIES * nachfrageLetzterMonat;
    	
    	
    	marginaleKosten = gehalt / (LAMBDA * 21.0);
    	double upperBarrierPrice = UPPER_PHI_PRICE * marginaleKosten;
    	double lowerBarrierPrice = LOWER_PHI_PRICE * marginaleKosten;
    	
    	
    	
    	if(inventar > upperBarrierInventory) {
    		//fire randomly chosen worker in next month
    		workerNeedsToBeFired = true;
    		if(preis > lowerBarrierPrice) {
    			//decrease price with probability Thita
    			if (RandomHelper.nextDouble() < THETA) {
    				adjustPrice(false); //decrease prce
    			}
    		}
    	}else if(inventar <= lowerBarrierInventory) {
    		System.out.println(lowerBarrierInventory);
    		//create new position to raise production
    		openPositions++;
    		//addTypeBPartner(new Haushalt()); //TODO fix this to not add a new household but to create opportunity for households to apply
    		if(preis < upperBarrierPrice) {
    			//increase price with probability Thita
    			if (RandomHelper.nextDouble() < THETA) {
    				adjustPrice(true); //increase price
    			}
    		}
    	}
    	
    	//reset Nachfrage letzter Monat to 0 after new Month has started
    	
    	nachfrageLetzterMonat = 0;
    	
    	
    }
    
    @ScheduledMethod(start=1, interval=21, priority= 1)
    public void finalizeMonth() {
        //pay wages, build buffer for bad times, pay profits
    	if( gehalt * typeBPartners.size() <= liquiditaet) {
    		// genug geld um arbeiter zu bezahlen
    		liquiditaet -= gehalt * typeBPartners.size();
    		for(Haushalt h : typeBPartners) {
    			h.empfangeGehalt(gehalt);
    		}
    		//try to do a liquiditaet buffer
    		double expectedliquidityBuffer = HI * gehalt * typeBPartners.size();
    		profit = Math.max(0, liquiditaet - expectedliquidityBuffer);
    		liquiditaet -= profit;
    		SessionManager.allocateProfits(profit);
    	}else {
    		//firm does not have enough money to pay wages - wage cuts are needed
    		double kriesenGehalt = liquiditaet / typeBPartners.size();
    		for(Haushalt h : typeBPartners) {
    			liquiditaet -= kriesenGehalt;
    			h.empfangeGehalt(kriesenGehalt);
    		}
    		liquiditaet = 0; // after distributing all liquidity - to make sure no stupid results
    		gehalt = kriesenGehalt;
    	}
    	
    	if(liquiditaet < 0 ) {
    		System.out.print("liquiditaet is negative");
    	}
    	
    	
    	//setze die Variable durchgehende Einstellungsmonate
    	if(openPositions == 0) {
    		consecutiveMonthsAllJobsFilled++;
    	}else{
    		//keine Einstellung diesen Monat
    		consecutiveMonthsAllJobsFilled = 0;
    	}
    	
    	
    	
    }
    
    
    private Haushalt fireRandomWorker() {
    	if (typeBPartners == null || typeBPartners.isEmpty()) return null;

    	int index = RandomHelper.nextIntFromTo(0, typeBPartners.size() - 1);
    	typeBPartners.get(index).notifyFired();
    	Haushalt h = typeBPartners.remove(index);
    	typeBPartners.removeIf(Objects::isNull);

        return h;
		
	}


	/*
     * decrease wage if increase is false else increase it
     */
    public void adjustWage(boolean increase) {
        double mu = RandomHelper.nextDouble() * DELTA; // μᵢ ∈ [0, δ)
        double newWage;

        if (increase) {
            newWage = gehalt * (1.0 + mu);
        } else {
            newWage = gehalt * (1.0 - mu);
        }

        // Ensure the new wage does not fall below the minimum wage
        if (newWage < MINIMUM_WAGE) {
            gehalt = MINIMUM_WAGE;
        } else {
            gehalt = newWage;
        }
    }

    /*
     * decrease price if increase is false, else increase it
     */
    public void adjustPrice(boolean increase) {
        double vu = RandomHelper.nextDouble() * theta;  // vᵢ ∈ [0, ϑ)
        double newPrice;

        if (increase) {
            newPrice = preis * (1.0 + vu);
        } else {
            newPrice = preis * (1.0 - vu);
        }

        // Ensure the new price doesn't drop below the minimum price
        if (newPrice < MINIMUM_PRICE) {
            preis = MINIMUM_PRICE;
        } else {
            preis = newPrice;
        }
    }
    
    //===============================Getter/Setter===========================================
    


    public void addTypeBPartner(Haushalt f) {
        typeBPartners.add(f);
    }

    public List<Haushalt> getTypeBPartners() {
        return typeBPartners;
    }


	public int getNumberOfWorkers() {
		return typeBPartners.size();
	}
	
	public double getPreis() {
		return preis;
	}
	
	public boolean getOpenPosition() {
		//TODO figure out some logic for hiring people Done
		return openPositions > 0;
	}
	
	public double getGehalt() {
		return gehalt;
	}
	
	public double getInventar() {
		return inventar;
	}
	
	public int getOpenPositions() {
		return openPositions;
	}


	public void empfangeBewerbungAufArbeit(Haushalt haushalt) {
		if(openPositions > 0) {
			//if you have open positions hire worker
			openPositions = openPositions - 1;
			addTypeBPartner(haushalt);
			haushalt.notifyHired(this);
		}
	}


	/**
	 * Attempts to fulfill a customer's purchase request based on their planned consumption spending.
	 * The method calculates the quantity of goods the customer wants to buy, and checks whether the
	 * firm's current inventory can meet that demand. If the inventory is sufficient, the full quantity
	 * is sold. Otherwise, the firm sells all remaining inventory.
	 *
	 * @param plannedConsumptionSpending the amount of money the customer is willing to spend
	 * @return the actual amount of product the customer got
	 */
	public double attemptPurchaseForAmount(double plannedConsumptionSpending) {
	    double wantedInventory = plannedConsumptionSpending / preis;
	    nachfrageLetzterMonat += wantedInventory;
	    
	    double quantitySold;
	    if (wantedInventory <= inventar) {
	        quantitySold = wantedInventory;
	    } else {
	        quantitySold = inventar;
	    }

	    inventar -= quantitySold;
	    //TODO make one variable
	    liquiditaet += quantitySold * preis; 
	    
	    
	    return quantitySold;
	}
	
	public double getNachfrageLetzterMonat() {
		return nachfrageLetzterMonat;
	}
	public double getLiquiditaet() {
		return liquiditaet;
	}

	/**
	 * notifies the Unternehmen that a household is quitting
	 * @param haushalt haushalt that is quitting
	 */
	public void notifyQuitting(Haushalt haushalt) {
		typeBPartners.remove(haushalt);
		typeBPartners.removeIf(Objects::isNull);
	}
	
    
    
    
	
}



