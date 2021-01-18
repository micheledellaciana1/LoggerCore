package LoggerCore.Communication;

import java.util.ArrayList;

public class CommandListenerCollection extends ArrayList<CommandListener> {

    public CommandListenerCollection() {
        super();
    }

    public void fireAnalyzesExecution(Object arg, Object returned) {
        for (int i = 0; i < size(); i++)
            get(i).CommandExecuted(arg, returned);
    }

    @Override
    public boolean add(CommandListener e) {
        e._commandListenerCollections.add(this);
        return super.add(e);
    }
}