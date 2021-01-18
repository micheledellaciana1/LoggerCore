package LoggerCore;

import javax.swing.SwingUtilities;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.Range;
import org.jfree.data.general.DatasetUtils;

import java.awt.event.*;

public class ModChartPanel extends ChartPanel {

    public ModChartPanel(JFreeChart chart) {
        super(chart);
        setMouseWheelEnabled(true);
        setFocusable(true);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
            }
        });
    }

    private int _XPressedMouse = 0;
    private int _YPressedMouse = 0;

    @Override
    public void mousePressed(MouseEvent arg0) {
        super.mousePressed(arg0);

        _XPressedMouse = arg0.getX();
        _YPressedMouse = arg0.getY();
    }

    @Override
    public void mouseClicked(MouseEvent arg0) {
        super.mouseClicked(arg0);
        if (arg0.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(arg0)) {
            _setMaxExtention();
        }
    }

    @Override
    public void mouseDragged(MouseEvent arg0) {
        if (SwingUtilities.isLeftMouseButton(arg0)) {
            super.mouseDragged(arg0);
        }

        if (SwingUtilities.isMiddleMouseButton(arg0)) {
            int dirX = arg0.getX() - _XPressedMouse;
            int dirY = arg0.getY() - _YPressedMouse;

            _XPressedMouse = arg0.getX();
            _YPressedMouse = arg0.getY();

            Range BoundX = getChart().getXYPlot().getDomainAxis().getRange();
            Range BoundY = getChart().getXYPlot().getRangeAxis().getRange();

            double IncrementX = (BoundX.getLength() * 0.5) / getSize().width;
            double IncrementY = (BoundY.getLength() * 0.5) / getSize().height;

            BoundX = new Range(BoundX.getLowerBound() - dirX * IncrementX, BoundX.getUpperBound() - dirX * IncrementX);
            BoundY = new Range(BoundY.getLowerBound() + dirY * IncrementY, BoundY.getUpperBound() + dirY * IncrementY);

            if (BoundX != null && BoundX.getLowerBound() != BoundX.getUpperBound())
                getChart().getXYPlot().getDomainAxis().setRange(BoundX);

            if (BoundY != null && BoundY.getLowerBound() != BoundY.getUpperBound())
                getChart().getXYPlot().getRangeAxis().setRange(BoundY);
        }
    }

    private void _setMaxExtention() {
        Range BoundX = DatasetUtils.findDomainBounds(getChart().getXYPlot().getDataset(), false);
        Range BoundY = DatasetUtils.findRangeBounds(getChart().getXYPlot().getDataset(), false);

        if (BoundX != null && BoundX.getLowerBound() != BoundX.getUpperBound())
            getChart().getXYPlot().getDomainAxis().setRange(BoundX);

        if (BoundY != null && BoundY.getLowerBound() != BoundY.getUpperBound())
            getChart().getXYPlot().getRangeAxis().setRange(BoundY);
    }
}