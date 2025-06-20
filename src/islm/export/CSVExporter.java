package islm.export;

import repast.simphony.engine.schedule.ScheduledMethod;
import java.io.*;
import java.nio.file.*;
import java.util.List;

import islm.SessionManager;
import islm.agenten.Haushalt;

public class CSVExporter {

    private final String dailyFilePath = "output/islm_daily_output.csv";
    private final String monthlyFilePath = "output/islm_monthly_output.csv";

    private boolean dailyHeaderWritten = false;
    private boolean monthlyHeaderWritten = false;

    public CSVExporter() {
        try {
            Files.createDirectories(Paths.get("output"));
            Files.deleteIfExists(Paths.get(dailyFilePath));
            Files.deleteIfExists(Paths.get(monthlyFilePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @ScheduledMethod(start = 1, interval = 1, priority = 2.0)
    public void exportDailyData() {
        try (FileWriter fw = new FileWriter(dailyFilePath, true);
             BufferedWriter bw = new BufferedWriter(fw)) {

            if (!dailyHeaderWritten) {
                bw.write("tick, unmetDemandRatio \n");
                dailyHeaderWritten = true;
            }

            double tick = repast.simphony.engine.environment.RunEnvironment.getInstance()
                    .getCurrentSchedule().getTickCount();

            
            
            bw.write(tick + "," + DataCollecter.getUnmetDemandRatioDurchschnitt() + "\n");
            

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @ScheduledMethod(start = 1, interval = 21, priority = 2.0)
    public void exportMonthlyData() {
        try (FileWriter fw = new FileWriter(monthlyFilePath, true);
             BufferedWriter bw = new BufferedWriter(fw)) {

            if (!monthlyHeaderWritten) {
            	bw.write("tick,employed_households(Über Haushalte), BeschäftigteLeute (über Unternehmen), open Positions,durchschnittsgehalt,Durchschnittspreis,Durchschnittsinventar,gesamtNachfrageLetzerMonat, "
            			+ "gepl. mon. KOnsum (Haushalte), allUnternehmenMoney, allHaushalteMoney, allMoney\n");
               
                monthlyHeaderWritten = true;
            }

            double tick = repast.simphony.engine.environment.RunEnvironment.getInstance()
                    .getCurrentSchedule().getTickCount();

            int employed = DataCollecter.getEmployedCount(); // You implement this method

            bw.write(tick + "," + employed + "," + DataCollecter.beschäftigteLeute() + "," + DataCollecter.getOpenPositions() +  "," + DataCollecter.getDurchschnittsgehalt() + "," + DataCollecter.getDurchSchnittspreis() +
            		"," + DataCollecter.getDurchSchnittsinventar() + ","  + DataCollecter.getGesamtNachfrage() +
            		"," + DataCollecter.getGeplanterMonatlicherKonsum() + "," + DataCollecter.getAllUnternehmenMoney() + ","+ DataCollecter.getAllHouseholdMoney() + 
            		","  +DataCollecter.getAllMoney() +"\n");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
