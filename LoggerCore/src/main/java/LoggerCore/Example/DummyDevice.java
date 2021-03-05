package LoggerCore.Example;

import LoggerCore.Communication.Command;
import LoggerCore.Communication.Device;

public class DummyDevice extends Device {

    public DummyDevice(String name) {
        super(name);
        addCommand(new Command("SayNothing") {
            @Override
            public Object execute(Object arg) {
                System.out.println("Nothing!");
                return true;
            }
        });

        addCommand(new Command("Parrot") {
            @Override
            public Object execute(Object arg) {
                System.out.println(arg);
                return true;
            }
        });
    }

}
