package islm.agenten;


import java.util.ArrayList;
import java.util.List;

import islm.DemandConstraint;
import islm.SessionManager;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;

public class Haushalt {
	
	private double liquiditaet; // mh
	
	private double reservationsGehalt; //wh
	
	private double aktuellesGehalt;
	
	private List<Unternehmen> consumptionsFirms = new ArrayList<>(); //Type a Verbindungen
	
	private List<DemandConstraint> lastPeriodsDemandConstraints = new ArrayList<>();

	
	private Unternehmen arbeitGeber; //typ b verbindung
	
	private double perMonthConsumption; //consumption per month: this is given in goods and not in money
	
	private static final double PSI_PRICE = 0.25;
	private static final double PSI_QUANT = 0.25;
	private static final double XI = 0.01;
	private static final int BETA = 5;
	private static final double PI = 0.1;
	private static final double ALPHA = 0.9;
	private static final double N = 7;
	
	
	
	public void dayStep() {
		//daily consumption
		double satisfiedConsumption = 0;
		double plannedDailyDemandInGoods = perMonthConsumption / 21;
		double minSatisfaction = plannedDailyDemandInGoods * 0.95;

		for(int i = 1; i <= N && satisfiedConsumption < minSatisfaction ; i++) {
			Unternehmen u = SessionManager.getRandomUnternehmen();
			double expectedCostForConsumption = u.getPreis() * plannedDailyDemandInGoods;
			double plannedConsumptionSpending;
			
			if(expectedCostForConsumption < liquiditaet) {
				//i have enough money to buy it so i will try to buy it
				plannedConsumptionSpending = expectedCostForConsumption;
			}else {
				
				plannedConsumptionSpending = liquiditaet;
			}
			
			double quantityBought = u.attemptPurchaseForAmount(plannedConsumptionSpending);
			
			liquiditaet -= quantityBought * u.getPreis();
			satisfiedConsumption += quantityBought;
		}
		
		
		
		
	}
	
	//this method is not being called automatically by repast but is being called from the islm builder in order to
	//ensure that the households are getting called in random order
    public void beginningOfMonth() {
    	
    	//household searches for cheapest place to buy stuff and may replace his old trading relations with new ones
    	if (!consumptionsFirms.isEmpty() && RandomHelper.nextDouble() < PSI_PRICE) {
    		//first pick within firms that you have type a connections with
    		int index = RandomHelper.nextIntFromTo(0, consumptionsFirms.size() - 1);
    	    Unternehmen pickedToReplace = consumptionsFirms.get(index);
    	    
    	    //now pick one from the firms you have no connections with
    	    
    	    
    	    Unternehmen newPick = pickFirmProportionalToWorkers();
    	    
    	    //replace unternehmen if it makes sense
    	    if(pickedToReplace.getPreis() < newPick.getPreis() * (1-XI)) {
    	    	consumptionsFirms.remove(pickedToReplace);
    	    	consumptionsFirms.add(newPick);
    	    }
    	}
    	//Now the household might replace companies that had demand constraints in last period
    	if (!lastPeriodsDemandConstraints.isEmpty() && RandomHelper.nextDouble() < PSI_QUANT) {
    		Unternehmen selectedCompanyWithDemandIssues = pickFirmProportionalToDemandRestriction();
    		Unternehmen newPick = pickFirmProportionalToWorkers();
    		consumptionsFirms.remove(selectedCompanyWithDemandIssues);
	    	consumptionsFirms.add(newPick);
    	}
    	
    	//Job search
    	
    	
    	if(arbeitGeber == null) {
    		//if you are unemployed check BETA firms, to find new employment
    		for(int i = 1; i <= BETA; i++) {
    			Unternehmen u = SessionManager.getRandomUnternehmen();
    			if(u.getOpenPosition() && u.getGehalt() >= reservationsGehalt) {
    				acceptPositionAt(u);
    				break;
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
    	
    	//plan your money supply: liquidity, consumption etc
    	double ph = computeAverageCostOfAllConsumptionGoods();
    	
    	perMonthConsumption = Math.min(Math.pow(liquiditaet / ph, ALPHA), liquiditaet / ph);)
    	
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



	private void acceptPositionAt(Unternehmen u) {
    	//TODO make sure this is right
		u.empfangeBewerbungAufArbeit(this);
		arbeitGeber = u;
		
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
    
}