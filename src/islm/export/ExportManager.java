package islm.export;

import islm.ISLMBuilder;
import repast.simphony.engine.schedule.ScheduledMethod;

/**
 * Manages the coordinated export of simulation data in multiple formats.
 * 
 * <p>This class acts as a central manager for exporting simulation results via:
 * <ul>
 *   <li>{@link DataPlotter} – generates plots at the end of the simulation</li>
 *   <li>{@link ExcelExporter} – collects and writes daily and monthly data to Excel</li>
 *   <li>{@link CSVExporter} – exports daily, monthly, and semi-annual data to CSV</li>
 * </ul>
 * </p>
 * 
 * <p>The class uses scheduled methods to automatically trigger data collection and export:
 * <ul>
 *   <li>{@code dailyData()} – collects daily metrics and exports them to CSV and Excel</li>
 *   <li>{@code monthlyData()} – collects monthly metrics and exports them</li>
 *   <li>{@code exportAtTheEnd()} – final export at the end of the simulation, including plotting</li>
 * </ul>
 * </p>
 */
public class ExportManager {
	
	DataPlotter dataPlotter;
	ExcelExporter excelExporter;
	CSVExporter csvExporter;
	
	/**
     * Initializes the data exporters and plotter.
     */
	public ExportManager() {
		dataPlotter = new DataPlotter();
		excelExporter = new ExcelExporter();
		csvExporter = new CSVExporter();
	}
	
	/**
     * Triggers final data export and plotting at the end of the simulation.
     */
	@ScheduledMethod(start = ISLMBuilder.TOTAL_SIMULATION_TICKS, interval = 1, priority = 3)
    public void exportAtTheEnd() {
		dataPlotter.exportAtTheEnd();
		excelExporter.writeToExcel();
	}
	
	/**
     * Collects and exports daily data to CSV and Excel.
     */
	@ScheduledMethod(start = 987, interval = 1, priority = 3)
    public void dailyData() {
		excelExporter.collectDailyData();
		csvExporter.exportDailyData();
	}
	
	/**
     * Collects and exports monthly data to CSV and Excel.
     */
	 @ScheduledMethod(start = 987, interval = 21, priority = 3)
	 public void monthlyData() {
		 excelExporter.collectMonthlyData();
		 csvExporter.exportMonthlyData();
	 }
}
