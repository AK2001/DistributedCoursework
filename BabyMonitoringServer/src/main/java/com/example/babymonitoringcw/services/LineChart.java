package com.example.babymonitoringcw.services;

import com.example.babymonitoringcw.Model.FeedingRecord;
import com.example.babymonitoringcw.dao.FeedingRecordsDAO;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.chart.axis.DateAxis;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;

public class LineChart extends JFrame{
    private FeedingRecordsDAO feedingRecordsDAO;

    private Timestamp startingDate;

    private Timestamp endingDate;

    private byte[] chartImage;

    public LineChart(FeedingRecordsDAO feedingRecordsDAO, Timestamp startingDate, Timestamp endingDate) {
        this.feedingRecordsDAO = feedingRecordsDAO;
        this.startingDate = startingDate;
        this.endingDate = endingDate;
        initChart();
    }

    private void initChart() {
        XYDataset dataset = generateDataset(startingDate, endingDate);
        JFreeChart chart = generateChart(dataset);
        byte[] image = null;
        try {
            image = ChartUtilities.encodeAsPNG(chart.createBufferedImage(550, 500));
        } catch (IOException e) {
            System.out.println(e);
        }
        chartImage = image;
    }

    private XYDataset generateDataset(Timestamp startingDate, Timestamp endingDate) {
        TimeSeries milkConsumptionSeries = new TimeSeries("Milk Consumption (ml)");
        TimeSeries feedingDurationSeries = new TimeSeries("Feeding Duration (minutes)");

        List<FeedingRecord> feedingRecords = feedingRecordsDAO.listFeedingRecord(startingDate, endingDate);
        feedingRecords.sort(Comparator.comparing(FeedingRecord::getFinishTime));

        feedingRecords.forEach(record -> {
            milkConsumptionSeries.addOrUpdate(new Day(record.getFinishTime()), record.getMilkConsumed());

            long duration = Duration.between(record.getStartTime().toInstant(), record.getFinishTime().toInstant()).toMinutes();
            feedingDurationSeries.addOrUpdate(new Day(record.getFinishTime()), duration);
        });

        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(milkConsumptionSeries);
        dataset.addSeries(feedingDurationSeries);

        return dataset;
    }

    private JFreeChart generateChart(XYDataset dataset) {
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Baby Feeding Chart",
                "Date",
                "Value",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        XYPlot plot = chart.getXYPlot();
        DateAxis dateAxis = new DateAxis("Date");
        dateAxis.setDateFormatOverride(new SimpleDateFormat("yyyy-MM-dd")); // Set desired date format
        plot.setDomainAxis(dateAxis);

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, Color.RED);
        renderer.setSeriesStroke(0, new BasicStroke(2.0f));
        renderer.setSeriesPaint(1, Color.BLUE);
        renderer.setSeriesStroke(1, new BasicStroke(2.0f));
        plot.setRenderer(renderer);
        plot.setBackgroundPaint(Color.white);
        plot.setRangeGridlinesVisible(false);
        plot.setDomainGridlinesVisible(false);

        chart.getLegend().setFrame(BlockBorder.NONE);
        chart.setTitle(new TextTitle("Baby Feeding Chart", new Font("Arial", Font.BOLD, 16)));

        return chart;
    }

    public byte[] getChartImagePNG() {
        return chartImage;
    }
}
