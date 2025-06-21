package islm;

import java.util.*;


import islm.agenten.Haushalt;
import islm.agenten.HuashaltCallerHelper;
import islm.agenten.Unternehmen;
import islm.export.CSVExporter;
import islm.export.ExportManager;
import repast.simphony.context.Context;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;

public class ISLMBuilder implements ContextBuilder<Object> {

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
        RunEnvironment.getInstance().endAt(7000 * 21);
        
        
        
        return context;
        
        /*
        context.setId("LengnickModel");

        NetworkBuilder<Object> consumptionNetBuilder = new NetworkBuilder<>("consumptionNetwork", context, true);
        consumptionNetBuilder.buildNetwork();

        NetworkBuilder<Object> employmentNetBuilder = new NetworkBuilder<>("employmentNetwork", context, false);
        employmentNetBuilder.buildNetwork();

        Random rand = new Random();

        for (int i = 0; i < 1000; i++) {
            Household h = new Household(100.0, 5.0);
            context.add(h);
        }

        for (int i = 0; i < 100; i++) {
            Firm f = new Firm(1.0 + rand.nextDouble() * 0.2, 5.0);
            context.add(f);
        }

        Network<Object> consumptionNet = (Network<Object>) context.getProjection("consumptionNetwork");
        Network<Object> employmentNet = (Network<Object>) context.getProjection("employmentNetwork");

        for (Object obj : context) {
            if (obj instanceof Household h) {
                List<Firm> firms = new ArrayList<>();
                for (Object fObj : context) {
                    if (fObj instanceof Firm f) firms.add(f);
                }
                Collections.shuffle(firms);
                for (int i = 0; i < 7; i++) {
                    Firm f = firms.get(i);
                    h.addConsumptionFirm(f);
                    consumptionNet.addEdge(h, f);
                }
            }
        }

        return context;
    }
         
         */
        
        
    }
    
    
    private List<Unternehmen> createListOfRandomCompanies() {
		List<Unternehmen> returnList = new ArrayList<>();
		for(int i = 0; i < 7; i++) {
			returnList.add(SessionManager.getRandomUnternehmen());
		}
		return returnList;
	}


	
}
