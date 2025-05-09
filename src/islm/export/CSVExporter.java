package islm.export;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import islm.SessionManager;
import islm.agenten.Zentralbank;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;

public class CSVExporter {
    private final String filePath = "output/islm_output.csv";
    private boolean headerWritten = false;
    
    public CSVExporter() {
    	File file = new File(filePath);
    	
    	// Delete existing file if it exists
        if (file.exists()) {
            boolean deleted = file.delete();
            if (!deleted) {
                System.err.println("Warning: Could not delete existing file at " + filePath);
            }
        }
    }

    
    @ScheduledMethod(start = 1, interval = 1, priority = 2.0)
    public void exportData() {
        try {
            File file = new File(filePath);
            boolean isNewFile = !file.exists();

            try (FileWriter writer = new FileWriter(file, true)) {

                // Kopfzeile nur einmal schreiben
                if (isNewFile || !headerWritten) {
                    writer.append("Tick,Zentralbankzins,Realzins,Investition,Konsum,Staatsausgaben,Aktive Kerdite,Kredit Anfragen,Anzahl Unternehmen\n");
                    headerWritten = true;
                }

                int tick = (int) RunEnvironment.getInstance().getCurrentSchedule().getTickCount();

                double zentralbankZins = Zentralbank.getZinsSatz();
                double realZins = SessionManager.getBank().getMarktZins();
                double investition = DataCollecter.getInvestition();
                double konsum = DataCollecter.getKonsum();
                double staatsausgaben = SessionManager.getStaat().getStaatsausgaben();
                double aktiveKredite = SessionManager.getBank().getAktiveKredite().size();
                double kreditAnfragen = SessionManager.getBank().getKreditAnfragen().size();
                double anzahlUnternehmen = SessionManager.getUnternehmenListe().size();
                
                
                String line = tick + "," + zentralbankZins + "," + realZins + "," + investition + "," + konsum + "," + staatsausgaben + "," + aktiveKredite + "," + kreditAnfragen + 
                	"," + anzahlUnternehmen  + "\n";

                writer.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}