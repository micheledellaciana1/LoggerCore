package LoggerCore.Moira.rouintes;

import LoggerCore.LoggerFrame;
import LoggerCore.LoggerFrameMinimal;
import LoggerCore.StoppableRunnable;
import LoggerCore.Moira.Moira;
import LoggerCore.Moira.VoltageMonitor;
import LoggerCore.Moira.Analyzer.LifeTimeAnalysis;

public class MeasureLifeTime extends StoppableRunnable {

    protected Moira _moira;
    protected VoltageMonitor _VoltageMonitor;
    protected LifeTimeAnalysis _LifeTimeAnalysis;
    protected LoggerFrame _logger;

    public MeasureLifeTime(Moira moira, VoltageMonitor voltageMonitor) {
        super();
        _moira = moira;
        _VoltageMonitor = voltageMonitor;
        _LifeTimeAnalysis = new LifeTimeAnalysis("LifeTime", moira);

        _ExecutionDelay = 100;
        _ExecutionDelay2 = 100;

        _firstDelay = true;
        _secondDelay = true;

        LoggerFrameMinimal _loggerMinimal = new LoggerFrameMinimal(true, false, false, false);
        _loggerMinimal.get_menuFile().add(_loggerMinimal.get_menuFile().BuildEraseSeriesDataItem("Erase data", false));
        _logger = _loggerMinimal;

        _logger.addXYSeries(_LifeTimeAnalysis.getSeries());
        _logger.DisplayXYSeries(_LifeTimeAnalysis.getSeries());
    }

    @Override
    public boolean setup(Object arg) {
        // todo
        // setto il trigger, aggiungo l'analisi al voltage monitor, imposto i valori dei
        // delay, collego il dut

        _VoltageMonitor.getLinkedAnalysisCollection().add(_LifeTimeAnalysis);

        double trgLevel = _moira.convertToCurrent(0.05);
        _moira.SetOscilloscopeTrigger(0, trgLevel, 0, 0, 0);
        _logger.setVisible(true);

        return super.setup(arg);
    }

    @Override
    public void OnClosing() {
        _VoltageMonitor.getLinkedAnalysisCollection().remove(_LifeTimeAnalysis);
        // _LifeTimeAnalysis.removeFromAllCollections(); equals to prev. line
        _moira.StopOscillosopeTrigger();
        super.OnClosing();
    }

    @Override
    public void StartLoop() {
        _moira.SetSwitch(true);
    }

    @Override
    public void MiddleLoop() {
        _moira.SetSwitch(false);
    }
}
