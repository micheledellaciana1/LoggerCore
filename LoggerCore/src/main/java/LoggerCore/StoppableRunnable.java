package LoggerCore;

public abstract class StoppableRunnable implements Runnable {
    public boolean verbose = true;

    private volatile boolean _BREAK = false;
    private volatile boolean _isRunning = false;
    protected String _name;

    protected int _ExecutionDelay = 0;
    protected int _ExecutionDelay2 = 0;

    public StoppableRunnable() {
        super();
    }

    public void setExecutionDelay(int executionDelay) {
        _ExecutionDelay = executionDelay;
        _ExecutionDelay2 = executionDelay;
    }

    public void setExecutionDelay1(int executionDelay) {
        _ExecutionDelay = executionDelay;
    }

    public void setExecutionDelay2(int executionDelay) {
        _ExecutionDelay2 = executionDelay;
    }

    public void set_firstDelay(boolean _firstDelay) {
        this._firstDelay = _firstDelay;
    }

    public void set_secondDelay(boolean _secondDelay) {
        this._secondDelay = _secondDelay;
    }

    public int getExecutionDelay() {
        return _ExecutionDelay;
    }

    public int getExecutionDelay2() {
        return _ExecutionDelay2;
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        _name = name;
    }

    public boolean setup(Object arg) {
        return true;
    }

    public void OnClosing() {
    }

    protected boolean _firstDelay = true;
    protected boolean _secondDelay = false;

    @Override
    public void run() {
        set_isRunning(true);

        while (!getBREAK()) {
            StartLoop();

            if (_firstDelay)
                try {
                    Thread.sleep(_ExecutionDelay);
                } catch (InterruptedException e) {
                    if (verbose)
                        e.printStackTrace();
                }

            MiddleLoop();

            if (_secondDelay)
                try {
                    Thread.sleep(_ExecutionDelay2);
                } catch (InterruptedException e) {
                    if (verbose)
                        e.printStackTrace();
                }

            EndLoop();
        }

        set_isRunning(false);
        setBREAK(false);
        OnClosing();
    }

    public void StartLoop() {

    }

    public void EndLoop() {

    }

    public abstract void MiddleLoop();

    public synchronized boolean getBREAK() {
        return _BREAK;
    }

    public synchronized void setBREAK(boolean BREAK) {
        _BREAK = BREAK;
    }

    public synchronized void kill() {
        _BREAK = true;
    }

    public synchronized boolean getisRunning() {
        return _isRunning;
    }

    private synchronized void set_isRunning(boolean _isRunning) {
        this._isRunning = _isRunning;
    }
}
