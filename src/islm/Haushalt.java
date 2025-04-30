package islm;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;

public class Haushalt {

    private double einkommen = 100 + RandomHelper.nextDoubleFromTo(0, 100);
    private double konsum, investition, ersparnis, steuern;
    private Zentralbank zentralbank;

    public Haushalt(Zentralbank zentralbank) {
        this.zentralbank = zentralbank;
        zentralbank.registriereHaushalt(this);
    }

    @ScheduledMethod(start = 1, interval = 1)
    public void step() {
        steuern = einkommen * 0.2;

        double netto = einkommen - steuern;
        konsum = RandomHelper.nextDoubleFromTo(0.2, 1.0) * netto;
        investition = RandomHelper.nextDoubleFromTo(0.0, 0.3) * (netto - konsum);
        ersparnis = netto - konsum - investition;

        // Investition wird als Ersparnis über die Zentralbank Firmen bereitgestellt
        zentralbank.empfangeSparbetrag(ersparnis);

        // Konsum: zufälliges Unternehmen wählen
        Unternehmen ziel = SimUtils.zufaelligesUnternehmen();
        if (ziel != null) {
            ziel.erhalteZahlung(konsum);
        }
    }
}