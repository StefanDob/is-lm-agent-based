package islm.export;

import java.util.*;

/**
 * Utility-Klasse zur Berechnung des Gini-Koeffizienten.
 */
public final class InequalityUtils {

    private InequalityUtils() { /* no instantiation */ }

    /**
     * Berechnet den Gini-Koeffizienten aus einem Array von Werten (z. B. Einkommen).
     *
     * Formel (diskrete, aufsteigend sortierte Werte x_i):
     *   G = (1 / (n * mean)) * sum_{i=1..n} (2*i - n - 1) * x_i
     *
     * Laufzeit: O(n log n) wegen des Sortierens.
     *
     * @param values Array mit nicht-negativen Werten
     * @return Gini-Koeffizient im Bereich [0, 1] (bei nicht-negativen Werten)
     * @throws IllegalArgumentException wenn values == null
     */
    public static double gini(double[] values) {
        if (values == null) {
            throw new IllegalArgumentException("values must not be null");
        }
        int n = values.length;
        if (n == 0) return 0.0;

        // Kopie anlegen, damit das Original nicht verändert wird
        double[] x = Arrays.copyOf(values, n);
        Arrays.sort(x); // aufsteigend

        // Summe und Mittelwert
        double sum = 0.0;
        for (double v : x) {
            sum += v;
        }
        if (sum == 0.0) return 0.0; // keine Verteilung

        // Summe der gewichteten Werte
        // Achtung Indizes: i läuft 0..n-1, im Formel-Index i_formel = i+1
        double weightedSum = 0.0;
        for (int i = 0; i < n; i++) {
            double factor = 2.0 * (i + 1) - n - 1; // (2*i - n - 1) mit i=1..n
            weightedSum += factor * x[i];
        }

        double gini = weightedSum / (n * sum);
        // Numerische Ungenauigkeiten können minimal negative Werte liefern; auf 0 korrigieren
        if (gini < 0 && gini > -1e-15) gini = 0.0;

        return gini;
    }

    /**
     * Überladung für List<Double>.
     */
    public static double gini(List<Double> values) {
        if (values == null) throw new IllegalArgumentException("values must not be null");
        double[] arr = values.stream().mapToDouble(Double::doubleValue).toArray();
        return gini(arr);
    }
}
