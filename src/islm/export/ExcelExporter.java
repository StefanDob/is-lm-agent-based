package islm.export;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xddf.usermodel.chart.XDDFChartData;
import org.apache.poi.xddf.usermodel.chart.XDDFDataSource;
import org.apache.poi.xssf.usermodel.XSSFChart;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles the collection and export of simulation data to an Excel workbook.
 * 
 * <p>This class collects daily and monthly simulation metrics (e.g., unmet demand, employment,
 * wages, prices, inventories, Gini coefficients) and writes them to an Excel file
 * ("output/islm_output.xlsx"). It also generates a histogram sheet for the distribution
 * of daily unmet demand ratios.</p>
 * 
 * <p>Daily data is captured in a {@code DailyData} list and monthly data in a {@code MonthlyData} list.
 * The writeToExcel() method ensures previous sheets are removed before adding updated data.</p>
 */
public class ExcelExporter {

	private final String outputPath = "output/islm_output.xlsx";

    private final List<DailyData> dailyDataList = new ArrayList<>();
    private final List<MonthlyData> monthlyDataList = new ArrayList<>();

    public ExcelExporter() {
        try {
            Files.createDirectories(Paths.get("output"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Collects daily data for the current simulation tick (e.g., unmet demand ratio)
     * and stores it in memory for later export.
     */
    public void collectDailyData() {
        double tick = RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
        double unmetDemandRatio = DataCollecter.getUnmetDemandRatioDurchschnitt();

        dailyDataList.add(new DailyData(tick, unmetDemandRatio));
    }

    /**
     * Collects monthly aggregate data, including employment statistics, financial metrics,
     * consumption, and inequality measures, and stores it for later export.
     */
    public void collectMonthlyData() {
        double tick = RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
        int year = (int) (0 + (tick / 12) / 21);

        monthlyDataList.add(new MonthlyData(
                tick,
                year,
                DataCollecter.getEmployedCount(),
                DataCollecter.beschäftigteLeute(),
                DataCollecter.getOpenPositions(),
                DataCollecter.getDurchschnittlicherReservationsGehalt(),
                DataCollecter.getDurchschnittsgehalt(),
                DataCollecter.getDurchSchnittspreis(),
                DataCollecter.getDurchSchnittsinventar(),
                DataCollecter.getGesamtNachfrage(),
                DataCollecter.getGeplanterMonatlicherKonsum(),
                DataCollecter.getAllUnternehmenMoney(),
                DataCollecter.getAllHouseholdMoney(),
                DataCollecter.getAllMoney(),
                DataCollecter.getVermögensGini(),
                DataCollecter.getGehaltsGini(),
                DataCollecter.getEinkommenGini(),
                DataCollecter.getDeltaPriceMonat(),
                DataCollecter.getDurchschnittsArbeitslosenMonat()
        ));
    }

    
    /**
     * Writes all collected daily and monthly data to the Excel workbook.
     * Also generates a histogram sheet for the distribution of unmet demand.
     * Existing sheets with the same names are removed to avoid duplication.
     */
    public void writeToExcel() {
        Workbook workbook = new XSSFWorkbook();

        File file = new File(outputPath);
        if (file.exists()) {
            // Bestehendes Workbook laden
            try (FileInputStream fis = new FileInputStream(file)) {
                workbook = WorkbookFactory.create(fis);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            } catch (InvalidFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        } 

     // Alte Sheets entfernen (falls vorhanden)
        removeSheetIfExists(workbook, "Daily Data");
        removeSheetIfExists(workbook, "Monthly Data");
        removeSheetIfExists(workbook, "PDF Histogram");
        

        // Neue Daily-Sheet erstellen
        Sheet dailySheet = workbook.createSheet("Daily Data");
        Row dailyHeader = dailySheet.createRow(0);
        dailyHeader.createCell(0).setCellValue("Tick");
        dailyHeader.createCell(1).setCellValue("UnmetDemandRatio");

        int dailyRowIndex = 1;
        for (DailyData d : dailyDataList) {
            Row row = dailySheet.createRow(dailyRowIndex++);
            row.createCell(0).setCellValue(d.tick);
            row.createCell(1).setCellValue(d.unmetDemandRatio);
        }

        // Neue Monthly-Sheet erstellen
        Sheet monthlySheet = workbook.createSheet("Monthly Data");
        Row monthlyHeader = monthlySheet.createRow(0);
        String[] headers = {
                "Tick", "Jahr", "Beschäftigung", "Offene Stellen", "Arbeitslose", "ReservationsGehalt",
                "Durchschnittsgehalt", "Durchschnittspreis", "Durchschnittsinventar",
                "GesamtNachfrage", "GeplanterMonatlicherKonsum",
                "UnternehmenMoney", "HaushalteMoney", "AllMoney", "Vermögens Gini", "Gehalts Gini", "Einkommen Gini", "Arbeitslose (Monatsdurchschnitt)"
        };
        for (int i = 0; i < headers.length; i++) {
            monthlyHeader.createCell(i).setCellValue(headers[i]);
        }

        int monthlyRowIndex = 1;
        for (MonthlyData m : monthlyDataList) {
            Row row = monthlySheet.createRow(monthlyRowIndex++);
            row.createCell(0).setCellValue(m.tick);
            row.createCell(1).setCellValue(m.jahr);
            row.createCell(2).setCellValue(m.employedCount);
            row.createCell(3).setCellValue(m.openPositions);
            row.createCell(4).setCellValue(1000 - m.employedCount);
            row.createCell(5).setCellValue(m.reservationsGehalt);
            row.createCell(6).setCellValue(m.durchschnittsgehalt);
            row.createCell(7).setCellValue(m.durchschnittspreis);
            row.createCell(8).setCellValue(m.durchschnittsinventar);
            row.createCell(9).setCellValue(m.gesamtNachfrage);
            row.createCell(10).setCellValue(m.geplanterMonatlicherKonsum);
            row.createCell(11).setCellValue(m.allUnternehmenMoney);
            row.createCell(12).setCellValue(m.allHouseholdMoney);
            row.createCell(13).setCellValue(m.allMoney);
            row.createCell(14).setCellValue(m.vermögensGini);
            row.createCell(15).setCellValue(m.gehaltsGini);
            row.createCell(16).setCellValue(m.einkommenGini);
            row.createCell(17).setCellValue(1000 - m.employedCount);
            row.createCell(17).setCellValue(m.durchschnittArbeitslose);
        }
        
        //Häufigkeitsverteilung für unmet demand:
        int bins = 10;
        int[] histogram = new int[bins + 1]; // +1 for the zero-bin

        // Häufigkeitsverteilung berechnen
        for (DailyData d : dailyDataList) {
            double value = d.unmetDemandRatio;

            if (value == 0.0) {
                histogram[0]++; // Bin 0: exact zeros
            } else {
                int binIndex = (int) Math.ceil(value * bins); // z.B. value = 0.27 → bin 3
                binIndex = Math.min(binIndex, bins); // Sicherheitsgrenze
                histogram[binIndex]++;
            }
        }

        // Gesamtanzahl aller Beobachtungen
        int total = dailyDataList.size();

        // Neue Sheet für Histogramm
        Sheet histSheet = workbook.createSheet("PDF Histogram");
        Row header = histSheet.createRow(0);
        header.createCell(0).setCellValue("Bin");
        header.createCell(1).setCellValue("Frequency");
        header.createCell(2).setCellValue("Probability");

        // Bin 0: exakt 0
        Row zeroRow = histSheet.createRow(1);
        zeroRow.createCell(0).setCellValue("= 0");
        zeroRow.createCell(1).setCellValue(histogram[0]);
        zeroRow.createCell(2).setCellValue((double) histogram[0] / total);

        // Bins > 0
        for (int i = 1; i <= bins; i++) {
            Row row = histSheet.createRow(i + 1);
            double lowerBound = (i - 1) / (double) bins;
            double upperBound = i / (double) bins;
            String label = String.format("(%.2f – %.2f]", lowerBound, upperBound);

            row.createCell(0).setCellValue(label);
            row.createCell(1).setCellValue(histogram[i]);
            row.createCell(2).setCellValue((double) histogram[i] / total);
        }
        
        //schreiben

        try (FileOutputStream fos = new FileOutputStream(outputPath)) {
            workbook.write(fos);
            System.out.println("Data updated in " + outputPath + " at tick " +
                    RunEnvironment.getInstance().getCurrentSchedule().getTickCount());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void removeSheetIfExists(Workbook workbook, String sheetName) {
        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet != null) {
            int index = workbook.getSheetIndex(sheet);
            workbook.removeSheetAt(index);
        }
    }


    /**
     * Represents a single day's data for export.
     */
    private static class DailyData {
        final double tick;
        final double unmetDemandRatio;

        DailyData(double tick, double unmetDemandRatio) {
            this.tick = tick;
            this.unmetDemandRatio = unmetDemandRatio;
        }
        
    }
    
    /**
     * Represents aggregated monthly data for export.
     */
    private static class MonthlyData {
        final double tick;
        final int jahr;
        final int employedCount;
        final int beschäftigteLeute;
        final int openPositions;
        final double reservationsGehalt;
        final double durchschnittsgehalt;
        final double durchschnittspreis;
        final double durchschnittsinventar;
        final double gesamtNachfrage;
        final double geplanterMonatlicherKonsum;
        final double allUnternehmenMoney;
        final double allHouseholdMoney;
        final double allMoney;
        final double vermögensGini;
        final double gehaltsGini;
        final double einkommenGini;
        final double deltaPreis;
        final double durchschnittArbeitslose;
        MonthlyData(double tick, int jahr, int employedCount, int beschäftigteLeute, int openPositions, double reservationsGehalt,
                    double durchschnittsgehalt, double durchschnittspreis, double durchschnittsinventar,
                    double gesamtNachfrage, double geplanterMonatlicherKonsum,
                    double allUnternehmenMoney, double allHouseholdMoney, double allMoney,double vermögensGini, double gehaltsGini,
                    double einkommenGini, double deltaPreis, double durchschnittArbeitslose) {
            this.tick = tick;
            this.jahr = jahr;
            this.employedCount = employedCount;
            this.beschäftigteLeute = beschäftigteLeute;
            this.openPositions = openPositions;
            this.reservationsGehalt = reservationsGehalt;
            this.durchschnittsgehalt = durchschnittsgehalt;
            this.durchschnittspreis = durchschnittspreis;
            this.durchschnittsinventar = durchschnittsinventar;
            this.gesamtNachfrage = gesamtNachfrage;
            this.geplanterMonatlicherKonsum = geplanterMonatlicherKonsum;
            this.allUnternehmenMoney = allUnternehmenMoney;
            this.allHouseholdMoney = allHouseholdMoney;
            this.allMoney = allMoney;
            this.vermögensGini = vermögensGini;
            this.gehaltsGini = gehaltsGini;
            this.einkommenGini = einkommenGini;
            this.deltaPreis = deltaPreis;
            this.durchschnittArbeitslose = durchschnittArbeitslose;
        }
    }
}