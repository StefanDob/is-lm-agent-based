package islm.agenten;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import islm.DemandConstraint;
import islm.SessionManager;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;

/**
* The {@code Haushalt} class models the behavior of a household (agent) in the simulation.
* 
* A household has:
* - liquidity (money available),
* - a reservation wage (the minimum wage it accepts),
* - connections to firms (for consumption and employment),
* - and decision rules for consumption, job search, and adaptation.
*
* The household consumes goods daily, searches for better consumption options and jobs
* monthly, and adjusts its economic expectations over time.
*
* Key dynamics include:
* - Daily consumption from firms.
* - Monthly reevaluation of consumption firms and job search.
* - Income handling from wages, profits, and transfers.
* - Adaptive reservation wage and consumption planning.
*/
public class Haushalt {
	
	/** Available liquidity (money held by the household). */
	private double liquiditaet; // mh
	
	/** Reservation wage (minimum acceptable wage). */
	private double reservationsGehalt = 0; //wh
	
	/** Current wage from employer. */
	private double aktuellesGehalt = 0;
	
	/** List of firms (Unternehmen) the household buys consumption goods from (Type A connections). */
	private List<Unternehmen> consumptionsFirms = new ArrayList<>(); //Type a Verbindungen
	
	 /** List of demand constraints faced in the last period (unsatisfied demand). */
	private List<DemandConstraint> lastPeriodsDemandConstraints = new ArrayList<>();

	/** Employer of the household (Type B connection). */
	private Unternehmen arbeitGeber; //typ b verbindung
	
	/** Planned monthly consumption in goods (not money). */
	private double perMonthConsumption; //consumption per month: this is given in goods and not in money
	
	/** Fraction of unmet demand in the last day (0 = fully satisfied, 1 = completely unsatisfied). */
	private double unmetDemandRatio = 1;
	
	/** Total income earned this month (after taxes). */
	private double aktuellesEinkommen = 0.0;
	
	private static final double PSI_PRICE = 0.25; // Probability of switching consumption firm due to price
	private static final double PSI_QUANT = 0.25; // Probability of switching firm due to demand constraints
	private static final double XI = 0.01; // Price improvement threshold
	private static final int BETA = 5; // Number of firms checked for job search
	private static final double PI = 0.1; // Probability of random job search
	private static final double ALPHA = 0.9; // Utility parameter for consumption planning
	private static final double N = 7; // Maximum number of firms tried per day for consumption
	
	/**
     * Constructor to initialize a household with liquidity and initial consumption firms.
     *
     * @param liquiditaet initial money available
     * @param consumptionsFirms list of firms household consumes from
     */
	public Haushalt(double liquiditaet,List<Unternehmen> consumptionsFirms) {
		this.liquiditaet = liquiditaet;
		this.consumptionsFirms = consumptionsFirms;
	}
	
	
	/**
     * Simulates one day of the household.
     * - Attempts daily consumption.
     * - Registers unmet demand if goods cannot be purchased.
     * - Looks for jobs if unemployed.
     */
	public void dayStep() {
		//daily consumption
		double satisfiedConsumption = 0;
		double plannedDailyDemandInGoods = perMonthConsumption / 21;
		double minSatisfaction = plannedDailyDemandInGoods * 0.95;

		// Attempt daily consumption from up to N firms
		for(int i = 1; i <= N && satisfiedConsumption < minSatisfaction ; i++) {
			Unternehmen u = SessionManager.getRandomUnternehmen();
			double expectedCostForConsumption = u.getPreis() * plannedDailyDemandInGoods;
			double plannedConsumptionSpending;
			
			// Decide how much to spend
			if(expectedCostForConsumption < liquiditaet) {
				//I have enough money to buy it so i will try to buy it
				plannedConsumptionSpending = expectedCostForConsumption;
			}else {
				
				plannedConsumptionSpending = liquiditaet;
			}
			
			// Attempt to purchase
			double quantityBought = u.attemptPurchaseForAmount(plannedConsumptionSpending);
			
			// If purchase was constrained, record demand restriction
			if(quantityBought * u.getPreis() < plannedConsumptionSpending) {
				//register a demand constraint
				lastPeriodsDemandConstraints.add(new DemandConstraint(u, plannedConsumptionSpending - quantityBought * u.getPreis()));
			}
			
			// Deduct spent liquidity
			liquiditaet -= quantityBought * u.getPreis();
			
			if(liquiditaet < 0) { //due to rounding etc.
				liquiditaet = 0;
			}
			satisfiedConsumption += quantityBought;
		}
		
		// Compute unmet demand ratio
		if (plannedDailyDemandInGoods > 0) {
		    unmetDemandRatio = Math.max(0, 1 - (satisfiedConsumption / plannedDailyDemandInGoods));
		    
		} else {
		    unmetDemandRatio = 0.0;
		}
		
		//if you are jobless look for jobs daily //TODO
		
		if(arbeitGeber == null) {
    		//if you are unemployed check BETA firms, to find new employment
    		for(int i = 1; i <= BETA; i++) {
    			Unternehmen u = SessionManager.getRandomUnternehmen();
    			if(u.getOpenPosition() && u.getGehalt() >= reservationsGehalt) {
    				acceptPositionAt(u);
    				if(arbeitGeber != null) break;
    			}
    		}
    	}
	}
	
