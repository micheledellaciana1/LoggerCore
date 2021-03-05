package LoggerCore.Menu;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;

import LoggerCore.GlobalVar;
import LoggerCore.LoggerFrame;
import LoggerCore.ThreadManager;
import LoggerCore.JFreeChartUtil.JFreeChartUtil;

public class MenuLoggerFrameFile extends BasicMenu {
    protected LoggerFrame _logger;

    public MenuLoggerFrameFile(LoggerFrame logger, String name) {
        super(name);
        _logger = logger;
    }

    public JMenuItem BuildDuplicateItem(final boolean includeFIFO) {
        return BuildNoArgMenuItem(new NoInputAction("Duplicate") {
            @Override
            public void actionPerformed() {
                LoggerFrame clone = _logger.cloneWithSharedDataset();
                JMenuBar menubar = new JMenuBar();

                MenuLoggerFrameFile fileMenu = new MenuLoggerFrameFile(clone, "file");
                fileMenu.add(fileMenu.BuildPropertyChartMenu(includeFIFO));
                fileMenu.add(new MenuLoggerFrameExportTxtFile(clone, "Export .txt").BuildDefault());
                fileMenu.add(fileMenu.BuildDuplicateItem(includeFIFO));

                menubar.add(fileMenu);
                menubar.add(new MenuLoggerFrameDisplay(clone, "Display").BuildDefault());
                clone.setJMenuBar(menubar);
                clone.setVisible(true);
            }
        });
    }

    public JMenu BuildPropertyChartMenu(boolean includeFIFO) {
        JMenu PropertyChartMenu = new JMenu("Property Chart");

        if (includeFIFO)
            PropertyChartMenu.add(new AbstractAction("FIFO scroll") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        String answer = JOptionPane.showInputDialog(
                                "Set dimension of domain window \n (to disable FIFO scroll enter a negative value):",
                                "50");
                        _logger.getPlot().getDomainAxis().setFixedAutoRange(Double.valueOf(answer));

                        NumberAxis yAxis = (NumberAxis) _logger.getPlot().getRangeAxis();

                        yAxis.setAutoRange(true);
                        yAxis.setAutoRangeIncludesZero(false);

                    } catch (Exception _e) {
                        if (verbose)
                            _e.printStackTrace();
                    }
                }
            });

        JMenu renderMenu = new JMenu("Series Renderer");

        renderMenu.add(new AbstractAction("Hide series legends") {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    _logger.getPlot().getRenderer().setDefaultSeriesVisibleInLegend(false);
                } catch (Exception _e) {
                    if (verbose)
                        _e.printStackTrace();
                }
            }
        });

        renderMenu.add(new AbstractAction("Show series legends") {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    _logger.getPlot().getRenderer().setDefaultSeriesVisibleInLegend(true);
                } catch (Exception _e) {
                    if (verbose)
                        _e.printStackTrace();
                }
            }
        });

        renderMenu.add(new AbstractAction("Hide points") {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    XYLineAndShapeRenderer.class.cast(_logger.getPlot().getRenderer()).setDefaultShapesVisible(false);
                } catch (Exception _e) {
                    if (verbose)
                        _e.printStackTrace();
                }
            }
        });

        renderMenu.add(new AbstractAction("Show points") {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    XYLineAndShapeRenderer.class.cast(_logger.getPlot().getRenderer()).setDefaultShapesVisible(true);
                } catch (Exception _e) {
                    if (verbose)
                        _e.printStackTrace();
                }
            }
        });

        PropertyChartMenu.add(renderMenu);

        PropertyChartMenu.add(new AbstractAction("Set window size") {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String args[] = JOptionPane.showInputDialog(null, "Enter: <XSize> <YSize>", "500 500").split(" ");
                    _logger.setSize(Integer.valueOf(args[0]), Integer.valueOf(args[1]));
                } catch (Exception _e) {
                    if (verbose)
                        _e.printStackTrace();
                }
            }
        });

        JMenu ThemeMenu = new JMenu("Theme");

        ThemeMenu.add(new AbstractAction("Dark Theme") {

            @Override
            public void actionPerformed(ActionEvent e) {
                JFreeChartUtil.AlternativeThemeDark(_logger.getJFreeChart());
            }
        });

        ThemeMenu.add(new AbstractAction("Light Theme") {

            @Override
            public void actionPerformed(ActionEvent e) {
                JFreeChartUtil.AlternativeThemeLight(_logger.getJFreeChart());
            }
        });

        PropertyChartMenu.add(ThemeMenu);

        return PropertyChartMenu;
    }

    public JMenuItem BuildEraseSeriesDataItem(String name, boolean ZeroGlobalTime) {
        return BuildNoArgMenuItem(new NoInputAction(name, "Erase all data " + _logger.getTitle(),
                UIManager.getIcon("OptionPane.warningIcon")) {
            public void actionPerformed() {
                switch (JOptionPane.showConfirmDialog(null, "Are you sure you want to delete all data?", "Erase data",
                        JOptionPane.WARNING_MESSAGE)) {
                    case 0:
                        _logger.EraseDataSeries();
                        GlobalVar.start = System.currentTimeMillis();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private FrameThreadManager _frameThreadManager = null;

    public JMenuItem BuildThreadManagerItem(final String name, final ThreadManager tm) {
        return new JMenuItem(new AbstractAction(name) {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (_frameThreadManager != null)
                    _frameThreadManager.dispose();

                _frameThreadManager = new FrameThreadManager(name, tm);
            }
        });
    }

    public JMenuItem BuildEraseSeriesItem(String name) {
        return BuildNoArgMenuItem(new NoInputAction(name, "Erase all series " + _logger.getTitle(),
                UIManager.getIcon("OptionPane.warningIcon")) {
            @Override
            public void actionPerformed() {
                switch (JOptionPane.showConfirmDialog(null, "Are you sure you want to delete all series?",
                        "Erase series", JOptionPane.WARNING_MESSAGE)) {
                    case 0:
                        _logger.removeAllSeries();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public JMenu BuildDefault() {
        return null;
    }
}
