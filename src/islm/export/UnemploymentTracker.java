package islm.export;

import java.util.*;

import repast.simphony.engine.schedule.ScheduledMethod;

/**
 * Tracks unemployment levels in the simulation over time.
 * 
 * <p>This class maintains two lists of unemployment counts:
 * <ul>
 *   <li>{@code unemploymentMonat} – daily unemployment counts within the current month</li>
 *   <li>{@code unemploymentSemianual} – daily unemployment counts within the current semi year (6 months)</li>
 * </ul>
 * </p>
 * 
 * <p>Scheduled methods handle automatic tracking and periodic resets:
 * <ul>
 *   <li>{@code dailyTracking()} – records daily unemployment counts</li>
 *   <li>{@code monthlyReset()} – clears monthly unemployment records at the end of each month</li>
 *   <li>{@code semianualReset()} – clears quarterly unemployment records every 6 months</li>
 * </ul>
 * </p>
 */
public class UnemploymentTracker {
	
	List<Integer> unemploymentMonat = new ArrayList<>();
	
	List<Integer> unemploymentSemianual = new ArrayList<>();
	
	
	
	
    public List<Integer> getUnemploymentMonat() {
        return unemploymentMonat;
    }
    
    public List<Integer> getUnemploymentQuarter() {
        return unemploymentSemianual;
    }

    
    public void setUnemployment(List<Integer> unemploymentMonat) {
        this.unemploymentMonat = unemploymentMonat;
    }
    
    @ScheduledMethod(start = 1, interval = 1, priority = 2)
    public void dailyTracking() {
    	unemploymentMonat.add(1000 - DataCollecter.getEmployedCount());
    	unemploymentSemianual.add(1000 - DataCollecter.getEmployedCount());
    }
    
    @ScheduledMethod(start = 1, interval = 21, priority = 4)
    public void monthlyReset() {
    	unemploymentMonat.clear();
    }
    
    @ScheduledMethod(start = 1, interval = 21*6, priority = 4)
    public void semianualReset() {
    	unemploymentSemianual.clear();
    }
    
    
    

}
