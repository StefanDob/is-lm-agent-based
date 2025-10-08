package islm;

import java.util.*;



import islm.agenten.Haushalt;
import islm.agenten.HuashaltCallerHelper;
import islm.agenten.Unternehmen;
import islm.export.ExportManager;
import repast.simphony.context.Context;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;


/**
 * {@code ISLMBuilder} sets up the simulation environment for the ISLM model.
 * 
 * <p>This class is responsible for creating the agents, registering them in the 
 * simulation context, and initializing supporting managers and trackers.</p>
 *
 * <p>Main responsibilities:</p>
 * <ul>
 *   <li>Populate the simulation with firms ({@code Unternehmen}) and households ({@code Haushalt}).</li>
 *   <li>Create supporting agents such as helper classes, and trackers.</li>
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
	 * Builds and initializes the simulation context for the IS-LM model.
	 * 
	 * <p>This method sets up the main simulation environment by creating and registering
	 * economic agents (companies and households), as well as helper and export management
	 * components. It also defines the simulation’s total runtime.</p>
	 * 
	 * <p>The context includes:
	 * <ul>
	 *   <li>100 {@link Unternehmen} instances, each initialized with a base capital of 1000.</li>
	 *   <li>1000 {@link Haushalt} instances, each initialized with an income of 100 and
	 *       linked to a randomly selected list of seven companies.</li>
	 *   <li>A {@link HuashaltCallerHelper} instance to coordinate household activities.</li>
	 *   <li>An {@link ExportManager} instance to manage data export operations.</li>
	 * </ul></p>
	 * 
	 * <p>All created agents are registered within the {@link SessionManager} to ensure
	 * global accessibility throughout the simulation. The simulation is configured
	 * to run for a total of {@code TOTAL_SIMULATION_TICKS} time steps,
	 * corresponding to 7000 months with 21 days per month.</p>
	 * 
	 * @param context the simulation context to be built
	 * @return the fully initialized simulation context
	 */
    @Override
    public Context<Object> build(Context<Object> context) {
        context.setId("islm");
        
        for (int i = 0; i < 100; i++) {
            Unternehmen u = new Unternehmen(1000);
            context.add(u);
            SessionManager.registriereUnternehmen(u);
        }
       
        for (int i = 0; i < 1000; i++) {
        	
            Haushalt h = new Haushalt(100,createListOfRandomCompanies());
            context.add(h);
            SessionManager.registriereHaushalt(h);
        }
        
        HuashaltCallerHelper helper = new HuashaltCallerHelper();
        context.add(helper);
        
        
       //Setup exporter
        ExportManager exportManager = new ExportManager();
        context.add(exportManager);
        
        
        // 7000 months * 21 daysPerMonth 
        RunEnvironment.getInstance().endAt(TOTAL_SIMULATION_TICKS);
     
        return context;
    }
    
    /**
     * Creates a list of seven randomly selected {@link Unternehmen} instances.
     * 
     * <p>This method retrieves random companies from the {@link SessionManager}
     * to associate each household with a diverse set of firms.</p>
     * 
     * @return a list of randomly selected companies
     */
    private List<Unternehmen> createListOfRandomCompanies() {
		List<Unternehmen> returnList = new ArrayList<>();
		for(int i = 0; i < 7; i++) {
			returnList.add(SessionManager.getRandomUnternehmen());
		}
		return returnList;
	}


	
}
