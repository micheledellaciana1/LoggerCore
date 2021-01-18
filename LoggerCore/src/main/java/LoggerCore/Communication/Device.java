package LoggerCore.Communication;

import java.util.ArrayList;

public class Device {
    protected String _name;
    protected ArrayList<Command> _commands;

    public boolean isASimulation = false;
    public boolean verbose = true;

    public Device(String name) {
        _name = name;
        _commands = new ArrayList<Command>();
    }

    public String getName() {
        return _name;
    }

    public void addCommand(Command newCommand) {
        _commands.add(newCommand);
    }

    public void removeCommand(Command _command) {
        _commands.remove(_command);
    }

    public void removeCommand(int index) {
        _commands.remove(index);
    }

    public void removeCommand(String name) {
        for (int i = 0; i < _commands.size(); i++) {
            if (_commands.get(i).getName().equals(name)) {
                _commands.remove(i);
                return;
            }
        }
    }

    public void OverrideCommand(String nameOldCOmmand, Command newCommand) {
        for (int i = 0; i < _commands.size(); i++) {
            if (_commands.get(i).getName().equals(nameOldCOmmand)) {
                _commands.set(i, newCommand);
                return;
            }
        }
    }

    synchronized public Object executeCommand(int index, Object arg) {
        if (isASimulation)
            return _commands.get(index).executeSimulation_wrap(arg);
        return _commands.get(index).execute_wrap(arg);
    }

    synchronized public Object executeCommand(String name, Object arg) {

        for (int i = 0; i < _commands.size(); i++) {
            if (_commands.get(i).getName().equals(name))
                return executeCommand(i, arg);
        }

        System.out.println("Return null: No command called: " + name);
        return null;
    }

    public Command getCommand(int index) {
        return _commands.get(index);
    }

    public Command getCommand(String name) {
        for (int i = 0; i < _commands.size(); i++) {
            if (_commands.get(i).getName().equals(name))
                return _commands.get(i);
        }

        System.out.println("Return null: No command called: " + name);
        return null;
    }

    public ArrayList<Command> getCommands() {
        return _commands;
    }
}
