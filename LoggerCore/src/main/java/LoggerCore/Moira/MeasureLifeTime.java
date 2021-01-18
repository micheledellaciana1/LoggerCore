package LoggerCore.Moira;

import LoggerCore.StoppableRunnable;

public class MeasureLifeTime extends StoppableRunnable {

    Moira _moira;

    MeasureLifeTime(Moira _m) {
        super();
        _moira = _m;

        _ExecutionDelay = 100;
        _ExecutionDelay2 = 1000;
    }

    @Override
    public boolean setup(Object arg) {
        // todo
        // setto il trigger, aggiungo l'analisi al voltage monitor, imposto i valori dei
        // delay, collego il dut
        double trgLevel = _moira.convertToCurrent(0.05);
        _moira.SetOscilloscopeTrigger(0, trgLevel, 0, 1, 0);
        super.setup(arg);

        return true;
    }

    @Override
    public void OnClosing() {
        _moira.StopOscillosopeTrigger();
        super.OnClosing();
    }

    @Override
    public void StartLoop() {
        _moira.SetSwitch(true);
        super.StartLoop();
    }

    @Override
    public void MiddleLoop() {
        _moira.SetSwitch(false);
    }
}