	/**
     * Called at the beginning of each month.
     * - Household may change consumption firms based on price or demand issues.
     * - Performs job search (depending on employment status and reservation wage).
     * - Plans monthly consumption budget.
     */
    public void beginningOfMonth() {
    	
    	// Price-based switching
    	if (!consumptionsFirms.isEmpty() && RandomHelper.nextDouble() < PSI_PRICE) {
    		//first pick within firms that you have type a connections with
    		int index = RandomHelper.nextIntFromTo(0, consumptionsFirms.size() - 1);
    	    Unternehmen pickedToReplace = consumptionsFirms.get(index);
    	    
    	    //now pick one from the firms you have no connections with
    	    
    	    
    	    Unternehmen newPick = pickFirmProportionalToWorkers();
    	    
    	    
    	    //replace unternehmen if it makes sense
    	    if(newPick != null && pickedToReplace.getPreis() < newPick.getPreis() * (1-XI)) {
    	    	consumptionsFirms.remove(pickedToReplace);
    	    	consumptionsFirms.add(newPick);
    	    	consumptionsFirms.removeIf(Objects::isNull);
    	    }
    	}

    	// Demand-based switching (Demans constraints
    	if (!lastPeriodsDemandConstraints.isEmpty() && RandomHelper.nextDouble() < PSI_QUANT) {
    		Unternehmen selectedCompanyWithDemandIssues = pickFirmProportionalToDemandRestriction();
    		Unternehmen newPick = pickFirmProportionalToWorkers();
    		consumptionsFirms.remove(selectedCompanyWithDemandIssues);
	    	consumptionsFirms.add(newPick);
	    	consumptionsFirms.removeIf(Objects::isNull);
    	}
    	
    	//Job search logic
    	if(arbeitGeber == null) {
    		//if you are unemployed check BETA firms, to find new employment
    		for(int i = 1; i <= BETA; i++) {
    			Unternehmen u = SessionManager.getRandomUnternehmen();
    			if(u.getOpenPosition() && u.getGehalt() >= reservationsGehalt) {
    				acceptPositionAt(u);
    				if(arbeitGeber != null) break;
    			}
    		}
			
    	}else if(aktuellesGehalt >= reservationsGehalt) {
    		//Employee is happily working however he might still check with probability pi (not the circle thing) for better jobs
    		if(RandomHelper.nextDouble() < PI) {
    			Unternehmen u = SessionManager.getRandomUnternehmen();
    			if(u.getOpenPosition() && u.getGehalt() >= aktuellesGehalt) {
    				acceptPositionAt(u);
    			}
    		}
    	}else if(aktuellesGehalt < reservationsGehalt) {
    		//if you are getting less then your reservationsGehalt always search for at least one position
    		Unternehmen u = SessionManager.getRandomUnternehmen();
			if(u.getOpenPosition() && u.getGehalt() >= aktuellesGehalt) {
				acceptPositionAt(u);
			}
    	}
    	
    	// Consumption planning
    	double ph = computeAverageCostOfAllConsumptionGoods();
    	
    	perMonthConsumption = Math.min(Math.pow(liquiditaet / ph, ALPHA), liquiditaet / ph);
    	
    	
    	// reset demand constraints
    	lastPeriodsDemandConstraints.clear();
    }
    
    
    /**
     * Monthly cleanup:
     * - If unemployed or underpaid, lowers reservation wage by 10%.
     */
    @ScheduledMethod(start=1, interval=21, priority= 1)
    public void finalizeMonth() {
    	if(arbeitGeber == null || arbeitGeber.getGehalt() < reservationsGehalt) {
    		reservationsGehalt = reservationsGehalt * 0.9;
    	}
    }
    
    /**
     * Resets monthly trackers (e.g., income).
     */
    @ScheduledMethod(start=1, interval=21, priority= 4)
    public void clearTrackers() {
    	aktuellesEinkommen = 0;
    }
    
    
    
    /**
     * Computes the average price of all consumption goods from the type A connected firms.
     *
     * @return the average price, or 0.0 if the list is empty (fallback default)
     */
    private double computeAverageCostOfAllConsumptionGoods() {
        double sumOfPrices = 0;
        for (Unternehmen u : consumptionsFirms) {
            sumOfPrices += u.getPreis();
        }

        return sumOfPrices / consumptionsFirms.size();
    }


