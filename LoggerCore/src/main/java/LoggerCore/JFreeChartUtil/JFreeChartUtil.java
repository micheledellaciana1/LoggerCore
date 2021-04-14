package LoggerCore.JFreeChartUtil;

import java.awt.*;
import java.awt.geom.*;

import org.jfree.chart.JFreeChart;

public class JFreeChartUtil {

    static public void AlternativeThemeLight(JFreeChart chart) {
        Color ColorWrite = new Color(30, 30, 30, 255);
        Color ColorThing = new Color(30, 30, 30, 255);
        Color ColorBackGroundChart = new Color(255, 255, 255, 255);
        Color ColorBackGroundPlot = new Color(255, 255, 255, 255);
        Color ColorGrid = new Color(200, 200, 200, 255);

        Font fontLabel = new Font("Calibri", Font.PLAIN, 16);
        Font fontTitle = new Font("Calibri", Font.PLAIN, 18);

        chart.getLegend().setItemFont(fontTitle);
        chart.getLegend().setBackgroundPaint(ColorBackGroundChart);
        chart.getLegend().setItemPaint(ColorWrite);

        chart.getXYPlot().getDomainAxis().setTickLabelFont(fontLabel);
        chart.getXYPlot().getDomainAxis().setLabelPaint(ColorWrite);
        chart.getXYPlot().getDomainAxis().setTickMarkOutsideLength(4);
        chart.getXYPlot().getDomainAxis().setTickMarkPaint(ColorThing);
        chart.getXYPlot().getDomainAxis().setTickLabelPaint(ColorWrite);
        chart.getXYPlot().getDomainAxis().setLabelFont(fontTitle);
        chart.getXYPlot().getRangeAxis().setTickLabelFont(fontLabel);
        chart.getXYPlot().getRangeAxis().setLabelPaint(ColorWrite);
        chart.getXYPlot().getRangeAxis().setTickMarkOutsideLength(4);
        chart.getXYPlot().getRangeAxis().setTickMarkPaint(ColorThing);
        chart.getXYPlot().getRangeAxis().setTickLabelPaint(ColorWrite);
        chart.getXYPlot().getRangeAxis().setLabelFont(fontTitle);
        chart.getXYPlot().setRangeGridlinePaint(ColorGrid);
        chart.getXYPlot().setDomainGridlinePaint(ColorGrid);
        chart.getXYPlot().setOutlinePaint(ColorThing);
        chart.setBackgroundPaint(ColorBackGroundChart);
        chart.getXYPlot().setBackgroundPaint(ColorBackGroundPlot);
    }

    static public void AlternativeThemeDark(JFreeChart chart) {
        Color ColorWrite = new Color(205, 205, 205, 255);
        Color ColorThing = new Color(205, 205, 205, 255);
        Color ColorBackGroundChart = new Color(40, 40, 40, 255);
        Color ColorBackGroundPlot = new Color(40, 40, 40, 255);
        Color ColorGrid = new Color(80, 80, 80, 255);

        Font fontLabel = new Font("Calibri", Font.PLAIN, 16);
        Font fontTitle = new Font("Calibri", Font.PLAIN, 18);

        chart.getLegend().setItemFont(fontTitle);
        chart.getLegend().setBackgroundPaint(ColorBackGroundChart);
        chart.getLegend().setItemPaint(ColorWrite);

        chart.getXYPlot().getDomainAxis().setTickLabelFont(fontLabel);
        chart.getXYPlot().getDomainAxis().setLabelPaint(ColorWrite);
        chart.getXYPlot().getDomainAxis().setTickMarkOutsideLength(4);
        chart.getXYPlot().getDomainAxis().setTickMarkPaint(ColorThing);
        chart.getXYPlot().getDomainAxis().setTickLabelPaint(ColorWrite);
        chart.getXYPlot().getDomainAxis().setLabelFont(fontTitle);
        chart.getXYPlot().getRangeAxis().setTickLabelFont(fontLabel);
        chart.getXYPlot().getRangeAxis().setLabelPaint(ColorWrite);
        chart.getXYPlot().getRangeAxis().setTickMarkOutsideLength(4);
        chart.getXYPlot().getRangeAxis().setTickMarkPaint(ColorThing);
        chart.getXYPlot().getRangeAxis().setTickLabelPaint(ColorWrite);
        chart.getXYPlot().getRangeAxis().setLabelFont(fontTitle);
        chart.getXYPlot().setRangeGridlinePaint(ColorGrid);
        chart.getXYPlot().setDomainGridlinePaint(ColorGrid);
        chart.getXYPlot().setOutlinePaint(ColorThing);
        chart.setBackgroundPaint(ColorBackGroundChart);
        chart.getXYPlot().setBackgroundPaint(ColorBackGroundPlot);
    }

    public static boolean checkPointValidity(Point2D p) {
        if (p == null)
            return false;

        if (Double.isNaN(p.getX()))
            return false;

        if (Double.isNaN(p.getY()))
            return false;

        if (Double.isInfinite(p.getX()))
            return false;

        if (Double.isInfinite(p.getY()))
            return false;

        return true;
    }
}
