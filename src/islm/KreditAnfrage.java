package islm;

public class KreditAnfrage {
    public Unternehmen unternehmen;
    public double betrag;

    // Konstruktor
    public KreditAnfrage(Unternehmen unternehmen, double betrag) {
        this.unternehmen = unternehmen;
        this.betrag = betrag;
    }
}
