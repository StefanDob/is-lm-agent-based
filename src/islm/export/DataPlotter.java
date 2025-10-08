package islm.export;

import tech.tablesaw.api.Table;
import tech.tablesaw.api.DoubleColumn;

import org.knowm.xchart.Histogram;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;

import java.io.IOException;
import java.util.List;

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
	
	    public void exportAtTheEnd() {
		String inputCsv = "output/islm_daily_output.csv";   
        String outputImage = "output/unmet_demand_density.png";

        

        // Load CSV using Tablesaw
        Table table = Table.read().csv(inputCsv);

        // Extract the unmet demand column
        DoubleColumn unmetDemand = table.doubleColumn("UnmetDemandRatio");

        // Convert to Java list
        List<Double> values = unmetDemand.asList();

        // Create histogram data (probability density)
        Histogram histogram = new Histogram(values, 30); // 30 bins

        // Build chart
        XYChart chart = new XYChartBuilder()
                .width(800)
                .height(600)
                .title("Probability Density of Unmet Demand")
                .xAxisTitle("Unmet Demand Ratio")
                .yAxisTitle("Density")
                .build();
        
        chart.getStyler().setXAxisDecimalPattern("0.00");

        chart.addSeries("UnmetDemandRatio", histogram.getxAxisData(), histogram.getyAxisData());

        // Export to PNG
        try {
			BitmapEncoder.saveBitmap(chart, outputImage, BitmapFormat.PNG);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        System.out.println("PDF plot written to " + outputImage);
	}

}