package LoggerCore.Menu;

import java.awt.event.*;
import java.util.ArrayList;
import java.awt.*;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.event.MouseInputAdapter;

import LoggerCore.LoggerBook;

public abstract class BasicMenu extends JMenu {

    static protected LoggerBook _logBook;
    static {
        _logBook = new LoggerBook("Log book");
    }

    protected boolean _logAction = true;
    public boolean verbose = true;

    public BasicMenu(String name) {
        super(name);
    }

    public void setLogAction(Boolean logAction) {
        _logAction = logAction;
    }

    public LoggerBook getLoggerBook() {
        return _logBook;
    }

    public JMenuItem BuildHideDisplayMenuLogger(String name) {
        JMenuItem menuItem = new JMenuItem(new NoInputAction(name) {
            @Override
            public void actionPerformed() {
                _logBook.setVisible(!_logBook.isVisible());
            }
        });
        return menuItem;
    }

    public abstract JMenu BuildDefault();

    public MenuListener BuildDefaultMenuListener() {
        MenuListener menuListener = new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                BuildDefault();
            }

            @Override
            public void menuDeselected(MenuEvent e) {
            }

            @Override
            public void menuCanceled(MenuEvent e) {
            }
        };
        return menuListener;
    }

    public JMenuItem BuildArgStringMenuItem(InputStringAction action) {
        JMenuItem menuItem = new JMenuItem(action);
        return menuItem;
    }

    public JMenuItem BuildSliderMenuItem(final String name, double min, double max, double initialValue,
            DoubleJSliderChangeListener listener) {

        final JFrame frame = new JFrame(name);
        JPanel pane = new JPanel();

        pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
        pane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        final DoubleJSlider slider = new DoubleJSlider(min, max, 10000000, initialValue);
        slider.setName(name);
        slider.setMajorTickSpacing(slider.getDivision() / 5);
        slider.setMinorTickSpacing(slider.getDivision() / 50);
        slider.setPaintTicks(true);
        slider.setPaintTrack(true);
        slider.setPaintLabels(false);
        slider.setPreferredSize(new Dimension(200, 30));

        final JTextField textField = new JTextField(Double.toString(initialValue));
        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                slider.setValue(Double.parseDouble(textField.getText()));
            }
        });

        slider.addChangeListener(new DoubleJSliderChangeListener() {
            @Override
            public void valueChanged(double valueSlider) {
                textField.setText(Double.toString(valueSlider));
            }
        });

        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == 10) {
                    JTextField textField = JTextField.class.cast(e.getSource());
                    if (_logAction)
                        _logBook.log(slider.getName() + ": " + textField.getText());
                }
            }
        });
        slider.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                DoubleJSlider slider = DoubleJSlider.class.cast(e.getSource());
                if (_logAction)
                    _logBook.log(slider.getName() + ": " + slider.getDoubleValue());
            }
        });

        slider.addChangeListener(listener);

        pane.add(new JLabel(name + ": " + slider.get_min() + " - " + slider.get_max()));
        pane.add(Box.createRigidArea(new Dimension(0, 5)));
        pane.add(textField);
        pane.add(Box.createRigidArea(new Dimension(0, 10)));
        pane.add(slider);
        frame.setContentPane(pane);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        JMenuItem menuItem = new JMenuItem(new AbstractAction(name) {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (!frame.isVisible())
                    frame.setVisible(true);
            }
        });

        return menuItem;
    }

    public JMenuItem BuildNSliderMenuItem(final String name, ArrayList<String> names, ArrayList<Double> mins,
            ArrayList<Double> maxs, ArrayList<Double> initialValues, ArrayList<DoubleJSliderChangeListener> listeners) {

        final JFrame frame = new JFrame(name);

        JPanel pane = new JPanel();
        pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
        pane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        for (int i = 0; i < names.size(); i++) {
            final DoubleJSlider slider = new DoubleJSlider(mins.get(i), maxs.get(i), 10000000, initialValues.get(i));

            slider.setName(names.get(i));
            slider.setMajorTickSpacing(slider.getDivision() / 5);
            slider.setMinorTickSpacing(slider.getDivision() / 50);
            slider.setPaintTicks(true);
            slider.setPaintTrack(true);
            slider.setPaintLabels(false);
            slider.setPreferredSize(new Dimension(200, 30));

            final JTextField textField = new JTextField(Double.toString(initialValues.get(i)));
            textField.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    slider.setValue(Double.parseDouble(textField.getText()));
                }
            });

            slider.addChangeListener(new DoubleJSliderChangeListener() {
                @Override
                public void valueChanged(double valueSlider) {
                    textField.setText(Double.toString(valueSlider));
                }
            });

            textField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    if (e.getKeyCode() == 10) {
                        JTextField textField = JTextField.class.cast(e.getSource());
                        if (_logAction)
                            _logBook.log(slider.getName() + ": " + textField.getText());
                    }
                }
            });
            slider.addMouseListener(new MouseInputAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    DoubleJSlider slider = DoubleJSlider.class.cast(e.getSource());
                    if (_logAction)
                        _logBook.log(slider.getName() + ": " + slider.getDoubleValue());
                }
            });

            slider.addChangeListener(listeners.get(i));

            pane.add(new JLabel(names.get(i) + ": " + slider.get_min() + " - " + slider.get_max()));
            pane.add(Box.createRigidArea(new Dimension(0, 5)));
            pane.add(textField);
            pane.add(Box.createRigidArea(new Dimension(0, 10)));
            pane.add(slider);
        }

        frame.setContentPane(pane);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        JMenuItem menuItem = new JMenuItem(new AbstractAction(name) {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (!frame.isVisible())
                    frame.setVisible(true);
            }
        });

        return menuItem;
    }

    public JMenuItem BuildNoArgMenuItem(NoInputAction action) {
        JMenuItem menuItem = new JMenuItem(action);
        return menuItem;
    }

    static public JMenuItem BuildHelpMenu(String name, final String HelpMessage) {
        JMenuItem menuItem = new JMenuItem(new AbstractAction(name) {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, HelpMessage, "Help", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        menuItem.setIcon(UIManager.getIcon("OptionPane.questionIcon"));
        return menuItem;
    }

    public abstract class InputStringAction extends AbstractAction {
        protected String _input;
        protected String _message;
        protected String _title;
        protected String _logString;

        public InputStringAction(String label, String message, String title, String initialInput) {
            super(label);
            _input = initialInput;
            _title = title;
            _message = message;
            _logString = label;
        }

        public InputStringAction(String label, String message, String title, String initialInput, Icon icon) {
            super(label, icon);
            _input = initialInput;
            _title = title;
            _message = message;
            _logString = label;
        }

        public InputStringAction(String label, String message, String title, String initialInput, String logString) {
            super(label);
            _input = initialInput;
            _title = title;
            _message = message;
            _logString = logString;
        }

        public InputStringAction(String label, String message, String title, String initialInput, String logString,
                Icon icon) {
            super(label, icon);
            _input = initialInput;
            _title = title;
            _message = message;
            _logString = logString;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                _input = JOptionPane
                        .showInputDialog(null, _message, _title, JOptionPane.QUESTION_MESSAGE, null, null, _input)
                        .toString();
                actionPerformed(_input);

                if (_logAction)
                    _logBook.log(_logString + ": " + _input);
            } catch (Exception _e) {
                if (verbose)
                    _e.printStackTrace();
            }
        }

        public abstract void actionPerformed(String input);
    }

    public abstract class NoInputAction extends AbstractAction {

        protected String _logString;

        public NoInputAction(String label, String logString) {
            super(label);
            _logString = logString;
        }

        public NoInputAction(String label, String logString, Icon icon) {
            super(label, icon);
            _logString = logString;
        }

        public NoInputAction(String label) {
            super(label);
            _logString = label;
        }

        public NoInputAction(String label, Icon icon) {
            super(label, icon);
            _logString = label;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                actionPerformed();

                if (_logAction)
                    _logBook.log(_logString);
            } catch (Exception _e) {
                if (verbose)
                    _e.printStackTrace();
            }
        }

        public abstract void actionPerformed();
    }

    public abstract class DoubleJSliderChangeListener implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent e) {
            DoubleJSlider slider = DoubleJSlider.class.cast(e.getSource());
            valueChanged(slider.getDoubleValue());
        }

        abstract public void valueChanged(double valueSlider);
    }

    public class DoubleJSlider extends JSlider {

        protected double _min;
        protected double _max;

        protected int _division;

        public DoubleJSlider(double min, double max, int division, double cur) {
            super(0, division);

            _division = division;
            _min = min;
            _max = max;

            setValue(cur);
        }

        public void setValue(double value) {
            if (value > _max)
                value = _max;
            if (value < _min)
                value = _min;

            int intvalue = (int) ((value - _min) / (_max - _min) * _division);
            super.setValue(intvalue);
        }

        public double getDoubleValue() {
            return (super.getValue() / (double) (_division)) * (_max - _min) + _min;
        }

        public int getDivision() {
            return _division;
        }

        public double get_max() {
            return _max;
        }

        public double get_min() {
            return _min;
        }
    }

}
