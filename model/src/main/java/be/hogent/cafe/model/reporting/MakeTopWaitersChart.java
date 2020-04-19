package be.hogent.cafe.model.reporting;
import java.io.*;
import java.util.HashMap;

import be.hogent.cafe.model.Cafe;
import be.hogent.cafe.model.Waiter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

public class MakeTopWaitersChart {

    //exception handling nog af te werken

    private static final Logger logger = LogManager.getLogger (Cafe.class.getName ());
    private static String reportsDirectory = Cafe.getReportsDirectory();

    public static boolean createJPG(HashMap<Waiter, Double> topWaiters) throws IOException {

        DefaultPieDataset dataset = new DefaultPieDataset( );

        topWaiters.forEach((waiter, revenue) ->  { //dataset vullen
            dataset.setValue((waiter.toString()), revenue);
        });

          JFreeChart chart = ChartFactory.createPieChart(
                "Top waiters in Cafe Java",   // chart title
                dataset,          // data
                true,             // include legend
                true,
                false);

        int width = 640;   /* Width of the image */
        int height = 480;  /* Height of the image */


        File pieChart = new File( reportsDirectory + "/topwaiterchart.jpg" );
        File parent = pieChart.getParentFile();
        if (!parent.exists() && !parent.mkdirs()) {
            throw new IllegalStateException("Couldn't create dir: " + parent);
        }
        ChartUtilities.saveChartAsJPEG( pieChart , chart , width , height );
        if (pieChart.exists()) {
            return true;
        } else
        {
            logger.error("PDF file creation failed");
            return false;
        }
    }
}