package LoggerCore.Menu;

import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import LoggerCore.Communication.Device;
import LoggerCore.Communication.MonitorDevice;

public class MenuDevice extends BasicMenu {

    Device _dev;

    public MenuDevice(String name, Device dev) {
        super(name);
        _dev = dev;
    }

    public JMenuItem BuildStringCommandItem(final int index, final String message, final String initialValue) {
        return BuildStringCommandItem(_dev.getCommand(index).getName(), index, message, initialValue);
    }

    public JMenuItem BuildStringCommandItem(String label, final int index, final String message,
            final String initialValue) {
        String CommandName = _dev.getCommand(index).getName();
        return BuildArgStringMenuItem(new InputStringAction(label, message, CommandName, initialValue) {
            @Override
            public void actionPerformed(String input) {
                _dev.executeCommand(index, input);
            }
        });
    }

    public JMenuItem BuildStringCommandItem(String CommandName, final String message, final String initialValue) {
        for (int i = 0; i < _dev.getCommands().size(); i++) {
            if (_dev.getCommand(i).getName().equals(CommandName))
                return BuildStringCommandItem(i, message, initialValue);
        }

        System.out.println("Return null: No command called: " + CommandName);

        return null;
    }

    public JMenuItem BuildStringCommandItem(String label, String CommandName, final String message,
            final String initialValue) {
        for (int i = 0; i < _dev.getCommands().size(); i++) {
            if (_dev.getCommand(i).getName().equals(CommandName))
                return BuildStringCommandItem(label, i, message, initialValue);
        }

        System.out.println("Return null: No command called: " + CommandName);

        return null;
    }

    public JMenuItem BuildSliderCommandItem(String label, final String CommandName, double min, double max,
            final double initialValue) {
        return BuildSliderMenuItem(label, min, max, initialValue, new DoubleJSliderChangeListener() {
            @Override
            public void valueChanged(double valueSlider) {
                _dev.executeCommand(CommandName, Double.toString(valueSlider));
            }
        });
    }

    public JMenuItem BuildNSliderCommandItem(String name, ArrayList<String> labels, ArrayList<String> CommandNames,
            ArrayList<Double> mins, ArrayList<Double> maxs, ArrayList<Double> initialValues) {

        ArrayList<DoubleJSliderChangeListener> listeners = new ArrayList<DoubleJSliderChangeListener>();
        for (int i = 0; i < CommandNames.size(); i++) {
            final String command = CommandNames.get(i);
            listeners.add(new DoubleJSliderChangeListener() {
                @Override
                public void valueChanged(double valueSlider) {
                    _dev.executeCommand(command, Double.toString(valueSlider));
                }
            });
        }

        return BuildNSliderMenuItem(name, labels, mins, maxs, initialValues, listeners);
    }

    public JMenuItem BuildNoArgCommandItem(final int index) {
        return BuildNoArgCommandItem(_dev.getCommand(index).getName(), index);
    }

    public JMenuItem BuildNoArgCommandItem(final String label, final int index) {
        return BuildNoArgMenuItem(new NoInputAction(label) {
            @Override
            public void actionPerformed() {
                _dev.executeCommand(index, null);
            }
        });
    }

    public JMenuItem BuildNoArgCommandItem(final String label, String CommandName) {
        for (int i = 0; i < _dev.getCommands().size(); i++) {
            if (_dev.getCommand(i).getName().equals(CommandName))
                return BuildNoArgCommandItem(label, i);
        }
        return null;
    }

    public JMenuItem BuildNoArgCommandItem(String CommandName) {
        for (int i = 0; i < _dev.getCommands().size(); i++) {
            if (_dev.getCommand(i).getName().equals(CommandName))
                return BuildNoArgCommandItem(i);
        }
        return null;
    }

    private MonitorDevice monitor = null;

    public JMenuItem BuildDeviceMonitorFrame(String name, String title) {
        if (monitor == null) {
            monitor = new MonitorDevice(_dev, true);
            monitor.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            monitor.setVisible(false);
            monitor.setTitle(title);
        }

        return BuildNoArgMenuItem(new NoInputAction(name) {
            @Override
            public void actionPerformed() {
                monitor.setVisible(true);
                monitor.pack();
                if (monitor.getWidth() > 300)
                    monitor.setSize(300, monitor.getHeight());
            }
        });
    }

    @Override
    public JMenu BuildDefault() {
        return null;
    }
}
