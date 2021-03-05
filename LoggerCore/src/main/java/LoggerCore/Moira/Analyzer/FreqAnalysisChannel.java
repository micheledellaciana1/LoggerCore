package LoggerCore.Moira.Analyzer;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import LoggerCore.LinkedArrayAnalysis;
import LoggerCore.Moira.AnalogDiscovery2;

public class FreqAnalysisChannel extends LinkedArrayAnalysis {

    private AnalogDiscovery2 _AD2;
    private int _channel;

    public FreqAnalysisChannel(AnalogDiscovery2 AD2, int channel) {
        super("Freq analysis Ch." + channel);
        _AD2 = AD2;
        _channel = channel;
    }

    @Override
    public ArrayList<Point2D> ExecuteArrayAnalysis(Object newData) {
        ArrayList<double[]> values = ArrayList.class.cast(newData);

        double[] mod = CurvesUtils.fft_mod(values.get(_channel), _AD2.get_dwf().FDwfAnalogInBufferSizeMax());
        ArrayList<Point2D> result = new ArrayList<Point2D>();

        for (int i = 0; i < mod.length / 2; i++) {
            if (Double.isFinite(mod[i]))
                result.add(new Point2D.Double(i / (_AD2.get_dwf().FDwfAnalogInBufferSizeMax() * _AD2.getTimeBin()),
                        mod[i]));
            else
                return null;
        }

        return result;
    }
}
