package islm;

import islm.export.DataCollecter;

public class GeldMarkt {

    // Parameter für Geldnachfragefunktion
    private static final double k = 0.5;
    private static final double h = 20.0;

    public static double berechneGeldnachfrage() {
        return k * DataCollecter.getVolkseinkommen() - h * SessionManager.getBank().getMarktZins();
    }
}