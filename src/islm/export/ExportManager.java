package islm.export;

import islm.ISLMBuilder;
import repast.simphony.engine.schedule.ScheduledMethod;

public class ExportManager {
	
	DataPlotter dataPlotter;
	ExcelExporter excelExporter;
	CSVExporter csvExporter;
	
	
	public ExportManager() {
		dataPlotter = new DataPlotter();
		excelExporter = new ExcelExporter();
		csvExporter = new CSVExporter();
	}
	
	
	@ScheduledMethod(start = ISLMBuilder.TOTAL_SIMULATION_TICKS, interval = 1, priority = 3)
    public void exportAtTheEnd() {
		dataPlotter.exportAtTheEnd();
		excelExporter.writeToExcel();
	}
	
	@ScheduledMethod(start = 6000, interval = 1, priority = 3)
    public void dailyData() {
		excelExporter.collectDailyData();
		csvExporter.exportDailyData();
	}
	
	 @ScheduledMethod(start = 6000, interval = 21, priority = 3)
	 public void monthlyData() {
		 excelExporter.collectMonthlyData();
		 csvExporter.exportMonthlyData();
	 }
}
