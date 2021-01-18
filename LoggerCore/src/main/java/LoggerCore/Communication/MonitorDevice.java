package LoggerCore.Communication;

import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class MonitorDevice extends JFrame {

    private Device _dev;
    private ArrayList<JLabel> labels;

    public MonitorDevice(Device dev, ArrayList<String> commandsName, ArrayList<String> correspondingLabel) {
        _dev = dev;
        labels = new ArrayList<JLabel>();

        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        for (int i = 0; i < commandsName.size(); i++) {
            final JLabel label = new JLabel();
            label.setName(correspondingLabel.get(i));
            label.setText(correspondingLabel.get(i) + ": <Not called>");

            _dev.getCommand(commandsName.get(i)).addCommandListeners(new CommandListener() {
                @Override
                public void CommandExecuted(Object arg, Object returned) {
                    label.setText(label.getName() + ": " + arg + " " + returned);
                }
            });

            getContentPane().add(label);
            labels.add(label);
        }

        pack();
    }

    public MonitorDevice(Device dev, boolean ShowWhenCalled) {
        _dev = dev;
        labels = new ArrayList<JLabel>();

        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        for (int i = 0; i < _dev.getCommands().size(); i++) {
            final JLabel label = new JLabel();
            label.setName(_dev.getCommands().get(i).getName());

            if (!ShowWhenCalled)
                label.setText(_dev.getCommands().get(i).getName() + ": <Not called>");

            _dev.getCommands().get(i).addCommandListeners(new CommandListener() {
                @Override
                public void CommandExecuted(Object arg, Object returned) {
                    label.setText(label.getName() + ": " + arg + " " + returned);
                }
            });

            getContentPane().add(label);
            labels.add(label);
        }

        pack();
    }
}
