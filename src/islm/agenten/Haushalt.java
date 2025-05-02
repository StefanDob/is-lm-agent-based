package islm.agenten;

import islm.SessionManager;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;

public class Haushalt {
	
	private double kassenBestand;
	
	private double konsum;

    
    public Haushalt() {
        
    }

    @ScheduledMethod(start = 1, interval = 1)
    public void step() {
        
        konsum = RandomHelper.nextDoubleFromTo(0.2, 1.0) * kassenBestand;
        
        // Konsum: über die bank wird der Konsum an zufällige unternehmen verteilt
        SessionManager.getBank().empfangeKonsumZahlung(konsum);
        
        double ersparnis = kassenBestand - konsum;

        // Investition wird als Ersparnis über die Bank Firmen bereitgestellt
        SessionManager.getBank().empfangeSparbetrag(ersparnis);
        
        
    }
    
    
    public void erhalteLohn(double lohn) {
    	double steuern = lohn * SessionManager.getStaat().getSteuerRate();
    	SessionManager.getStaat().erhalteSteuern(steuern);
    	
    	this.kassenBestand += (lohn - steuern);
    }
    
    
    public double getKonsum() {
    	return konsum;
    }
}