    /**
     * Accepts a job position at a given firm.
     *
     * @param u the firm offering the job
     */
	private void acceptPositionAt(Unternehmen u) {
		u.empfangeBewerbungAufArbeit(this);
		aktuellesGehalt = u.getGehalt();		
	}



	/**
     * Randomly selects a firm from the given list with probability proportional
     * to the number of workers in each firm.
     * <p>
     * This performs a weighted random selection, where each firm's weight is 
     * determined by its current number of employees (via {@code getNumberOfWorkers()}).
     * Firms with more workers are more likely to be chosen.
     * </p>
     *
     * 
     * @return a randomly selected firm based on worker-weighted probability,
     *         or {@code null} if the total number of workers is zero
     */
    public Unternehmen pickFirmProportionalToWorkers() {
    	List<Unternehmen> firmsWithNoConnection = new ArrayList<>(SessionManager.getUnternehmenListe());
	    firmsWithNoConnection.removeAll(consumptionsFirms);
        // Step 1: compute total weight
        int totalWorkers = firmsWithNoConnection.stream().mapToInt(Unternehmen::getNumberOfWorkers).sum();
        if (totalWorkers == 0) return null; // no selection possible

        // Step 2: generate a random value in [0, totalWorkers)
        double r = RandomHelper.nextDoubleFromTo(0, totalWorkers);

        // Step 3: iterate and subtract weights until we cross r
        double cumulative = 0.0;
        for (Unternehmen u : firmsWithNoConnection) {
            cumulative += u.getNumberOfWorkers();
            if (r < cumulative) {
                return u;
            }
        }

        // Fallback (shouldn't happen unless rounding issues)
        return firmsWithNoConnection.get(firmsWithNoConnection.size() - 1);
    }
	
    /**
     * Randomly selects a firm (Unternehmen) from the list of last period's demand constraints,
     * with the probability of selecting each firm being proportional to the value of its
     * demand restriction.
     * <p>
     * This implements a weighted random selection where each {@code DemandConstraint} acts as a weight
     * source via {@code getRestriktion()}, and the associated {@code Unternehmen} is returned.
     * </p>
     *
     * @return a randomly selected {@code Unternehmen}, weighted by demand restriction,
     *         or {@code null} if the total weight is zero
     */
    public Unternehmen pickFirmProportionalToDemandRestriction() {
        // Step 1: compute total weight
        double totalRestriktion = lastPeriodsDemandConstraints.stream().mapToDouble(DemandConstraint::getRestriktion).sum();
        if (totalRestriktion == 0) return null; // no selection possible

        // Step 2: generate a random value in [0, totalWorkers)
        double r = RandomHelper.nextDoubleFromTo(0, totalRestriktion);

        // Step 3: iterate and subtract weights until we cross r
        double cumulative = 0.0;
        for (DemandConstraint dc : lastPeriodsDemandConstraints) {
            cumulative += dc.getRestriktion();
            if (r < cumulative) {
                return dc.getUnternehmen();
            }
        }

        // Fallback (shouldn't happen unless rounding issues)
        return lastPeriodsDemandConstraints.get(lastPeriodsDemandConstraints.size() - 1).getUnternehmen();
    }
    
    //---Income---
    
    /**
     * Receive salary and update liquidity and reservation wage.
     */
	public void empfangeGehalt(double gehalt) {
		aktuellesEinkommen += gehalt;
		liquiditaet += gehalt;
		
		//sollte das Gehalt größer sein, als das Reservationsgehalt wird das reservationsgehalt hochgesetzt
		if(gehalt > reservationsGehalt) {
			reservationsGehalt = gehalt;
		}
	}
	
	/**
     * Receive profit income.
     */
	public void empfangeProfit(double profit) {
		aktuellesEinkommen += profit;
		liquiditaet += profit;
	}
	
	//---Employment---
	
	/** Notifies the household that it has been fired. */
	public void notifyFired() {
		arbeitGeber = null;
	}
	
	/** Notifies the household that it has been hired. */
	public void notifyHired(Unternehmen unternehmen) {
		//first notify the old Arbeitgeber that you got hired if necessary and that you thereby Quit
		if(arbeitGeber != null) {
			arbeitGeber.notifyQuitting(this);
		}
		arbeitGeber = unternehmen;
	}
	
	//--- Getters----
	
	
	public double getLiquiditaet(){
		return liquiditaet;
	}
	
	public Unternehmen getArbeitGeber() {
		return arbeitGeber;
	}



	public double getUnmetDemandRatio() {
		return unmetDemandRatio;
	}
	
	public double getPerMonthKonsumption() {
		return perMonthConsumption;
	}
	
	public double getReservationsGehalt() {
		return reservationsGehalt;
	}

	public double getAktuellesGehalt() {
		return aktuellesGehalt;
	}
	
	public double getAktuellesEinkommen() {
		return aktuellesEinkommen;
	}
	

	



    
}