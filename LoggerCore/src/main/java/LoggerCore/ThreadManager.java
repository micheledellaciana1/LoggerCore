package LoggerCore;

import java.util.ArrayList;

public class ThreadManager extends ArrayList<StoppableRunnable> {

    public ThreadManager() {
        super();
    }

    public void pause(int idx) {
        get(idx).kill();
    }

    public void pauseAll() {
        for (int i = 0; i < size(); i++)
            pause(i);
    }

    public void run(int idx) {
        get(idx).setBREAK(false);
        if (!get(idx).getisRunning()) {
            Thread t = new Thread(get(idx));
            t.start();
        }
    }

    public void runAll() {
        for (int i = 0; i < size(); i++)
            run(i);
    }
}
