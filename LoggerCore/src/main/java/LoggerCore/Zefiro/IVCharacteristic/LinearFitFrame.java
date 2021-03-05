package LoggerCore.Zefiro.IVCharacteristic;

import java.awt.event.*;

import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;

import LoggerCore.LinkedAnalysisCollection;
import LoggerCore.LoggerFrame;
import LoggerCore.LoggerFrameMinimal;

public class LinearFitFrame extends LoggerFrameMinimal {

    protected LinkedAnalysisCollection _linkedCollection;
    protected LinearFitAnalysis _LinearFitAnalysis;
    protected JCheckBoxMenuItem _runningCheckBox;

    public LinearFitFrame(LinkedAnalysisCollection linkedCollection) {
        super(false, false, true);
        setTitle("Linear Fit");

        _linkedCollection = linkedCollection;

        _LinearFitAnalysis = new LinearFitAnalysis();

        addXYSeries(_LinearFitAnalysis.getSeries(), "Voltage[V]", "Error [mA]");
        addXYSeries(_LinearFitAnalysis.getSlopeSeries(), "time[sec]", "[1/kOhm]");
        addXYSeries(_LinearFitAnalysis.getInterceptSeries(), "time[sec]", "[mA]");

        DisplayXYSeries(_LinearFitAnalysis.getSeries());

        _runningCheckBox = BuildRunAnalyzerCheckBoxItem();
        getJMenuBar().add(_runningCheckBox);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                _LinearFitAnalysis.removeFromAllCollections();
                setVisible(false);
            }
        });
    }

    public JMenuItem BuildShowFrameMenuItem() {
        final LoggerFrame frame = this;
        return new JMenuItem(new AbstractAction("Linear Fit") {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setVisible(true);
                _runningCheckBox.setSelected(true);
                _runningCheckBox.getAction().actionPerformed(null);
            }
        });
    }

    private JCheckBoxMenuItem BuildRunAnalyzerCheckBoxItem() {
        final JCheckBoxMenuItem runningCheckBox = new JCheckBoxMenuItem();

        runningCheckBox.setAction(new AbstractAction("RUNNING") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (runningCheckBox.isSelected()) {
                    if (!_linkedCollection.contains(_LinearFitAnalysis))
                        _linkedCollection.add(_LinearFitAnalysis);
                } else {
                    _LinearFitAnalysis.removeFromAllCollections();
                }
            }
        });
        return runningCheckBox;
    }
}
