package islm;

public class Kreditvertrag {
    public double ursprungsbetrag;
    public double restschuld;
    public double zinssatz;
    public Unternehmen kreditnehmer;

    public Kreditvertrag(double betrag, double zinssatz, Unternehmen kreditnehmer) {
        this.ursprungsbetrag = betrag;
        this.restschuld = betrag;
        this.zinssatz = zinssatz;
        this.kreditnehmer = kreditnehmer;
    }

    public double berechneZinszahlung() {
        return restschuld * zinssatz;
    }

    public void tilge(double betrag) {
        restschuld -= betrag;
        if (restschuld < 0) restschuld = 0;
    }

    public boolean istAbbezahlt() {
        return restschuld <= 0.01;
    }
}

