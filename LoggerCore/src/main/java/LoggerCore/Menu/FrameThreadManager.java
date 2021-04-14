package LoggerCore.Menu;

import java.awt.event.*;
import java.awt.Dimension;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import LoggerCore.ThreadManager;

public class FrameThreadManager extends JFrame {

    protected ThreadManager _tm;

    public FrameThreadManager(String name, ThreadManager tm) {
        super(name);
        _tm = tm;

        JPanel pane = new JPanel();
        pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
        pane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        for (int i = 0; i < _tm.size(); i++) {
            final int idx = i;

            final JCheckBox jcb = new JCheckBox();
            jcb.setSelected(_tm.get(idx).getisRunning());
            jcb.setAction(new AbstractAction(_tm.get(i).getName()) {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (jcb.isSelected())
                        _tm.run(idx);
                    else
                        _tm.pause(idx);
                }
            });

            JLabel labelText = new JLabel("Delay in millis:");
            final JTextField jtf = new JTextField(Integer.toString(_tm.get(idx).getExecutionDelay()));
            jtf.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    if (e.getKeyCode() == 10) {
                        _tm.get(idx).setExecutionDelay(Integer.valueOf(jtf.getText()));
                    }
                }
            });

            jtf.addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                    _tm.get(idx).setExecutionDelay(Integer.valueOf(jtf.getText()));
                }

                @Override
                public void focusLost(FocusEvent e) {
                    _tm.get(idx).setExecutionDelay(Integer.valueOf(jtf.getText()));
                }
            });

            pane.add(jcb);
            pane.add(labelText);
            pane.add(jtf);

            if (i != _tm.size() - 1)
                pane.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        setContentPane(pane);
        pack();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setVisible(true);
    }
}
