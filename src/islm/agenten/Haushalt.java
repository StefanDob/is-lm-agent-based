package islm.agenten;

import islm.SessionManager;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;

public class Haushalt {
	
	private double kassenBestand;
	
	private double konsum;
	
	double investition;

    
    public Haushalt() {
        
    }

    @ScheduledMethod(start = 1, interval = 1)
    public void step() {
        
    	double zins = SessionManager.getBank().getMarktZins(); // aktueller Marktzins

        // Zinsreaktionsfunktion: höhere Zinsen → mehr sparen (weniger konsumieren)
        // z. B. Konsumquote nimmt linear ab mit steigendem Zins (zwischen 0.2 und 1.0)
        double konsumQuote = 1.0 - Math.min(0.8, zins * 10); // skaliert bei Zins = 0.08 auf 0.2

        konsum = konsumQuote * kassenBestand;

        // Konsumzahlung über die Bank an Firmen
        SessionManager.getBank().empfangeKonsumZahlung(konsum);

        investition = kassenBestand - konsum;
        
       

        // Sparbetrag wird über die Bank investiert
        SessionManager.getBank().empfangeSparbetrag(investition, this);
        
        kassenBestand = 0;
    }
    
    
    public void erhalteLohn(double lohn) {
    	double steuern = lohn * SessionManager.getStaat().getSteuerRate();
    	SessionManager.getStaat().erhalteSteuern(steuern);
    	
    	this.kassenBestand += (lohn - steuern);
    }
    
    public void erhalteZinsen(double zinsen) {
    	//TODO vllt auch steuern modellieren?
    	this.kassenBestand += zinsen;
    }
    
    
    public double getKonsum() {
    	return konsum;
    }
    
    public double getInvestition() {
    	return investition;
    }
}