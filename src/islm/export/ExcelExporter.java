package islm.export;

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

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ExcelExporter {

	private final String outputPath = "output/islm_output.xlsx";

    private final List<DailyData> dailyDataList = new ArrayList<>();
    private final List<MonthlyData> monthlyDataList = new ArrayList<>();

    public ExcelExporter() {
        try {
            Files.createDirectories(Paths.get("output"));
            Files.deleteIfExists(Paths.get(outputPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    public void collectDailyData() {
        double tick = RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
        double unmetDemandRatio = DataCollecter.getUnmetDemandRatioDurchschnitt();

        dailyDataList.add(new DailyData(tick, unmetDemandRatio));
    }

    
    public void collectMonthlyData() {
        double tick = RunEnvironment.getInstance().getCurrentSchedule().getTickCount();

        monthlyDataList.add(new MonthlyData(
                tick,
                DataCollecter.getEmployedCount(),
                DataCollecter.beschäftigteLeute(),
                DataCollecter.getOpenPositions(),
                DataCollecter.getDurchschnittsgehalt(),
                DataCollecter.getDurchSchnittspreis(),
                DataCollecter.getDurchSchnittsinventar(),
                DataCollecter.getGesamtNachfrage(),
                DataCollecter.getGeplanterMonatlicherKonsum(),
                DataCollecter.getAllUnternehmenMoney(),
                DataCollecter.getAllHouseholdMoney(),
                DataCollecter.getAllMoney()
        ));
    }

    

    public void writeToExcel() {
        Workbook workbook = new XSSFWorkbook();

        // Daily sheet
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

        // Monthly sheet
        Sheet monthlySheet = workbook.createSheet("Monthly Data");
        Row monthlyHeader = monthlySheet.createRow(0);
        String[] headers = {
                "Tick", "EmployedHouseholds", "BeschäftigteLeute", "OpenPositions",
                "Durchschnittsgehalt", "Durchschnittspreis", "Durchschnittsinventar",
                "GesamtNachfrage", "GeplanterMonatlicherKonsum",
                "UnternehmenMoney", "HaushalteMoney", "AllMoney"
        };
        for (int i = 0; i < headers.length; i++) {
            monthlyHeader.createCell(i).setCellValue(headers[i]);
        }

        int monthlyRowIndex = 1;
        for (MonthlyData m : monthlyDataList) {
            Row row = monthlySheet.createRow(monthlyRowIndex++);
            row.createCell(0).setCellValue(m.tick);
            row.createCell(1).setCellValue(m.employedCount);
            row.createCell(2).setCellValue(m.beschäftigteLeute);
            row.createCell(3).setCellValue(m.openPositions);
            row.createCell(4).setCellValue(m.durchschnittsgehalt);
            row.createCell(5).setCellValue(m.durchschnittspreis);
            row.createCell(6).setCellValue(m.durchschnittsinventar);
            row.createCell(7).setCellValue(m.gesamtNachfrage);
            row.createCell(8).setCellValue(m.geplanterMonatlicherKonsum);
            row.createCell(9).setCellValue(m.allUnternehmenMoney);
            row.createCell(10).setCellValue(m.allHouseholdMoney);
            row.createCell(11).setCellValue(m.allMoney);
        }

        try (FileOutputStream fos = new FileOutputStream(outputPath)) {
            workbook.write(fos);
            System.out.println("Data successfully written to " + outputPath + " at tick " +
                    RunEnvironment.getInstance().getCurrentSchedule().getTickCount());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class DailyData {
        final double tick;
        final double unmetDemandRatio;

        DailyData(double tick, double unmetDemandRatio) {
            this.tick = tick;
            this.unmetDemandRatio = unmetDemandRatio;
        }
        
    }

    private static class MonthlyData {
        final double tick;
        final int employedCount;
        final int beschäftigteLeute;
        final int openPositions;
        final double durchschnittsgehalt;
        final double durchschnittspreis;
        final double durchschnittsinventar;
        final double gesamtNachfrage;
        final double geplanterMonatlicherKonsum;
        final double allUnternehmenMoney;
        final double allHouseholdMoney;
        final double allMoney;

        MonthlyData(double tick, int employedCount, int beschäftigteLeute, int openPositions,
                    double durchschnittsgehalt, double durchschnittspreis, double durchschnittsinventar,
                    double gesamtNachfrage, double geplanterMonatlicherKonsum,
                    double allUnternehmenMoney, double allHouseholdMoney, double allMoney) {
            this.tick = tick;
            this.employedCount = employedCount;
            this.beschäftigteLeute = beschäftigteLeute;
            this.openPositions = openPositions;
            this.durchschnittsgehalt = durchschnittsgehalt;
            this.durchschnittspreis = durchschnittspreis;
            this.durchschnittsinventar = durchschnittsinventar;
            this.gesamtNachfrage = gesamtNachfrage;
            this.geplanterMonatlicherKonsum = geplanterMonatlicherKonsum;
            this.allUnternehmenMoney = allUnternehmenMoney;
            this.allHouseholdMoney = allHouseholdMoney;
            this.allMoney = allMoney;
        }
    }
}