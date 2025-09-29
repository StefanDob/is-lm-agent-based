package islm.agenten;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;

import java.util.*;

import islm.SessionManager;

/**
 * {@code Unternehmen} represents a firm in the agent-based model.
 *
 * <p>Firms hire workers, produce goods, adjust wages and prices, 
 * and distribute profits to households. They react adaptively 
 * to changes in demand and inventory levels by opening/closing 
 * positions, firing workers, and adjusting prices or wages.</p>
 *
 * <h3>Key Features:</h3>
 * <ul>
 *   <li>Production: output is proportional to the number of workers.</li>
 *   <li>Labor market: firms post vacancies, hire, and sometimes fire workers.</li>
 *   <li>Pricing: firms adjust prices depending on inventory levels and marginal costs.</li>
 *   <li>Wages: adjusted upward if vacancies remain unfilled, downward if persistent overstaffing occurs.</li>
 *   <li>Profits: after paying wages and maintaining liquidity buffers, excess profits are distributed to households.</li>
 * </ul>
 */
public class Unternehmen {
	
	/** Current liquidity (money reserves). */
	private double liquiditaet;
	
	/** Current product price (per unit). */
	private double preis = 1;
	
	/** Current wage per worker. */
    private double gehalt = 1; 
    
    /** Current inventory (units of goods in stock). */
    private int inventar = 1; 
    
    /** Demand observed during the last month. */
    private double nachfrageLetzterMonat = 0; 
    
    /** Marginal cost of production (wage cost per unit). */
    private double marginaleKosten = 0;
    
    /** Number of consecutive months with all jobs filled. */
    private int consecutiveMonthsAllJobsFilled = 0; 
    
    /** Number of open job positions waiting for workers. */
    private int openPositions = 0;
    
    
    private static final int GAMMA = 24; // Threshold for wage decreases
    private static final double DELTA = 0.019;// Wage adjustment factor
    private static final double UPPER_PHI_INVENTORIES = 1.0;
    private static final double LOWER_PHI_INVENTORIES = 0.25;
    private static final double MINIMUM_WAGE = 1.0;
    private static final double MINIMUM_PRICE = 1.0;
    
    private static final double UPPER_PHI_PRICE = 120;
    private static final double LOWER_PHI_PRICE = 1.025;
    private static final double LAMBDA = 3.0;// Production multiplier (productivity) per worker per day
    
    private static final double THETA = 0.75; // Probability of adjusting price
    private static final double theta = 0.02;// Magnitude of random price adjustment
    private static final double HI = 0.1;// Liquidity buffer share
    
    /** List of employed workers (households). */
    private List<Haushalt> typeBPartners = new ArrayList<>(); // employment
    
    /** Flag to fire a worker at the start of next month. */
    private boolean workerNeedsToBeFired = false;
    
    /** Last month’s profit (before distribution). */
    private double profit = 0;
    
    /**
     * Creates a new firm with an initial liquidity level.
     *
     * @param liquiditaet the firm’s starting liquidity
     */
    public Unternehmen(double liquiditaet) {
		this.liquiditaet = liquiditaet;
	}
    
    // ----------------------------------------------------------------------------------
    // Simulation Scheduling
    // ----------------------------------------------------------------------------------

    
    
    /**
     * Daily production step.
     *
     * <p>Executed once per simulation day. Output increases inventory
     * proportionally to the number of workers employed.</p>
     */    @ScheduledMethod(start = 1, interval = 1, priority = 0)
    public void dayStep() {
    	//each firm produces according to the production function
    	double numberOfWorkers = typeBPartners.size();
    	inventar += LAMBDA * numberOfWorkers;
    }

