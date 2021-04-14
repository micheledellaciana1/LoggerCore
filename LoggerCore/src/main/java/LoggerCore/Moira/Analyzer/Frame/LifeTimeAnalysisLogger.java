package LoggerCore.Moira.Analyzer.Frame;

import java.awt.event.*;

import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;

import LoggerCore.LoggerFrame;
import LoggerCore.LoggerFrameMinimal;
import LoggerCore.Moira.Moira;
import LoggerCore.Moira.VoltageMonitor;
import LoggerCore.Moira.Analyzer.LifeTimeAnalysis;

public class LifeTimeAnalysisLogger extends LoggerFrameMinimal {

    protected Moira _moira;
    protected VoltageMonitor _vm;
    protected LifeTimeAnalysis _lifeTimeAnalysis;
    protected JCheckBoxMenuItem _runningCheckBox;

    public LifeTimeAnalysisLogger(Moira moira, VoltageMonitor vm) {
        super(false, false, true, false);
        setTitle("Harmonic Analyzer");

        _moira = moira;
        _vm = vm;
        _lifeTimeAnalysis = new LifeTimeAnalysis("Life time", moira);

        _runningCheckBox = BuildRunAnalyzerCheckBoxItem();
        getJMenuBar().add(_runningCheckBox);

        addXYSeries(_lifeTimeAnalysis.getSeries(), "Time[S]", "Life time[S]");

        DisplayXYSeries(_lifeTimeAnalysis.getSeries());

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                _lifeTimeAnalysis.removeFromAllCollections();
                setVisible(false);
            }
        });
    }

    public JMenuItem BuildShowFrameMenuItem() {
        final LoggerFrame frame = this;
        return new JMenuItem(new AbstractAction("Life time Analyzer") {
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
                    if (!_vm.getLinkedAnalysisCollection().contains(_lifeTimeAnalysis))
                        _vm.getLinkedAnalysisCollection().add(_lifeTimeAnalysis);
                } else {
                    _lifeTimeAnalysis.removeFromAllCollections();
                }
            }
        });
        return runningCheckBox;
    }
}
