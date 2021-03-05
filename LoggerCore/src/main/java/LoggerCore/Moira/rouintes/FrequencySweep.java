
package LoggerCore.Moira.rouintes;

import LoggerCore.Sweep;
import LoggerCore.Moira.Moira;

public class FrequencySweep extends Sweep {

    protected Moira _moira;
    protected int _idxWF;

    public FrequencySweep(Moira moira) {
        super();

        _moira = moira;
        _useLogPath = true;
    }

    @Override
    public boolean setup(Object arg) {

        switch (_moira.getMoiraState()) {
            case DirectCoupled:
                _idxWF = 0;
                break;
            case LEDCoupled:
                _idxWF = 1;
                break;
            default:
                break;
        }

        return this.setup(arg);
    }

    @Override
    public void sweepAction(double value) {
        _moira.SetFreqWave(_idxWF, _Path.get(_indexPath++));
    }
}