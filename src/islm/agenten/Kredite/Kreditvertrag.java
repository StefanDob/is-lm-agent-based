package islm.agenten.Kredite;

import islm.agenten.Unternehmen;
import islm.agenten.Zentralbank;
import repast.simphony.engine.schedule.ScheduledMethod;

public class Kreditvertrag {
    public double ursprungsbetrag;
    public double restschuld;
    public Unternehmen kreditnehmer;

    public Kreditvertrag(double betrag, Unternehmen kreditnehmer) {
        this.ursprungsbetrag = betrag;
        this.restschuld = betrag;
        this.kreditnehmer = kreditnehmer;
    }
    
    
    @ScheduledMethod(start = 1, interval = 1)
    public void step() {
    	//TODO account for months and years maybe
    	kreditnehmer.zahleZinsen(berechneZinszahlung());
    }

    public double berechneZinszahlung() {
        return restschuld * Zentralbank.getZinsSatz();
    }

    public void tilge(double betrag) {
        restschuld -= betrag;
        if (restschuld < 0) restschuld = 0;
    }

    public boolean istAbbezahlt() {
        return restschuld <= 0.01;
    }
    
    public double getUrsprungsbetrag() {
        return ursprungsbetrag;
    }

    public void setUrsprungsbetrag(double ursprungsbetrag) {
        this.ursprungsbetrag = ursprungsbetrag;
    }

    public double getRestschuld() {
        return restschuld;
    }

    public void setRestschuld(double restschuld) {
        this.restschuld = restschuld;
    }

    public Unternehmen getKreditnehmer() {
        return kreditnehmer;
    }

    public void setKreditnehmer(Unternehmen kreditnehmer) {
        this.kreditnehmer = kreditnehmer;
    }
    
    
}

