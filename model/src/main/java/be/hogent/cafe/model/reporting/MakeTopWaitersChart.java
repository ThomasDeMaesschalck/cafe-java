package be.hogent.cafe.model.reporting;
import java.io.*;
import java.util.HashMap;

import be.hogent.cafe.model.Waiter;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

//NOG af te werken: exception handling insteken

public class MakeTopWaitersChart {

    private final HashMap<Waiter, Double> topwaiters;

    public MakeTopWaitersChart(HashMap<Waiter, Double> topWaiters) throws Exception {
        this.topwaiters = topWaiters;
        createJPG();
    }

    private void createJPG() throws Exception {

        DefaultPieDataset dataset = new DefaultPieDataset( );

        getTopwaiters().forEach((waiter, revenue) ->  { //dataset vullen
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
        File pieChart = new File( "reports/topwaiterchart.jpg" );
        ChartUtilities.saveChartAsJPEG( pieChart , chart , width , height );
    }

    private HashMap<Waiter, Double> getTopwaiters() {
        return topwaiters;
    }
}