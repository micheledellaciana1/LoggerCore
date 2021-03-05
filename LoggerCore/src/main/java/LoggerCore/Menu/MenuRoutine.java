package LoggerCore.Menu;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.UIManager;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import LoggerCore.StoppableRunnable;

public class MenuRoutine extends BasicMenu {

    protected StoppableRunnable _runningRoutine;

    public MenuRoutine(String name) {
        super(name);
        _runningRoutine = null;
    }

    public JMenuItem BuildNoArgRoutineItem(String name, final StoppableRunnable runnable) {
        return BuildNoArgMenuItem(new NoInputAction(name, "Started macro " + runnable.getName()) {
            @Override
            public void actionPerformed() {
                if (_runningRoutine != null) {
                    while (_runningRoutine.getisRunning()) {
                        _runningRoutine.kill();
                    }

                    if (_logAction)
                        _logBook.log("Stopped " + _runningRoutine.getName() + " macro");
                }

                if (!runnable.setup(null))
                    return;

                _runningRoutine = runnable;

                Thread t = new Thread(_runningRoutine);
                t.start();
            }
        });
    }

    public JMenuItem BuildStringRoutineItem(String name, final StoppableRunnable runnable, final String message,
            final String initialValue) {
        return BuildArgStringMenuItem(
                new InputStringAction(name, message, name, initialValue, "Started macro " + runnable.getName()) {
                    @Override
                    public void actionPerformed(String input) {
                        if (_runningRoutine != null) {
                            while (_runningRoutine.getisRunning()) {
                                _runningRoutine.kill();
                            }

                            if (_logAction)
                                _logBook.log("Stopped " + _runningRoutine.getName() + " macro");
                        }

                        if (!runnable.setup(_input))
                            return;

                        _runningRoutine = runnable;

                        Thread t = new Thread(_runningRoutine);
                        t.start();
                    }
                });
    }

    public JMenuItem BuildStopRoutineItem() {
        final JMenuItem item = BuildNoArgMenuItem(new NoInputAction(null) {
            @Override
            public void actionPerformed() {
                if (_runningRoutine != null) {

                    if (!_runningRoutine.getisRunning())
                        return;

                    while (_runningRoutine.getisRunning()) {
                        _runningRoutine.kill();
                    }

                    _logString = "Stopped " + _runningRoutine.getName() + " macro";
                }

                _runningRoutine = null;
            }
        });

        addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                if (_runningRoutine != null && _runningRoutine.getisRunning()) {
                    item.setText("Stop <" + _runningRoutine.getName() + ">");
                    item.setIcon(UIManager.getIcon("OptionPane.warningIcon"));
                } else {
                    item.setText("No running routine");
                    item.setIcon(UIManager.getIcon("OptionPane.informationIcon"));
                }
            }

            @Override
            public void menuDeselected(MenuEvent e) {
            }

            @Override
            public void menuCanceled(MenuEvent e) {
            }
        });

        return item;
    }

    @Override
    public JMenu BuildDefault() {
        return null;
    }
}