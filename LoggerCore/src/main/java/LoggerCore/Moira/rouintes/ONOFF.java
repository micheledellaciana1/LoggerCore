package LoggerCore.Moira.rouintes;

import LoggerCore.StoppableRunnable;
import LoggerCore.Moira.Moira;

public class ONOFF extends StoppableRunnable {

    protected Moira _moira;

    public ONOFF(Moira moira) {
        super();
        _moira = moira;

        _ExecutionDelay = 25;
        _ExecutionDelay2 = 100;

        _firstDelay = true;
        _secondDelay = true;
    }

    @Override
    public boolean setup(Object arg) {
        return super.setup(arg);
    }

    @Override
    public void OnClosing() {
        super.OnClosing();
    }

    @Override
    public void StartLoop() {
        _moira.SetSwitch(true);
        // _moira.setDCVoltage(0, 0);
    }

    @Override
    public void MiddleLoop() {
        _moira.SetSwitch(false);
        // _moira.setDCVoltage(0, 2);
    }
}
