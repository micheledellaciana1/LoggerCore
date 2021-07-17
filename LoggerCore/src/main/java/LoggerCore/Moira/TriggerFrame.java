package LoggerCore.Moira;

import java.awt.event.*;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class TriggerFrame extends JFrame {

    private Moira _moira;

    private JTextField _channelTF;
    private JTextField _valueTF;
    private JTextField _positionTF;
    private JTextField _conditionTF;
    private JTextField _timeoutTF;
    private JCheckBox _runningTriggerCB;

    public TriggerFrame(String name, Moira moira) {
        super(name);

        _moira = moira;
        JPanel pane = new JPanel();
        pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
        pane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        JLabel channelText = new JLabel("Channel trigger [0/1]");
        _channelTF = new JTextField("0");
        _channelTF.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == 10)
                    setTrigger();
            }
        });

        pane.add(channelText);
        pane.add(_channelTF);

        JLabel valueText = new JLabel("Value trigger [V]");
        _valueTF = new JTextField("0");
        _valueTF.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == 10)
                    setTrigger();
            }
        });

        pane.add(valueText);
        pane.add(_valueTF);

        JLabel positionText = new JLabel("Position trigger [sec]");
        _positionTF = new JTextField("0");
        _positionTF.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == 10)
                    setTrigger();
            }
        });

        pane.add(positionText);
        pane.add(_positionTF);

        JLabel ConditionText = new JLabel("Condition trigger [0/Rising, 1/Falling]");
        _conditionTF = new JTextField("0");
        _conditionTF.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == 10)
                    setTrigger();
            }
        });

        pane.add(ConditionText);
        pane.add(_conditionTF);

        JLabel timeoutText = new JLabel("Timeout [sec]");
        _timeoutTF = new JTextField("0");
        _timeoutTF.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == 10)
                    setTrigger();
            }
        });

        pane.add(timeoutText);
        pane.add(_timeoutTF);

        _runningTriggerCB = new JCheckBox();
        _runningTriggerCB.setAction(new AbstractAction("Trigger") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (_runningTriggerCB.isSelected())
                    setTrigger();
                else
                    _moira.StopOscillosopeTrigger();
            }
        });

        pane.add(_runningTriggerCB);

        setContentPane(pane);
        pack();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setVisible(true);
    }

    private void setTrigger() {

        int channel = Integer.valueOf(_channelTF.getText());
        double value = Double.valueOf(_valueTF.getText());
        double position = Double.valueOf(_positionTF.getText());
        int condition = Integer.valueOf(_conditionTF.getText());
        double timeout = Double.valueOf(_timeoutTF.getText());

        _runningTriggerCB.setSelected(true);
        _moira.SetOscilloscopeTrigger(channel, value, position, condition, timeout);
    }

    static public JMenuItem BuildMenuItem(final String name, final Moira moira) {
        return new JMenuItem(new AbstractAction(name) {
            @Override
            public void actionPerformed(ActionEvent e) {
                new TriggerFrame(name, moira);
            }
        });
    }
}
