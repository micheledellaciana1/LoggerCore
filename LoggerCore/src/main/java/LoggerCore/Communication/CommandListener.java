package LoggerCore.Communication;

import java.util.ArrayList;

public abstract class CommandListener {

    protected ArrayList<CommandListenerCollection> _commandListenerCollections;

    public CommandListener() {
        _commandListenerCollections = new ArrayList<CommandListenerCollection>();
    }

    public abstract void CommandExecuted(Object arg, Object returned);

    public void removeFromAllCollections() {
        for (CommandListenerCollection commandListenerCollection : _commandListenerCollections)
            commandListenerCollection.remove(this);
    }
}
