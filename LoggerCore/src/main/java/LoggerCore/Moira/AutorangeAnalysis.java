package LoggerCore.Moira;

import java.util.ArrayList;
import LoggerCore.LinkedAnalysis;

public class AutorangeAnalysis extends LinkedAnalysis {

    Moira _moira;

    AutorangeAnalysis(Moira moira) {
        super("AutorangeAnalysis");
        _moira = moira;
    }

    @Override
    public Object ExecuteAnalysis(Object newData) {
        ArrayList<double[]> values = ArrayList.class.cast(newData);
        autorangeVoltage(values.get(0), 0);
        autorangeVoltage(values.get(1), 1);
        // autorangeCurrentDUT(values.get(1));
        return null;
    }

    private void autorangeCurrentDUT(double[] voltagesChannel1) {
        /*
         * double threshold = 2; double value =
         * Arrays.stream(voltagesChannel1).max().getAsDouble();
         * 
         * if (value >= 2.5) { _moira.setGain(_moira.getCurrentGainPGAIndex() - 1);
         * return; }
         * 
         * double nextGain = _moira.get_CorGains()[_moira.getCurrentGainPGAIndex() + 1];
         * if ((value / _moira.getCurrentGainPGA()) * nextGain < threshold) {
         * _moira.setGain(_moira.getCurrentGainPGAIndex() + 1); }
         */
    }

    private void autorangeVoltage(double[] voltagesChannel, int channel) {
        boolean setRange25 = false;
        boolean setRange2_5 = false;

        for (int i = 0; i < voltagesChannel.length; i++) {
            if (Math.abs(voltagesChannel[i]) >= 2.5) {
                setRange25 = true;
                setRange2_5 = false;
                break;
            } else {
                setRange2_5 = true;
            }
        }

        if (_moira.getVoltageRange(channel) > 2.5)
            setRange25 = false;

        if (_moira.getVoltageRange(channel) <= 2.5)
            setRange2_5 = false;

        if (setRange25)
            _moira.SetVoltageRange(channel, 25);
        if (setRange2_5)
            _moira.SetVoltageRange(channel, 2.5);
    }
}
