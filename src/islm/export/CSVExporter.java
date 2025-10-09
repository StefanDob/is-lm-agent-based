package islm.export;

import repast.simphony.engine.environment.RunEnvironment;
import java.io.*;
import java.nio.file.*;

/**
* Utility class for exporting simulation data into CSV files at different time intervals.
* 
* <p>The exporter creates three output files under the {@code output/} directory:</p>
* <ul>
*   <li>{@code islm_daily_output.csv} – exports daily statistics (e.g., unmet demand ratio).</li>
*   <li>{@code islm_monthly_output.csv} – exports monthly macroeconomic indicators 
*       (employment, wages, prices, inventories, etc.).</li>
*   <li>{@code islm_semianual_output.csv} – exports aggregated data every six months 
*       (e.g., unemployment averages, price changes).</li>
* </ul>
*
* <p>Each file is initialized with headers on first write. Existing files are deleted 
* when a new {@code CSVExporter} is created to ensure fresh outputs per simulation run.</p>
*/
public class CSVExporter {

    private final String dailyFilePath = "output/islm_daily_output.csv";
    private final String monthlyFilePath = "output/islm_monthly_output.csv";
    private final String quarterlyFilePath = "output/islm_semianual_output.csv";

    private boolean dailyHeaderWritten = false;
    private boolean monthlyHeaderWritten = false;
    private boolean quarterlyHeaderWritten = false;

    
    /**
     * Creates a new exporter and ensures the output directory exists.
     * Old CSV files are deleted at initialization to avoid mixing runs.
     */
    public CSVExporter() {
        try {
            Files.createDirectories(Paths.get("output"));
            Files.deleteIfExists(Paths.get(dailyFilePath));
            Files.deleteIfExists(Paths.get(monthlyFilePath));
            Files.deleteIfExists(Paths.get(quarterlyFilePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Appends daily simulation statistics to {@code islm_daily_output.csv}.
     * Currently logs tick number and average unmet demand ratio.
     */
    public void exportDailyData() {
        try (FileWriter fw = new FileWriter(dailyFilePath, true);
             BufferedWriter bw = new BufferedWriter(fw)) {

            if (!dailyHeaderWritten) {
                bw.write("tick,unmetDemandRatio\n");
                dailyHeaderWritten = true;
            }

            double tick = RunEnvironment.getInstance()
                    .getCurrentSchedule().getTickCount();

            bw.write(tick + "," + DataCollecter.getUnmetDemandRatioDurchschnitt() + "\n");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Appends monthly aggregated statistics to {@code islm_monthly_output.csv}.
     * Logs employment, wages, prices, inventories, demand, and liquidity balances.
     */
    public void exportMonthlyData() {
        try (FileWriter fw = new FileWriter(monthlyFilePath, true);
             BufferedWriter bw = new BufferedWriter(fw)) {

            if (!monthlyHeaderWritten) {
                bw.write("tick,employed_households(Über Haushalte),BeschäftigteLeute(über Unternehmen),openPositions,"
                        + "durchschnittsgehalt,durchschnittspreis,durchschnittsinventar,gesamtNachfrageLetzterMonat,"
                        + "geplanterMonatlicherKonsum(Haushalte),allUnternehmenMoney,allHaushalteMoney,allMoney\n");
                monthlyHeaderWritten = true;
            }

            double tick = RunEnvironment.getInstance()
                    .getCurrentSchedule().getTickCount();

            int employed = DataCollecter.getEmployedCount();

            bw.write(tick + "," + employed + "," + DataCollecter.beschäftigteLeute() + ","
                    + DataCollecter.getOpenPositions() + "," + DataCollecter.getDurchschnittsgehalt() + ","
                    + DataCollecter.getDurchSchnittspreis() + "," + DataCollecter.getDurchSchnittsinventar() + ","
                    + DataCollecter.getGesamtNachfrage() + "," + DataCollecter.getGeplanterMonatlicherKonsum() + ","
                    + DataCollecter.getAllUnternehmenMoney() + "," + DataCollecter.getAllHouseholdMoney() + ","
                    + DataCollecter.getAllMoney() + "\n");

        } catch (IOException e) {
            e.printStackTrace();
        }

        
    }

    /**
     * Appends semiannual (6 months) data to {@code islm_semianual_output.csv}.
     * Currently includes unemployment averages and price delta - used for phillips curve
     */
    public void exportSemianualData() {
        try (FileWriter fw = new FileWriter(quarterlyFilePath, true);
             BufferedWriter bw = new BufferedWriter(fw)) {

            if (!quarterlyHeaderWritten) {
                bw.write("tick,unemploymentAverage,DeltaPrice,\n");
                quarterlyHeaderWritten = true;
            }

            double tick = RunEnvironment.getInstance()
                    .getCurrentSchedule().getTickCount();

            

            bw.write(tick + "," + DataCollecter.getDurchschnittsArbeitslosenSemianual() + "," + DataCollecter.getDeltaPriceSemianual()  + "\n");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
