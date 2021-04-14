package LoggerCore.Moira.Analyzer.Frame;

import java.awt.event.*;

import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;

import LoggerCore.LoggerFrame;
import LoggerCore.LoggerFrameMinimal;
import LoggerCore.Moira.AnalogDiscovery2;
import LoggerCore.Moira.Moira;
import LoggerCore.Moira.VoltageMonitor;
import LoggerCore.Moira.Analyzer.HarmonicAnalysis;

public class HarmonicAnalyzerLogger extends LoggerFrameMinimal {

    protected Moira _moira;
    protected VoltageMonitor _vm;
    protected HarmonicAnalysis _harmonicAnalysis;
    protected JCheckBoxMenuItem _runningCheckBox;

    public HarmonicAnalyzerLogger(Moira moira, VoltageMonitor vm) {
        super(false, false, true, false);
        setTitle("Harmonic Analyzer");

        _moira = moira;
        _vm = vm;
        _harmonicAnalysis = new HarmonicAnalysis(moira);

        _runningCheckBox = BuildRunAnalyzerCheckBoxItem();
        getJMenuBar().add(_runningCheckBox);

        addXYSeries(_harmonicAnalysis.getAmplitudeImpedence(), "[S]", "[Ohm]");
        addXYSeries(_harmonicAnalysis.getVoltageAmplitude(), "[S]", "[V]");
        addXYSeries(_harmonicAnalysis.getCurrentAmplitude(), "[S]", "[A]");
        addXYSeries(_harmonicAnalysis.getPhaseShift(), "[S]", "[rad]");
        addXYSeries(_harmonicAnalysis.getVoltageFreq(), "[S]", "[Hz]");
        addXYSeries(_harmonicAnalysis.getCurrentFreq(), "[S]", "[Hz]");

        DisplayXYSeries(_harmonicAnalysis.getAmplitudeImpedence());

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                _harmonicAnalysis.removeFromAllCollections();
                setVisible(false);
            }
        });
    }

    public JMenuItem BuildShowFrameMenuItem() {
        final LoggerFrame frame = this;
        return new JMenuItem(new AbstractAction("Harmonic Analyzer") {
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
                    if (!_vm.getLinkedAnalysisCollection().contains(_harmonicAnalysis))
                        _vm.getLinkedAnalysisCollection().add(_harmonicAnalysis);
                } else {
                    _harmonicAnalysis.removeFromAllCollections();
                }
            }
        });
        return runningCheckBox;
    }
}
