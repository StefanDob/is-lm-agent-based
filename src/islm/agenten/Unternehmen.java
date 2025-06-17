package islm.agenten;

import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;

import java.util.*;

import islm.SessionManager;

public class Unternehmen {
	private double liquiditaet;
	private double preis = 0;
    private double gehalt = 0;; 
    private int inventar = 0; //keeping track of it product and not in money
    
    private double nachfrageLetzterMonat = 0; //TODO make sure this gets set
    private double marginaleKosten = 0;
    
    private int durchgehendeEinstellungsmonate = 0; //anzahl an monaten in denen durchgehend leute eingestellt wurden
    
    private static final int GAMMA = 3; //anzahl an aufeinanderfolgenden monaten in denen konsequent leute eingestellt wurden
    private static final double DELTA = 0.019; //boundries of distribution to increase wage
    private static final double UPPER_PHI_INVENTORIES = 1.0;
    private static final double LOWER_PHI_INVENTORIES = 0.25;
    
    private static final double UPPER_PHI_PRICE = 1.15;
    private static final double LOWER_PHI_PRICE = 0.025;
    private static final double LAMBDA = 3;
    
    private static final double THETA = 0.75; //propability of changing price if inventory is not in bounds
    private static final double theta = 0.02;
    private static final double HI = 0.1;
    
    private List<Unternehmen> typeAPartners = new ArrayList<>(); //buy consumption goods 
    private List<Haushalt> typeBPartners = new ArrayList<>(); // employment
    
    private boolean workerNeedsToBeFired = false;
    
    
    //TODO check wether LAstpriority fits here; i only put it there because the descriptionof what the firms do come after the description of what the people do
    @ScheduledMethod(start = 1, interval = 1, priority = ScheduleParameters.LAST_PRIORITY)
    public void dayStep() {
    	//each firm produces according to the production function
    	double numberOfWorkers = typeBPartners.size();
    	inventar += LAMBDA * numberOfWorkers;
    }

    
    
    
    
    @ScheduledMethod(start = 1, interval = 21, priority = ScheduleParameters.FIRST_PRIORITY)
    public void beginningOfMonth() {
    	//fire people if you have to do so because of last periods
    	if(workerNeedsToBeFired) {
    		fireRandomWorker();
    	}
    	
    	//adjust wages based on months without hiring
    	if (durchgehendeEinstellungsmonate == 0) {
    		//im letzen monat wurde niemand eingestellt
            adjustWage(true); // increase wage
        } else if (durchgehendeEinstellungsmonate >= GAMMA ) {
            adjustWage(false); // decrease wage
        }
    	
    	//adjust number of employees and price
    	
    	double upperBarrierInventory = UPPER_PHI_INVENTORIES * nachfrageLetzterMonat;
    	double lowerBarrierInventory = LOWER_PHI_INVENTORIES * nachfrageLetzterMonat;
    	
    	double upperBarrierPrice = UPPER_PHI_PRICE * marginaleKosten;
    	double lowerBarrierPrice = LOWER_PHI_PRICE * marginaleKosten;
    	
    	
    	if(inventar > upperBarrierInventory) {
    		//fire randomly chosen worker in next month
    		workerNeedsToBeFired = true;
    		if(preis > upperBarrierPrice) {
    			//decrease price with probability Thita
    			if (RandomHelper.nextDouble() < THETA) {
    				adjustPrice(false); //decrease prce
    			}
    		}
    	}else if(inventar < lowerBarrierInventory) {
    		//create new position to raise production
    		addTypeBPartner(new Haushalt()); //TODO fix this to not add a new household but to create opportunity for households to apply
    		if(preis > upperBarrierPrice) {
    			//increase price with probability Thita
    			if (RandomHelper.nextDouble() < THETA) {
    				adjustPrice(true); //increase price
    			}
    		}
    		
    	}
    	
    	
    }
    
    @ScheduledMethod(start=1, interval=21, priority=ScheduleParameters.LAST_PRIORITY)
    public void finalizeMonth() {
        //pay wages
    	if( gehalt * typeBPartners.size() <= liquiditaet) {
    		// genug geld um arbeiter zu bezahlen
    		liquiditaet -= gehalt * typeBPartners.size();
    		for(Haushalt h : typeBPartners) {
    			h.empfangeGehalt(gehalt);
    		}
    		//try to do a liquiditaet buffer
    		double expectedliquidityBuffer = HI * gehalt * typeBPartners.size();
    		double profit = Math.max(0, liquiditaet - expectedliquidityBuffer);
    		
    		SessionManager.allocateProfits(profit);
    	}else {
    		//firm does not have enough money to pay wages - wage cuts are needed
    		double kriesenGehalt = liquiditaet / typeBPartners.size();
    		for(Haushalt h : typeBPartners) {
    			h.empfangeGehalt(kriesenGehalt);
    		}
    	}
    	
    	
    	//build buffer for bad times
    	
    	//pay profits
    }
    
    
    private Haushalt fireRandomWorker() {
    	if (typeBPartners == null || typeBPartners.isEmpty()) return null;

    	int index = RandomHelper.nextIntFromTo(0, typeBPartners.size() - 1);
        return typeBPartners.remove(index);
		
	}


	/*
     * decrease wage if increase is false else increase it
     */
    public void adjustWage(boolean increase) {
        double mu = RandomHelper.nextDouble() * DELTA;  // μᵢ ∈ [0, δ)

        if (increase) {
            gehalt *= (1.0 + mu);
        } else {
        	gehalt *= (1.0 - mu);
        }
    }

    /*
     * decrease price if increase is false, else increase it
     */
    public void adjustPrice(boolean increase) {
        double vu = RandomHelper.nextDouble() * theta;  // vᵢ ∈ [0, ϑ)

        if (increase) {
            preis *= (1.0 + vu);
        } else {
            preis *= (1.0 - vu);
        }
    }
    
    //===============================Getter/Setter===========================================
    
    
    public void addTypeAPartner(Unternehmen f) {
        typeAPartners.add(f);
    }

    public void addTypeBPartner(Haushalt f) {
        typeBPartners.add(f);
    }

    public List<Unternehmen> getTypeAPartners() {
        return typeAPartners;
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
		//TODO figure out some logic for hiring people
		return true;
	}
	
	public double getGehalt() {
		return gehalt;
	}
	
	public double getInventar() {
		return inventar;
	}


	public void empfangeBewerbungAufArbeit(Haushalt haushalt) {
		// TODO Auto-generated method stub 
		
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

	
    
    
    
	
}



