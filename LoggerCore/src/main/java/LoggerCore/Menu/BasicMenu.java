package LoggerCore.Menu;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

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
                actionPerformed();

                if (_logAction)
                    _logBook.log(_logString + ": " + _input);
            } catch (Exception _e) {
                if (verbose)
                    _e.printStackTrace();
            }
        }

        public abstract void actionPerformed();
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
}
