package LoggerCore.Moira.Analyzer.Frame;

import java.awt.event.*;

import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;

import LoggerCore.LoggerFrame;
import LoggerCore.LoggerFrameMinimal;
import LoggerCore.Moira.Moira;
import LoggerCore.Moira.VoltageMonitor;
import LoggerCore.Moira.Analyzer.AvgCurrentDUTAnalysis;
import LoggerCore.Moira.Analyzer.AvgVoltageAnalysis;

public class AverangeValueLogger extends LoggerFrameMinimal {

    protected Moira _Moira;
    protected VoltageMonitor _vm;
    protected AvgVoltageAnalysis _avgVoltageCh1;
    protected AvgVoltageAnalysis _avgVoltageCh2;
    protected AvgCurrentDUTAnalysis _avgCurrentDUTAnalysis;
    protected JCheckBoxMenuItem _runningCheckBox;

    public AverangeValueLogger(Moira Moira, VoltageMonitor vm) {
        super(false, false, true, false);
        setTitle("Averange values");
        _Moira = Moira;
        _vm = vm;
        _avgVoltageCh1 = new AvgVoltageAnalysis(0);
        _avgVoltageCh2 = new AvgVoltageAnalysis(1);

        _avgCurrentDUTAnalysis = new AvgCurrentDUTAnalysis(_Moira);

        addXYSeries(_avgVoltageCh1.getSeries(), "Time [S]", "[V]");
        addXYSeries(_avgVoltageCh2.getSeries(), "Time [S]", "[V]");
        addXYSeries(_avgCurrentDUTAnalysis.getSeries(), "Time [S]", "[A]");

        DisplayXYSeries(_avgVoltageCh1.getSeries());
        DisplayXYSeries(_avgVoltageCh2.getSeries());

        _runningCheckBox = BuildRunAnalyzerCheckBoxItem();
        getJMenuBar().add(_runningCheckBox);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                _avgVoltageCh1.removeFromAllCollections();
                _avgVoltageCh2.removeFromAllCollections();
                _avgCurrentDUTAnalysis.removeFromAllCollections();
                setVisible(false);
            }
        });
    }

    public JMenuItem BuildShowFrameMenuItem() {
        final LoggerFrame frame = this;
        return new JMenuItem(new AbstractAction("Averange values") {
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
                    if (!_vm.getLinkedAnalysisCollection().contains(_avgVoltageCh1))
                        _vm.getLinkedAnalysisCollection().add(_avgVoltageCh1);

                    if (!_vm.getLinkedAnalysisCollection().contains(_avgVoltageCh2))
                        _vm.getLinkedAnalysisCollection().add(_avgVoltageCh2);

                    if (!_vm.getLinkedAnalysisCollection().contains(_avgCurrentDUTAnalysis))
                        _avgVoltageCh2.getLinkedAnalysisCollection().add(_avgCurrentDUTAnalysis);
                } else {
                    _avgVoltageCh1.removeFromAllCollections();
                    _avgVoltageCh2.removeFromAllCollections();
                    _avgCurrentDUTAnalysis.removeFromAllCollections();
                }
            }
        });
        return runningCheckBox;
    }
}