     /**
      * Monthly adjustment phase.
      *
      * <p>Executed once per simulated month. The firm decides whether to fire workers,
      * adjust wages, and adapt prices depending on inventory and demand conditions.</p>
      *
      * <ul>
      *   <li>If inventories are too high, a worker is scheduled to be fired and price may decrease.</li>
      *   <li>If inventories are too low, vacancies are created and price may increase.</li>
      *   <li>Wages are adjusted up if vacancies persist, down if all positions are filled for many months.</li>
      * </ul>
      */
    @ScheduledMethod(start = 1, interval = 21, priority = -3)
    public void beginningOfMonth() {
    	//fire people if you have to do so because of last periods
    	if(workerNeedsToBeFired) {
    		fireRandomWorker();
    		workerNeedsToBeFired = false;
    	}
    	
    	// Wage adjustment based on vacancy persistence
    	if (consecutiveMonthsAllJobsFilled == 0) {
            adjustWage(true); // increase wage
        } else if (consecutiveMonthsAllJobsFilled >= GAMMA ) {
            adjustWage(false); // decrease wage
        }
    	
    	// Inventory thresholds
    	double upperBarrierInventory = UPPER_PHI_INVENTORIES * nachfrageLetzterMonat;
    	double lowerBarrierInventory = LOWER_PHI_INVENTORIES * nachfrageLetzterMonat;
    	
    	marginaleKosten = gehalt / (LAMBDA * 21.0);
    	double upperBarrierPrice = UPPER_PHI_PRICE * marginaleKosten;
    	double lowerBarrierPrice = LOWER_PHI_PRICE * marginaleKosten;
    	
    	// Inventory too high → fire worker, maybe lower price
    	if(inventar > upperBarrierInventory) {
    		//fire randomly chosen worker in next month
    		workerNeedsToBeFired = true;
    		if(preis > lowerBarrierPrice) {
    			//decrease price with probability Thita
    			if (RandomHelper.nextDouble() < THETA) {
    				adjustPrice(false); //decrease prce
    			}
    		}
		// Inventory too low → open position, maybe raise price
    	}else if(inventar <= lowerBarrierInventory) {
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
    	
    	// Reset demand tracker
    	nachfrageLetzterMonat = 0;
    }
    
    /**
     * Monthly finalization phase.
     *
     * <p>Executed once per simulated month, after households act.
     * The firm pays wages, builds a liquidity buffer, and distributes profits
     * to households through the {@link SessionManager}.</p>
     *
     * <ul>
     *   <li>If liquidity is sufficient, wages are fully paid and profits distributed.</li>
     *   <li>If liquidity is insufficient, workers receive reduced wages (crisis wages).</li>
     * </ul>
     */
    @ScheduledMethod(start=1, interval=21, priority= 1)
    public void finalizeMonth() {
        //pay wages, build buffer for bad times, pay profits
    	if( gehalt * typeBPartners.size() <= liquiditaet) {
    		// Pay full wages
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
    	
    	
    	// Update job-fill counter
    	if(openPositions == 0) {
    		consecutiveMonthsAllJobsFilled++;
    	}else{
    		//keine Einstellung diesen Monat
    		consecutiveMonthsAllJobsFilled = 0;
    	}
    }
    
    // ----------------------------------------------------------------------------------
    // Internal Logic
    // ----------------------------------------------------------------------------------
    
    /** Fires a random worker, if any exist. */
    private Haushalt fireRandomWorker() {
    	if (typeBPartners == null || typeBPartners.isEmpty()) return null;
    	int index = RandomHelper.nextIntFromTo(0, typeBPartners.size() - 1);
    	typeBPartners.get(index).notifyFired();
    	Haushalt h = typeBPartners.remove(index);
    	typeBPartners.removeIf(Objects::isNull);
        return h;
	}


    /**
     * Adjusts wages upward or downward within bounds.
     *
     * @param increase {@code true} to increase wages, {@code false} to decrease
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

    /**
     * Adjusts the product price upward or downward within bounds.
     *
     * @param increase {@code true} to increase price, {@code false} to decrease
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
    

    // ----------------------------------------------------------------------------------
    // Market Interactions
    // ----------------------------------------------------------------------------------
    /**
     * Handles a customer’s purchase request.
     *
     * @param plannedConsumptionSpending the amount of money the customer is willing to spend
     * @return the actual quantity of goods purchased
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
	    liquiditaet += quantitySold * preis; 
	    
	    
	    return quantitySold;
	}
	
	/**
     * Processes a job application from a household.
     * If vacancies exist, the household is hired and linked to this firm.
     *
     * @param haushalt the applying household
     */
	public void empfangeBewerbungAufArbeit(Haushalt haushalt) {
		if(openPositions > 0) {
			//if you have open positions hire worker
			openPositions = openPositions - 1;
			addTypeBPartner(haushalt);
			haushalt.notifyHired(this);
		}
	}
	
	/**
	 * notifies the Unternehmen that a household is quitting
	 * @param haushalt haushalt that is quitting
	 */
	public void notifyQuitting(Haushalt haushalt) {
		typeBPartners.remove(haushalt);
		typeBPartners.removeIf(Objects::isNull);
	}
	
    //===============================Getter/Setter===========================================
    
    public void addTypeBPartner(Haushalt f) {
        typeBPartners.add(f);
    }

	public double getNachfrageLetzterMonat() {
		return nachfrageLetzterMonat;
	}
	public double getLiquiditaet() {
		return liquiditaet;
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
}



