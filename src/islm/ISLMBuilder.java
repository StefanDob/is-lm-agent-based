package islm;

import java.util.*;


import islm.agenten.Haushalt;
import islm.agenten.HuashaltCallerHelper;
import islm.agenten.Staat;
import islm.agenten.Unternehmen;
import islm.export.CSVExporter;
import islm.export.ExportManager;
import islm.export.UnemploymentTracker;
import repast.simphony.context.Context;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.data2.DataSetRegistry;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.environment.RunState;

/**
 * {@code ISLMBuilder} sets up the simulation environment for the ISLM model.
 * 
 * <p>This class is responsible for creating the agents, registering them in the 
 * simulation context, and initializing supporting managers and trackers.</p>
 *
 * <p>Main responsibilities:</p>
 * <ul>
 *   <li>Populate the simulation with firms ({@code Unternehmen}) and households ({@code Haushalt}).</li>
 *   <li>Create supporting agents such as the state ({@code Staat}), helper classes, and trackers.</li>
 *   <li>Register all created agents with the {@code SessionManager} for global access.</li>
 *   <li>Define the total number of ticks for the simulation run.</li>
 * </ul>
 */
public class ISLMBuilder implements ContextBuilder<Object> {
	
	/** 
     * Total number of simulation ticks.
     * Defined as 7000 months × 21 days per month.
     */
	public static final int TOTAL_SIMULATION_TICKS = 7000 * 21;
	
	/**
     * Builds and initializes the simulation context.
     * 
     * <p>The following setup is performed:</p>
     * <ul>
     *   <li>Creates 100 firms with initial liquidity and registers them.</li>
     *   <li>Creates 1000 households with random firm connections and registers them.</li>
     *   <li>Adds a {@code HuashaltCallerHelper} to manage household execution order.</li>
     *   <li>Creates and registers the {@code Staat} (government actor).</li>
     *   <li>Initializes and registers an {@code ExportManager} for data output.</li>
     *   <li>Initializes and registers an {@code UnemploymentTracker} for statistics.</li>
     *   <li>Configures the simulation to end at {@link #TOTAL_SIMULATION_TICKS}.</li>
     * </ul>
     *
     * @param context the simulation context
     * @return the populated simulation context
     */
    @Override
    public Context<Object> build(Context<Object> context) {
        context.setId("islm");
        
        // --- Firms (Unternehmen) ---
        for (int i = 0; i < 100; i++) {
            Unternehmen u = new Unternehmen(1000);
            context.add(u);
            SessionManager.registriereUnternehmen(u);
        }
       
        // --- Households (Haushalt) ---
        for (int i = 0; i < 1000; i++) {
        	
            Haushalt h = new Haushalt(100,createListOfRandomCompanies());
            context.add(h);
            SessionManager.registriereHaushalt(h);
        }
        
        // --- Household helper (ensures random order of execution) ---
        HuashaltCallerHelper helper = new HuashaltCallerHelper();
        context.add(helper);
        
        // --- Government actor (Staat) ---
        Staat staat = new Staat();
        SessionManager.setStaat(staat); 
        context.add(staat);
        
        // --- Data export manager ---
        ExportManager exportManager = new ExportManager();
        context.add(exportManager);
        
        // --- Tracker for unemployment statistics ---
        UnemploymentTracker unemploymentTracker = new UnemploymentTracker();
        SessionManager.setUnemploymentTracker(unemploymentTracker);
        context.add(unemploymentTracker);
        
        
        // --- End simulation after defined number of ticks ---
        RunEnvironment.getInstance().endAt(TOTAL_SIMULATION_TICKS);
        
        return context; 
    }
    
    /**
     * Creates a list of random firms to initialize household consumption connections.
     * Each household is connected to 7 randomly chosen firms.
     *
     * @return a list of 7 randomly selected firms
     */
    private List<Unternehmen> createListOfRandomCompanies() {
		List<Unternehmen> returnList = new ArrayList<>();
		for(int i = 0; i < 7; i++) {
			returnList.add(SessionManager.getRandomUnternehmen());
		}
		return returnList;
	}
}
