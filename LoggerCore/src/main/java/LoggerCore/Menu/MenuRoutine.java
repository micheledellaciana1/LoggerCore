package LoggerCore.Menu;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

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
                if (_runningRoutine != null && _runningRoutine.getisRunning()) {
                    _runningRoutine.kill();
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
                    public void actionPerformed() {
                        if (_runningRoutine != null && _runningRoutine.getisRunning()) {
                            _runningRoutine.kill();
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

    public JMenuItem BuildStopRoutineItem(String name) {
        return BuildNoArgMenuItem(new NoInputAction(name) {
            @Override
            public void actionPerformed() {
                if (_runningRoutine != null) {
                    _runningRoutine.kill();
                } else {
                    return;
                }

                _logString = "Stopped " + _runningRoutine.getName();
                _runningRoutine = null;
            }
        });

    }

    @Override
    public JMenu BuildDefault() {
        return null;
    }
}