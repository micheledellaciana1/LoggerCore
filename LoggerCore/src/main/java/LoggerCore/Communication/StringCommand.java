package LoggerCore.Communication;

public abstract class StringCommand extends Command {
    public StringCommand(String name) {
        super(name);
    }

    @Override
    protected Object execute(Object arg) {
        return execute(String.class.cast(arg));
    }

    @Override
    protected Object executeSimulation(Object arg) {
        return super.executeSimulation(arg);
    }

    protected abstract Object execute(String arg);
}
