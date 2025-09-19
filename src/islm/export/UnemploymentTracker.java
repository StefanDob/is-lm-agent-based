package islm.export;

import java.util.*;

import repast.simphony.engine.schedule.ScheduledMethod;

public class UnemploymentTracker {
	
	List<Integer> unemployment = new ArrayList<>();
	
	
	
	/**
     * Getter method to retrieve the unemployment data.
     * @return A list of integers representing the unemployment data.
     */
    public List<Integer> getUnemployment() {
        return unemployment;
    }

    /**
     * Setter method to update the unemployment data.
     * @param unemployment The new list of integers to set.
     */
    public void setUnemployment(List<Integer> unemployment) {
        this.unemployment = unemployment;
    }
    
    @ScheduledMethod(start = 1, interval = 1, priority = 2)
    public void dailyTracking() {
    	unemployment.add(1000 - DataCollecter.getEmployedCount());
    }
    
    @ScheduledMethod(start = 1, interval = 21, priority = 4)
    public void monthlyReset() {
    	unemployment.clear();
    }
    
    
    

}
