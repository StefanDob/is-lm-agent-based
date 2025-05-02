package islm.agenten;



public class Zentralbank {

    private static double zinsSatz;
    private static double geldMenge;

    public static double getZinsSatz() {
        return zinsSatz;
    }

    public static void setZinsSatz(double neuerZinsSatz) {
        zinsSatz = neuerZinsSatz;
    }

    public static double getGeldMenge() {
        return geldMenge;
    }

    public static void setGeldMenge(double neueGeldMenge) {
        geldMenge = neueGeldMenge;
    }
}
