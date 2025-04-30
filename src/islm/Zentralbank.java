package islm;

import java.util.*;

public class Zentralbank {

    private double zinssatz = 0.05;
    private List<KreditAnfrage> kreditAnfragen = new ArrayList<>();
    private List<Kreditvertrag> aktiveKredite = new ArrayList<>();
    private List<Haushalt> haushalte = new ArrayList<>();
    private double gesamtSparbetrag = 0;

    public void empfangeSparbetrag(double betrag) {
        gesamtSparbetrag += betrag;
    }

    public void registriereHaushalt(Haushalt h) {
        haushalte.add(h);
    }

    public void stelleKreditanfrage(KreditAnfrage anfrage) {
        kreditAnfragen.add(anfrage);
    }

    public void verteileKredite() {
        for (KreditAnfrage anfrage : kreditAnfragen) {
            if (gesamtSparbetrag >= anfrage.betrag) {
                Kreditvertrag vertrag = new Kreditvertrag(anfrage.betrag, zinssatz, anfrage.unternehmen);
                aktiveKredite.add(vertrag);
                anfrage.unternehmen.empfangeKreditvertrag(vertrag);
                gesamtSparbetrag -= anfrage.betrag;
            }
        }
        kreditAnfragen.clear();
    }

    public double getZinssatz() {
        return zinssatz;
    }
}