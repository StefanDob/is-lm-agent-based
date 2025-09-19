package islm.export;

import repast.simphony.engine.environment.RunEnvironment;
import java.io.*;
import java.nio.file.*;

public class CSVExporter {

    private final String dailyFilePath = "output/islm_daily_output.csv";
    private final String monthlyFilePath = "output/islm_monthly_output.csv";
    private final String quarterlyFilePath = "output/islm_quarterly_output.csv";

    private boolean dailyHeaderWritten = false;
    private boolean monthlyHeaderWritten = false;
    private boolean quarterlyHeaderWritten = false;

    private int monthCounter = 0; // counts months to trigger quarterly export

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

        // increment month counter and trigger quarterly export
        monthCounter++;
        if (monthCounter == 3) {
            exportQuarterlyData();
            monthCounter = 0;
        }
    }

    private void exportQuarterlyData() {
        try (FileWriter fw = new FileWriter(quarterlyFilePath, true);
             BufferedWriter bw = new BufferedWriter(fw)) {

            if (!quarterlyHeaderWritten) {
                bw.write("tick,unemploymentAverage,DeltaPrice,\n");
                quarterlyHeaderWritten = true;
            }

            double tick = RunEnvironment.getInstance()
                    .getCurrentSchedule().getTickCount();

            

            bw.write(tick + "," + DataCollecter.getDurchschnittsArbeitslosenQuarter() + "," + DataCollecter.getDeltaPriceQuarter()  + "\n");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
