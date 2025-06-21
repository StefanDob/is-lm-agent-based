package islm.agenten;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import islm.SessionManager;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;

//Helper class in order to be able to call the haushalt in random order
//TODO Spelling
public class HuashaltCallerHelper {
	//this method is called here instead of in the household classes in order to make sure that the houselholds are picked in 
    //random order to seek new trading connections
    @ScheduledMethod(start = 1, interval = 21, priority = -2)
    public void beginningOfMonth() {
    	List<Haushalt> shuffledHouseholds = new ArrayList<>(SessionManager.getHausHaltListe());
    	Collections.shuffle(shuffledHouseholds);
    	for(Haushalt h : shuffledHouseholds) {
    		h.beginningOfMonth();
    	}
    }
    
    
  //this method is called here instead of in the household classes in order to make sure that the houselholds are picked in 
    //random order to execute their daily demands
    @ScheduledMethod(start = 1, interval = 1, priority = -1)
    public void dayStep() {
    	List<Haushalt> shuffledHouseholds = new ArrayList<>(SessionManager.getHausHaltListe());
    	Collections.shuffle(shuffledHouseholds);
    	for(Haushalt h : shuffledHouseholds) {
    		h.dayStep();
    	}
    }
}
