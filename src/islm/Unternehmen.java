package islm;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Unternehmen {

    private double kapital = 1000;
    private Zentralbank zentralbank;
    private List<Kreditvertrag> kredite = new ArrayList<>();

    public Unternehmen(Zentralbank zentralbank) {
        this.zentralbank = zentralbank;
    }

    @ScheduledMethod(start = 1, interval = 1)
    public void step() {
        double lohnkosten = RandomHelper.nextDoubleFromTo(20, 100);
        double materialkosten = RandomHelper.nextDoubleFromTo(30, 150);
        kapital -= (lohnkosten + materialkosten);

        // Investitionswunsch äußern
        double investitionsbedarf = RandomHelper.nextDoubleFromTo(100, 500);
        zentralbank.stelleKreditanfrage(new KreditAnfrage(this, investitionsbedarf));

        // Zinsen und Tilgung zahlen
        double tilgungsrate = 50;

        Iterator<Kreditvertrag> it = kredite.iterator();
        while (it.hasNext()) {
            Kreditvertrag vertrag = it.next();
            double zinsen = vertrag.berechneZinszahlung();
            kapital -= zinsen;

            kapital -= tilgungsrate;
            vertrag.tilge(tilgungsrate);

            if (vertrag.istAbbezahlt()) {
                it.remove();
            }
        }

        if (kapital < -1000) {
            System.out.println("Unternehmen insolvent.");
        }
    }

    public void erhalteZahlung(double betrag) {
        kapital += betrag;
        System.out.println("Gets here");
    }

    public void empfangeKreditvertrag(Kreditvertrag vertrag) {
        kredite.add(vertrag);
        kapital += vertrag.ursprungsbetrag;
    }
}



