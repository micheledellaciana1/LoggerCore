package LoggerCore.Zefiro.IVCharacteristic;

import java.awt.event.*;

import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;

import LoggerCore.LinkedAnalysisCollection;
import LoggerCore.LoggerFrame;
import LoggerCore.LoggerFrameMinimal;

public class PowerLawFitFrame extends LoggerFrameMinimal {

    protected LinkedAnalysisCollection _linkedCollection;
    protected PowerLowFitAnalysis _powerFitAnalysis;
    protected JCheckBoxMenuItem _runningCheckBox;

    public PowerLawFitFrame(LinkedAnalysisCollection linkedCollection) {
        super(false, false, true);
        setTitle("Power Law Fit");

        _linkedCollection = linkedCollection;

        _powerFitAnalysis = new PowerLowFitAnalysis();

        addXYSeries(_powerFitAnalysis.getSeries(), "Voltage[V]", "Error [mA]");
        addXYSeries(_powerFitAnalysis.getKSeries(), "time[sec]", "[1/kOhm]");
        addXYSeries(_powerFitAnalysis.getAlphaSeries(), "time[sec]", "n.u.");

        DisplayXYSeries(_powerFitAnalysis.getSeries());

        _runningCheckBox = BuildRunAnalyzerCheckBoxItem();
        getJMenuBar().add(_runningCheckBox);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                _powerFitAnalysis.removeFromAllCollections();
                setVisible(false);
            }
        });
    }

    public JMenuItem BuildShowFrameMenuItem() {
        final LoggerFrame frame = this;
        return new JMenuItem(new AbstractAction("Power Law Fit") {
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
                    if (!_linkedCollection.contains(_powerFitAnalysis))
                        _linkedCollection.add(_powerFitAnalysis);
                } else {
                    _powerFitAnalysis.removeFromAllCollections();
                }
            }
        });
        return runningCheckBox;
    }
}
