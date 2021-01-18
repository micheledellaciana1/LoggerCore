package LoggerCore.Communication;

public abstract class Command {
    protected String _name;
    protected CommandListenerCollection _listeners;

    public boolean verbose = true;

    public Command(String name) {
        _name = name;
        _listeners = new CommandListenerCollection();
    }

    public void addCommandListeners(CommandListener listener) {
        _listeners.add(listener);
    }

    public void removeCommandListeners(CommandListener listener) {
        _listeners.remove(listener);
    }

    public String getName() {
        return _name;
    }

    protected Object execute_wrap(Object arg) {
        Object returned = execute(arg);
        for (CommandListener commandListener : _listeners)
            commandListener.CommandExecuted(arg, returned);
        return returned;
    }

    protected Object executeSimulation_wrap(Object arg) {
        Object returned = executeSimulation(arg);
        for (CommandListener commandListener : _listeners)
            commandListener.CommandExecuted(arg, returned);
        return returned;
    }

    protected abstract Object execute(Object arg);

    protected Object executeSimulation(Object arg) {
        return null;
    }
}
