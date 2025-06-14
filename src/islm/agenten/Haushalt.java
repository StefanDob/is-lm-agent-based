package islm.agenten;


import java.util.ArrayList;
import java.util.List;

import islm.SessionManager;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;

public class Haushalt {
	
	private double liquiditaet; // mh
	
	private double reservationsGehalt; //wh
	
	private List<Unternehmen> consumptionsFirms = new ArrayList<>(); //Type a Verbindungen
	
	private Unternehmen arbeitGeber; //typ b verbindung
	
	private static final double PSI_PRICE = 0.25;
	
	//this method is not being called automatically by repast but is being called from the islm builder in order to
	//ensure that the households are getting called in random order
    public void beginningOfMonth() {
    	//household searches for cheapest place to buy stuff
    	
    	if (!consumptionsFirms.isEmpty() && RandomHelper.nextDouble() < PSI_PRICE) {
    		//first pick within firms that you have type a connections with
    		int index = RandomHelper.nextIntFromTo(0, consumptionsFirms.size() - 1);
    	    Unternehmen picked = consumptionsFirms.get(index);
    	    
    	    //now pick one from the firms you have no connections with
    	    
    	}
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
     * @param unternehmen the list of firms to choose from
     * @return a randomly selected firm based on worker-weighted probability,
     *         or {@code null} if the total number of workers is zero
     */
    public Unternehmen pickFirmProportionalToWorkers(List<Unternehmen> unternehmen) {
        // Step 1: compute total weight
        int totalWorkers = unternehmen.stream().mapToInt(Unternehmen::getNumberOfWorkers).sum();
        if (totalWorkers == 0) return null; // no selection possible

        // Step 2: generate a random value in [0, totalWorkers)
        double r = RandomHelper.nextDoubleFromTo(0, totalWorkers);

        // Step 3: iterate and subtract weights until we cross r
        double cumulative = 0.0;
        for (Unternehmen u : unternehmen) {
            cumulative += u.getNumberOfWorkers();
            if (r < cumulative) {
                return u;
            }
        }

        // Fallback (shouldn't happen unless rounding issues)
        return unternehmen.get(unternehmen.size() - 1);
    }
	
    
}