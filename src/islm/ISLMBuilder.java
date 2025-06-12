package islm;

import islm.agenten.Bank;
import islm.agenten.Haushalt;
import islm.agenten.Staat;
import islm.agenten.Unternehmen;
import islm.agenten.Zentralbank;
import islm.export.CSVExporter;
import repast.simphony.context.Context;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;

public class ISLMBuilder implements ContextBuilder<Object> {

    @Override
    public Context<Object> build(Context<Object> context) {
        context.setId("islm");
       
        
        
        
        //Setup Zentralbank
        Zentralbank.setZinsSatz(0.02);
        Zentralbank.setGeldMenge(1000000);
        
        Bank bank = new Bank(Zentralbank.getZinsSatz());
        SessionManager.setBank(bank);
        context.add(bank);
        
        //Staat aufsetzen
        Staat staat = new Staat(0.2);
        SessionManager.setStaat(staat);
        context.add(staat);
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
            //das erste Geld wird so in das system eingeführt
            u.erhalteZahlung(startKapitalProUnternehmen);
            
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
}
