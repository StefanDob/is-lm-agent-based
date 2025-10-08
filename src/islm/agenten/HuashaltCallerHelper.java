package islm.agenten;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import islm.SessionManager;
import repast.simphony.engine.schedule.ScheduledMethod;

/**
 * {@code HuashaltCallerHelper} is a scheduling utility that coordinates
 * household behavior in the simulation.
 * 
 * <p>The primary purpose of this helper is to ensure that all households
 * ({@link Haushalt}) are invoked in a <b>random order</b> rather than a
 * fixed sequence. </p>
 *
 * <p>Two types of scheduled events are handled:</p>
 * <ul>
 *   <li>{@link #beginningOfMonth()} – executed once every 21 ticks, at the start of each month.</li>
 *   <li>{@link #dayStep()} – executed every tick, for daily household decisions.</li>
 * </ul>
 */
public class HuashaltCallerHelper {
	
	/**
     * Invoked at the beginning of each month.
     *
     * <p>This method shuffles the household list and calls
     * {@link Haushalt#beginningOfMonth()} for each household.
     * The random order ensures households seek new trading
     * connections without execution-order bias.</p>
     *
     * <p>Scheduled at:</p>
     * <ul>
     *   <li>{@code start = 1}</li>
     *   <li>{@code interval = 21} (every 21 ticks ≈ monthly cycle)</li>
     *   <li>{@code priority = -2} (runs before daily steps)</li>
     * </ul>
     */
    @ScheduledMethod(start = 1, interval = 21, priority = -2)
    public void beginningOfMonth() {
    	List<Haushalt> shuffledHouseholds = new ArrayList<>(SessionManager.getHausHaltListe());
    	Collections.shuffle(shuffledHouseholds);
    	for(Haushalt h : shuffledHouseholds) {
    		h.beginningOfMonth();
    	}
    }
    
    
    /**
     * Invoked every simulation day (every tick).
     *
     * <p>This method shuffles the household list and calls
     * {@link Haushalt#dayStep()} for each household.
     * The random order ensures daily demand execution
     * is unbiased across households.</p>
     *
     * <p>Scheduled at:</p>
     * <ul>
     *   <li>{@code start = 1}</li>
     *   <li>{@code interval = 1} (every tick ≈ daily cycle)</li>
     *   <li>{@code priority = -1} (after monthly setup)</li>
     * </ul>
     */
    @ScheduledMethod(start = 1, interval = 1, priority = -1)
    public void dayStep() {
    	List<Haushalt> shuffledHouseholds = new ArrayList<>(SessionManager.getHausHaltListe());
    	Collections.shuffle(shuffledHouseholds);
    	for(Haushalt h : shuffledHouseholds) {
    		h.dayStep();
    	}
    }
}