package LoggerCore.Zefiro.ITCharacteristic;

import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jfree.data.xy.XYSeries;

import LoggerCore.GlobalVar;
import LoggerCore.LoggerFrame;
import LoggerCore.StoppableRunnable;
import LoggerCore.Menu.MenuLoggerFrameFile;
import LoggerCore.themal.IFeedbackController;

public class FrameThermalRecepie extends LoggerFrame {

    protected int _Ncycle;
    protected int _cycleCounter;

    protected long _start;
    protected int _indexPath;

    protected StoppableRunnable _ThermalCook;
    protected IFeedbackController _fc;

    JCheckBox _runningCheckBox;

    public FrameThermalRecepie(IFeedbackController fc) {
        super();
        _fc = fc;

        setTitle("Thermal Recepie");
        MenuLoggerFrameFile fileMenu = new MenuLoggerFrameFile(this, "File");
        fileMenu.add(fileMenu.BuildPropertyChartMenu(false));
        fileMenu.add(BuildImportRecepieItem("Import recepie"));

        _runningCheckBox = new JCheckBox();
        AbstractAction action = new AbstractAction("Run recepie") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (_runningCheckBox.isSelected()) {
                    try {
                        _indexPath = 0;
                        if (_ThermalCook.getisRunning())
                            return;
                        else {
                            _start = System.currentTimeMillis();
                            Thread t = new Thread(_ThermalCook);
                            t.start();
                        }
                    } catch (Exception _e) {
                        _e.printStackTrace();
                    }
                } else {
                    while (_ThermalCook.getisRunning())
                        _ThermalCook.kill();
                }
            }
        };

        _runningCheckBox.setAction(action);

        JMenuBar menubar = new JMenuBar();
        menubar.add(fileMenu);
        menubar.add(_runningCheckBox);
        setJMenuBar(menubar);

        _ThermalCook = new StoppableRunnable() {

            @Override
            public void MiddleLoop() {
                if (System.currentTimeMillis()
                        - _start > getDisplayedDataset().getSeries(0).getX(_indexPath).doubleValue() * 1000) {
                    _fc.set_target_value(getDisplayedDataset().getSeries(0).getY(_indexPath).doubleValue());
                    _indexPath++;
                    if (_indexPath >= getDisplayedDataset().getSeries(0).getItemCount()) {
                        kill();
                    }
                }
            }

            @Override
            public synchronized void kill() {
                _runningCheckBox.setSelected(false);
                super.kill();
            }
        };

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                setVisible(false);
                if (_ThermalCook.getisRunning()) {
                    _ThermalCook.kill();
                }
            }
        });

        _ThermalCook.set_firstDelay(true);
        _ThermalCook.set_secondDelay(false);
        _ThermalCook.setExecutionDelay(100);
    }

    public JMenuItem BuildImportRecepieItem(String name) {
        final LoggerFrame logger = this;
        JMenuItem item = new JMenuItem(new AbstractAction(name) {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Import recepie");
                fileChooser.setApproveButtonText("Import");
                fileChooser.setFileFilter(new FileNameExtensionFilter(".txt", "txt"));

                if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    ArrayList<ArrayList<Double>> recepieRaw = GlobalVar.defaultDataManager
                            .parseDoubleColumnsAUTO(fileChooser.getSelectedFile());

                    XYSeries recepie = new XYSeries("Recepie");
                    double actualX = 0;
                    for (int i = 0; i < recepieRaw.get(0).size(); i++) {
                        recepie.add(actualX, recepieRaw.get(1).get(i));
                        actualX += recepieRaw.get(0).get(i);
                        recepie.add(actualX, recepieRaw.get(1).get(i));
                    }

                    logger.getDisplayedDataset().removeAllSeries();
                    logger.getLoadedDataset().removeAllSeries();
                    logger.addXYSeries(recepie, "Time[sec]", "Target Temperature[°C]");
                    logger.DisplaEveryLoadedSeries();
                }
            }
        });

        item.setIcon(UIManager.getIcon("FileView.fileIcon"));
        return item;
    }

    public JMenuItem BuildDisplayFrameMenuItem(String label) {
        JMenuItem menuItem = new JMenuItem(new AbstractAction(label) {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(true);
            }
        });
        return menuItem;
    }
}
