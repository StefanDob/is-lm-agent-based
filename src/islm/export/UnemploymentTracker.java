package islm.export;

import java.util.*;

import repast.simphony.engine.schedule.ScheduledMethod;

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
