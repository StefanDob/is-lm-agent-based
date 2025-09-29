package islm.export;

import tech.tablesaw.api.Table;
import tech.tablesaw.api.DoubleColumn;

import org.knowm.xchart.Histogram;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;

import islm.ISLMBuilder;

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import repast.simphony.engine.schedule.ScheduledMethod;
/**
 * Utility class for generating visualizations from simulation output.
 * 
 * <p>This class reads the daily CSV output from the ISLM simulation and
 * generates a histogram plot of the unmet demand ratio across all households.
 * Zero and non-zero values are handled separately to clearly show density
 * at zero.</p>
 * 
 * <p>The plot is saved as a PNG file at the end of the simulation
 * ({@link ISLMBuilder#TOTAL_SIMULATION_TICKS}).</p>
 * 
 * The chart is not used in the actual paper - just for visualisation
 */
public class DataPlotter {

	/**
     * Reads daily unmet demand data from CSV and generates a density plot
     * showing the distribution of unmet demand ratios across households.
     * Zero values are treated separately to highlight cases with no unmet demand.
     * The plot is exported as a PNG file in the "output" directory.
     */
    @ScheduledMethod(start = ISLMBuilder.TOTAL_SIMULATION_TICKS, interval = 1, priority = 1000)
    public void exportAtTheEnd() {
        String inputCsv = "output/islm_daily_output.csv";
        String outputImage = "output/unmet_demand_density.png";

        try {
            Table table = Table.read().csv(inputCsv);
            DoubleColumn unmetDemand = table.doubleColumn("UnmetDemandRatio");
            List<Double> values = unmetDemand.asList();

            // Separate zeros and non-zeros
            List<Double> zeros = new ArrayList<>();
            List<Double> nonZeros = new ArrayList<>();
            for (Double v : values) {
                if (v == 0.0) {
                    zeros.add(v);
                } else {
                    nonZeros.add(v);
                }
            }

            // Create histogram for non-zero values (e.g., 29 bins)
            Histogram nonZeroHist = new Histogram(nonZeros, 29);

            // Manually add a bin for zeros at the beginning
            List<Double> xData = new ArrayList<>();
            List<Double> yData = new ArrayList<>();

            // Add zero bin
            xData.add(0.0);
            yData.add((double) zeros.size() / values.size() / 1); // density

            // Add non-zero bins
            for (int i = 0; i < nonZeroHist.getxAxisData().size(); i++) {
                xData.add(nonZeroHist.getxAxisData().get(i));
                yData.add(nonZeroHist.getyAxisData().get(i));
            }

            // Build chart
            XYChart chart = new XYChartBuilder()
                    .width(800)
                    .height(600)
                    .title("Probability Density of Unmet Demand")
                    .xAxisTitle("Unmet Demand Ratio")
                    .yAxisTitle("Density")
                    .build();

            chart.getStyler().setXAxisDecimalPattern("0.00");
            chart.addSeries("UnmetDemandRatio", xData, yData);

            BitmapEncoder.saveBitmap(chart, outputImage, BitmapFormat.PNG);
            System.out.println("PDF plot written to " + outputImage);

        } catch (IOException e) {
            System.err.println("Error processing the CSV file or saving the image: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
