package LoggerCore;

import java.io.File;
import java.util.ArrayList;

import javax.swing.JFrame;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.labels.XYSeriesLabelGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import LoggerCore.JFreeChartUtil.JFreeChartUtil;

public class LoggerApp extends JFrame {
    public boolean verbose = true;

    protected ChartPanel _panel;
    protected JFreeChart _chart;
    protected XYPlot _plot;

    protected XYSeriesCollection _loadedDataset;
    protected XYSeriesCollection _DisplayedDataset;

    protected ArrayList<String> _unityX;
    protected ArrayList<String> _unityY;

    public LoggerApp() {
        super();

        _loadedDataset = new XYSeriesCollection();
        _unityX = new ArrayList<String>();
        _unityY = new ArrayList<String>();

        _DisplayedDataset = new XYSeriesCollection();
        _plot = new XYPlot();
        _plot.setDataset(_DisplayedDataset);

        _plot.setDomainAxis(0, new NumberAxis("x"));
        _plot.setRangeAxis(0, new NumberAxis("y"));
        _plot.setRenderer(0, new XYLineAndShapeRenderer(true, false));
        _plot.getRenderer().setLegendItemLabelGenerator(new XYSeriesLabelGenerator() {
            @Override
            public String generateLabel(XYDataset dataset, int series) {
                return series + ": " + dataset.getSeriesKey(series).toString();
            }
        });

        _plot.getRenderer().setDefaultToolTipGenerator(new StandardXYToolTipGenerator());

        _chart = new JFreeChart(null, getFont(), _plot, true);
        JFreeChartUtil.AlternativeThemeLight(_chart);

        NumberAxis yAxis = (NumberAxis) _plot.getRangeAxis();
        NumberAxis xAxis = (NumberAxis) _plot.getDomainAxis();

        yAxis.setAutoRangeIncludesZero(false);
        xAxis.setAutoRangeIncludesZero(false);

        _panel = new ModChartPanel(_chart);

        _panel.setMinimumDrawWidth(0);
        _panel.setMinimumDrawHeight(0);
        _panel.setMaximumDrawWidth(Integer.MAX_VALUE);
        _panel.setMaximumDrawHeight(Integer.MAX_VALUE);

        setContentPane(_panel);

        setSize(500, 500);
        setLocationRelativeTo(null);
    }

    public boolean addXYSeries(XYSeries serie, String UnityX, String UnityY) {
        try {
            _loadedDataset.addSeries(serie);
            _unityX.add(UnityX);
            _unityY.add(UnityY);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean addXYSeries(XYSeries serie) {
        try {
            _loadedDataset.addSeries(serie);
            _unityX.add("x");
            _unityY.add("y");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void DisplaEveryLoadedSeries() {
        for (int i = 0; i < _loadedDataset.getSeriesCount(); i++)
            DisplayXYSeries(i);
    }

    public void DisplayXYSeries(int index) {
        try {
            if (_DisplayedDataset.getSeriesIndex(_loadedDataset.getSeries(index).getKey()) != -1)
                return;

            if (!_unityX.get(index).equals(_plot.getDomainAxis().getLabel())
                    || !_unityY.get(index).equals(_plot.getRangeAxis().getLabel())) {
                HideAllXYSeries();
                _plot.getDomainAxis().setLabel(_unityX.get(index));
                _plot.getRangeAxis().setLabel(_unityY.get(index));
                _DisplayedDataset.addSeries(_loadedDataset.getSeries(index));
            } else {
                _DisplayedDataset.addSeries(_loadedDataset.getSeries(index));
            }
        } catch (Exception e) {
            if (verbose)
                e.printStackTrace();
        }
    }

    public void DisplayXYSeries(Comparable key) {
        for (int i = 0; i < _loadedDataset.getSeriesCount(); i++)
            if (_loadedDataset.getSeriesKey(i).equals(key)) {
                DisplayXYSeries(i);
                return;
            }
    }

    public void DisplayXYSeries(XYSeries serie) {
        for (int i = 0; i < _loadedDataset.getSeriesCount(); i++)
            if (_loadedDataset.getSeries(i).equals(serie)) {
                DisplayXYSeries(i);
                return;
            }
    }

    public void HideXYSeries(int index) {
        _DisplayedDataset.removeSeries(_loadedDataset.getSeries(index));
    }

    public void HideAllXYSeries() {
        _DisplayedDataset.removeAllSeries();
    }

    public XYSeriesCollection getLoadedDataset() {
        return _loadedDataset;
    }

    public XYSeriesCollection getDisplayedDataset() {
        return _DisplayedDataset;
    }

    public XYPlot getPlot() {
        return _plot;
    }

    public JFreeChart getJFreeChart() {
        return _chart;
    }

    public ChartPanel getChartPanel() {
        return _panel;
    }

    public LoggerApp cloneWithSharedDataset() {
        LoggerApp clone = new LoggerApp();

        clone._loadedDataset = _loadedDataset;
        clone._unityX = _unityX;
        clone._unityY = _unityY;

        return clone;
    }

    public void ExportEveryLoadeXYSeries(File folder) {
        DataManger2 dataManager = new DataManger2(GlobalVar.defaulFormat);
        for (int i = 0; i < _loadedDataset.getSeriesCount(); i++) {
            XYSeries serie = _loadedDataset.getSeries(i);
            ArrayList<String> headers = new ArrayList<String>();
            headers.add(_unityX.get(i));
            headers.add(_unityY.get(i));
            dataManager.saveData(new File(folder, serie.getKey().toString() + ".txt"), headers, serie);
        }
    }

    public void ExportTextXYSeries(Comparable key, File file) {
        int index = _loadedDataset.getSeriesIndex(key);
        ExportTextXYSeries(file, index);
    }

    public void ExportTextXYSeries(File file, int index) {
        XYSeries serie = _loadedDataset.getSeries(index);
        DataManger2 dataManager = new DataManger2(GlobalVar.defaulFormat);
        ArrayList<String> headers = new ArrayList<String>();
        headers.add(_unityX.get(index));
        headers.add(_unityY.get(index));

        dataManager.saveData(new File(file.getAbsolutePath() + ".txt"), headers, serie);
    }

    public void EraseDataSeries() {
        for (int i = 0; i < _loadedDataset.getSeriesCount(); i++)
            _loadedDataset.getSeries(i).clear();
    }

    public void EraseAllSeries() {
        _DisplayedDataset.removeAllSeries();
        _loadedDataset.removeAllSeries();
        _unityX.clear();
        _unityY.clear();

    }

    public boolean IsDisplayed(XYSeries serie) {
        for (int i = 0; i < _DisplayedDataset.getSeriesCount(); i++) {
            if (_DisplayedDataset.getSeries(i).equals(serie))
                return true;
        }
        return false;
    }
}