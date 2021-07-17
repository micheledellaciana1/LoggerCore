package LoggerCore.Moira.Analyzer.Frame;

import java.awt.event.*;

import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JTextField;

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
    protected JTextField _Tstart;
    protected JTextField _TFinish;

    public LifeTimeAnalysisLogger(Moira moira, VoltageMonitor vm) {
        super(false, false, true, false);
        setTitle("Harmonic Analyzer");

        _moira = moira;
        _vm = vm;
        _lifeTimeAnalysis = new LifeTimeAnalysis("Life time", moira);

        _runningCheckBox = BuildRunAnalyzerCheckBoxItem();
        getJMenuBar().add(_runningCheckBox);

        _Tstart = BuildTextFieldLowerLimit();
        getJMenuBar().add(_Tstart);

        _TFinish = BuildTextFieldUpperLimit();
        getJMenuBar().add(_TFinish);

        addXYSeries(_lifeTimeAnalysis.getSeries(), "Time[S]", "Life time / Temperature [us]/[K]");

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

    private JTextField BuildTextFieldLowerLimit() {
        final JTextField Tstart = new JTextField("Lower Limit");
        Tstart.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == 10)
                    _lifeTimeAnalysis.set_TimeStart(Double.valueOf(Tstart.getText()));
            }
        });

        Tstart.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                _lifeTimeAnalysis.set_TimeStart(Double.valueOf(Tstart.getText()));
            }
        });

        return Tstart;
    }

    private JTextField BuildTextFieldUpperLimit() {
        final JTextField TFinish = new JTextField("Upper Limit");
        TFinish.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == 10)
                    _lifeTimeAnalysis.set_TimeFinish(Double.valueOf(TFinish.getText()));
            }
        });

        TFinish.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                _lifeTimeAnalysis.set_TimeFinish(Double.valueOf(TFinish.getText()));
            }
        });

        return TFinish;
    }
}
