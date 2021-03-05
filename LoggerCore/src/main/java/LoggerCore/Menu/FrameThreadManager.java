package LoggerCore.Menu;

import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

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
            jcb.setSelected(_tm.get(i).getisRunning());

            jcb.setAction(new AbstractAction(_tm.get(i).getName()) {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (jcb.isSelected())
                        _tm.run(idx);
                    else
                        _tm.pause(idx);
                }
            });

            pane.add(jcb);
            if (i != _tm.size() - 1)
                pane.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        setContentPane(pane);
        pack();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setVisible(true);
    }
}
