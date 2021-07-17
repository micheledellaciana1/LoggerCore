package LoggerCore.Moira;

import java.awt.event.*;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class DCControlFrame extends JFrame {

    private Moira _moira;

    private JTextField _voltageDAC0;
    private JTextField _voltageDAC1;
    private JTextField _gainAMP;
    private JTextField _GroundResistor;
    private JCheckBox _connectedDUT;
    private JButton _updateButton;

    public DCControlFrame(String name, Moira moira) {
        super(name);

        _moira = moira;
        JPanel pane = new JPanel();
        pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
        pane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        JLabel voltageDAC0Text = new JLabel("Voltage DAC0");
        _voltageDAC0 = new JTextField("0");
        _voltageDAC0.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == 10)
                    _moira.setDCVoltage(0, Double.valueOf(_voltageDAC0.getText()));
            }
        });

        pane.add(voltageDAC0Text);
        pane.add(_voltageDAC0);

        JLabel voltageDAC1Text = new JLabel("Voltage DAC1");
        _voltageDAC1 = new JTextField("0");
        _voltageDAC1.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == 10)
                    _moira.setDCVoltage(1, Double.valueOf(_voltageDAC1.getText()));
            }
        });

        pane.add(voltageDAC1Text);
        pane.add(_voltageDAC1);

        JLabel _gainAMPText = new JLabel("Gain Current [0-7]");
        _gainAMP = new JTextField("0");
        _gainAMP.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == 10)
                    _moira.setGain(Integer.valueOf(_gainAMP.getText()));
            }
        });

        pane.add(_gainAMPText);
        pane.add(_gainAMP);

        JLabel _GroundResistorText = new JLabel("Ground Resistor [0-7]");
        _GroundResistor = new JTextField("0");
        _GroundResistor.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == 10)
                    _moira.setGroundResistor(Integer.valueOf(_GroundResistor.getText()));
            }
        });

        pane.add(_GroundResistorText);
        pane.add(_GroundResistor);

        _connectedDUT = new JCheckBox();
        _connectedDUT.setAction(new AbstractAction("ConnectDUT") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (_connectedDUT.isSelected())
                    _moira.SetSwitch(true);
                else
                    _moira.SetSwitch(false);
            }
        });

        pane.add(_connectedDUT);

        _updateButton = new JButton(new AbstractAction("Update") {
            @Override
            public void actionPerformed(ActionEvent e) {
                update();
            }
        });

        pane.add(_updateButton);

        setContentPane(pane);
        pack();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setVisible(true);
    }

    private void update() {
        _moira.setDCVoltage(0, Double.valueOf(_voltageDAC0.getText()));
        _moira.setDCVoltage(1, Double.valueOf(_voltageDAC1.getText()));
        _moira.setGain(Integer.valueOf(_gainAMP.getText()));
        _moira.setGroundResistor(Integer.valueOf(_GroundResistor.getText()));
        if (_connectedDUT.isSelected())
            _moira.SetSwitch(true);
        else
            _moira.SetSwitch(false);

    }

    static public JMenuItem BuildMenuItem(final String name, final Moira moira) {
        return new JMenuItem(new AbstractAction(name) {
            @Override
            public void actionPerformed(ActionEvent e) {
                new DCControlFrame(name, moira);
            }
        });
    }
}
