package islm.agenten;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import islm.DemandConstraint;
import islm.SessionManager;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;

public class Haushalt {
	
	private double liquiditaet; // mh
	
	private double reservationsGehalt = 0; //wh
	
	private double aktuellesGehalt = 0;
	
	private List<Unternehmen> consumptionsFirms = new ArrayList<>(); //Type a Verbindungen
	
	private List<DemandConstraint> lastPeriodsDemandConstraints = new ArrayList<>();

	
	private Unternehmen arbeitGeber; //typ b verbindung
	
	private double perMonthConsumption; //consumption per month: this is given in goods and not in money
	
	private double unmetDemandRatio = 1;
	
	private static final double PSI_PRICE = 0.25;
	private static final double PSI_QUANT = 0.25;
	private static final double XI = 0.01;
	private static final int BETA = 5;
	private static final double PI = 0.1;
	private static final double ALPHA = 0.9;
	private static final double N = 7;
	
	
	public Haushalt(double liquiditaet,List<Unternehmen> consumptionsFirms) {
		this.liquiditaet = liquiditaet;
		this.consumptionsFirms = consumptionsFirms;
	}
	
	
	
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
				//I have enough money to buy it so i will try to buy it
				plannedConsumptionSpending = expectedCostForConsumption;
			}else {
				
				plannedConsumptionSpending = liquiditaet;
			}
			
			double quantityBought = u.attemptPurchaseForAmount(plannedConsumptionSpending);
			
			if(quantityBought * u.getPreis() < plannedConsumptionSpending) {
				//register a demand constraint
				lastPeriodsDemandConstraints.add(new DemandConstraint(u, plannedConsumptionSpending - quantityBought * u.getPreis()));
			}
			
			liquiditaet -= quantityBought * u.getPreis();
			
			if(liquiditaet < 0) { //due to rounding etc.
				liquiditaet = 0;
			}
			
			satisfiedConsumption += quantityBought;
		}
		
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
			//if it does not find anything it just takes any job:
			if(arbeitGeber == null) {
				for(Unternehmen u : SessionManager.getUnternehmenListe()) {
					if(u.getOpenPosition() ) {
						acceptPositionAt(u);
						if(arbeitGeber != null) break;
					}
			}
	    		
			}
			if(arbeitGeber == null) {
				System.out.println("Arbeitgeber is still sero");
			}
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
    	    if(newPick != null && pickedToReplace.getPreis() < newPick.getPreis() * (1-XI)) {
    	    	consumptionsFirms.remove(pickedToReplace);
    	    	consumptionsFirms.add(newPick);
    	    	consumptionsFirms.removeIf(Objects::isNull);
    	    }
    	}
    	//Now the household might replace companies that had demand constraints in last period
    	if (!lastPeriodsDemandConstraints.isEmpty() && RandomHelper.nextDouble() < PSI_QUANT) {
    		Unternehmen selectedCompanyWithDemandIssues = pickFirmProportionalToDemandRestriction();
    		Unternehmen newPick = pickFirmProportionalToWorkers();
    		consumptionsFirms.remove(selectedCompanyWithDemandIssues);
	    	consumptionsFirms.add(newPick);
	    	consumptionsFirms.removeIf(Objects::isNull);
    	}
    	
    	//Job search
    	
    	
    	if(arbeitGeber == null) {
    		//if you are unemployed check BETA firms, to find new employment
    		for(int i = 1; i <= BETA; i++) {
    			Unternehmen u = SessionManager.getRandomUnternehmen();
    			if(u.getOpenPosition() && u.getGehalt() >= reservationsGehalt) {
    				acceptPositionAt(u);
    				if(arbeitGeber != null) break;
    			}
    		}
			//if it does not find anything it just takes any job:
			if(arbeitGeber == null) {
				for(Unternehmen u : SessionManager.getUnternehmenListe()) {
					if(u.getOpenPosition() ) {
						acceptPositionAt(u);
						if(arbeitGeber != null) break;
					}
			}
	    		
			}
			if(arbeitGeber == null) {
				System.out.println("Arbeitgeber is still sero");
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
    	
    	perMonthConsumption = Math.min(Math.pow(liquiditaet / ph, ALPHA), liquiditaet / ph);
    	
    	
    	// as the new months jobs all have completed reset the demand constraints
    	lastPeriodsDemandConstraints.clear();
    }
    
    @ScheduledMethod(start=1, interval=21, priority= 1)
    public void finalizeMonth() {
    	//if you did not have work this month reduce reservation wage by 10 %
    	if(arbeitGeber == null) {
    		reservationsGehalt = reservationsGehalt * 0.9;
    	}
    	
    	
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

	public void empfangeGehalt(double gehalt) {
		liquiditaet += gehalt;
		
		//sollte das Gehalt größer sein, als das Reservationsgehalt wird das reservationsgehalt hochgesetzt
		if(gehalt > reservationsGehalt) {
			reservationsGehalt = gehalt;
		}
	}
	
	public void empfangeProfit(double profit) {
		liquiditaet += profit;
		
	}
	
	public double getLiquiditaet(){
		return liquiditaet;
	}

	//this method is getting called to notify the household it got fired
	public void notifyFired() {
		arbeitGeber = null;
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



	public void notifyHired(Unternehmen unternehmen) {
		//first notify the old Arbeitgeber that you got hired if necessary and that you thereby Quit
		if(arbeitGeber != null) {
			arbeitGeber.notifyQuitting(this);
		}
		arbeitGeber = unternehmen;
	}



    
}