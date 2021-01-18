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
        autorangeVoltageDUT(values.get(0));
        autorangeCurrentDUT(values.get(1));
        return null;
    }

    private void autorangeCurrentDUT(double[] voltagesChannel1) {
    }

    private void autorangeVoltageDUT(double[] voltagesChannel0) {

        boolean setRange25 = false;
        boolean setRange2_5 = false;

        for (int i = 0; i < voltagesChannel0.length; i++) {
            if (Math.abs(voltagesChannel0[i]) >= 2.5) {
                setRange25 = true;
                setRange2_5 = false;
                break;
            } else {
                setRange2_5 = true;
            }
        }

        if (_moira.getVoltageRange(0) > 2.5)
            setRange25 = false;

        if (_moira.getVoltageRange(0) <= 2.5)
            setRange2_5 = false;

        if (setRange25)
            _moira.SetVoltageRange(0, 25);
        if (setRange2_5)
            _moira.SetVoltageRange(0, 2.5);
    }
}
