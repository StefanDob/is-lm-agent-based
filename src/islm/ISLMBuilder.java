package islm;

import java.util.*;


import islm.agenten.Haushalt;
import islm.agenten.Unternehmen;
import islm.export.CSVExporter;
import repast.simphony.context.Context;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;

public class ISLMBuilder implements ContextBuilder<Object> {

    @Override
    public Context<Object> build(Context<Object> context) {
        context.setId("islm");
       
        for (int i = 0; i < 70; i++) {
            Haushalt h = new Haushalt();
            context.add(h);
            SessionManager.registriereHaushalt(h);
        }
		
        int anzahlUnternehmen = 20;
        double startKapitalProUnternehmen = Zentralbank.getGeldMenge() / anzahlUnternehmen;
        
        for (int i = 0; i < 10; i++) {
            Unternehmen u = new Unternehmen();
            context.add(u);
            SessionManager.registriereUnternehmen(u);
        }
        
        
       //Setup exporter
        CSVExporter exporter = new CSVExporter();
        context.add(exporter);
        
        RunEnvironment.getInstance().endAt(1000);
        
        
        
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
    
    
    //this method is called here instead of in the household classes in order to make sure that the houselholds are picked in 
    //random order to seek new trading connections
    @ScheduledMethod(start = 1, interval = 21, priority = ScheduleParameters.LAST_PRIORITY)
    public void beginningOfMonth() {
    	List<Haushalt> shuffledHouseholds = new ArrayList<>(SessionManager.getHausHaltListe());
    	Collections.shuffle(shuffledHouseholds);
    	for(Haushalt h : shuffledHouseholds) {
    		h.beginningOfMonth();
    	}
    }
}
