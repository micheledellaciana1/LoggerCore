package LoggerCore.Moira.Analyzer.Frame;

import java.awt.event.*;

import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;

import LoggerCore.LoggerFrame;
import LoggerCore.LoggerFrameMinimal;
import LoggerCore.Moira.AnalogDiscovery2;
import LoggerCore.Moira.VoltageMonitor;
import LoggerCore.Moira.Analyzer.FreqAnalysisChannel;

public class SpectrumAnalyzerLogger extends LoggerFrameMinimal {

    protected AnalogDiscovery2 _AD2;
    protected VoltageMonitor _vm;
    protected FreqAnalysisChannel _freqAnalyzerCh1;
    protected FreqAnalysisChannel _freqAnalyzerCh2;
    protected JCheckBoxMenuItem _runningCheckBox;

    public SpectrumAnalyzerLogger(AnalogDiscovery2 AD2, VoltageMonitor vm) {
        super(false, false, true);
        setTitle("Spectrum Analyzer");

        _AD2 = AD2;
        _vm = vm;
        _freqAnalyzerCh1 = new FreqAnalysisChannel(AD2, 0);
        _freqAnalyzerCh2 = new FreqAnalysisChannel(AD2, 1);

        addXYSeries(_freqAnalyzerCh1.getSeries(), "[Hz]", "Amplitude");
        addXYSeries(_freqAnalyzerCh2.getSeries(), "[Hz]", "Amplitude");

        DisplayXYSeries(_freqAnalyzerCh1.getSeries());
        DisplayXYSeries(_freqAnalyzerCh2.getSeries());

        _runningCheckBox = BuildRunAnalyzerCheckBoxItem();
        getJMenuBar().add(_runningCheckBox);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                _freqAnalyzerCh1.removeFromAllCollections();
                _freqAnalyzerCh2.removeFromAllCollections();
                setVisible(false);
            }
        });
    }

    public JMenuItem BuildShowFrameMenuItem() {
        final LoggerFrame frame = this;
        return new JMenuItem(new AbstractAction("Spectrum Analyzer") {
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
                    if (!_vm.getLinkedAnalysisCollection().contains(_freqAnalyzerCh1))
                        _vm.getLinkedAnalysisCollection().add(_freqAnalyzerCh1);

                    if (!_vm.getLinkedAnalysisCollection().contains(_freqAnalyzerCh2))
                        _vm.getLinkedAnalysisCollection().add(_freqAnalyzerCh2);
                } else {
                    _freqAnalyzerCh1.removeFromAllCollections();
                    _freqAnalyzerCh2.removeFromAllCollections();
                }
            }
        });
        return runningCheckBox;
    }
}
