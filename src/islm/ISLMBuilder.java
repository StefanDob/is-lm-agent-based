package islm;

import islm.agenten.Bank;
import islm.agenten.Haushalt;
import islm.agenten.Staat;
import islm.agenten.Unternehmen;
import islm.agenten.Zentralbank;
import repast.simphony.context.Context;
import repast.simphony.dataLoader.ContextBuilder;

public class ISLMBuilder implements ContextBuilder<Object> {

    @Override
    public Context<Object> build(Context<Object> context) {
        context.setId("islm");
       
        
        
        
        //Setup Zentralbank
        Zentralbank.setZinsSatz(0.02);
        Zentralbank.setGeldMenge(1000000);
        
        Bank bank = new Bank();
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
        
        System.out.println("Gets here and context is: " + (context == null));
        
       
        
        
        return context;
    }
}
