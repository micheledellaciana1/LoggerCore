package LoggerCore.Moira.Analyzer;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;

import org.jfree.data.xy.XYSeries;

import LoggerCore.GlobalVar;
import LoggerCore.LinkedAnalysis;
import LoggerCore.LinkedPointAnalysis;
import LoggerCore.Moira.Moira;

public class HarmonicAnalysis extends LinkedAnalysis {

    protected Moira _moira;
    protected LinkedPointAnalysis _amplitudeImpedence;
    protected LinkedPointAnalysis _amplitudeVoltage;
    protected LinkedPointAnalysis _amplitudeCurrent;
    protected LinkedPointAnalysis _angularFreqVoltage;
    protected LinkedPointAnalysis _angularFreqCurrent;
    protected LinkedPointAnalysis _phaseShift;

    public HarmonicAnalysis(Moira moira) {
        super("Harmonic analysis");
        _moira = moira;

        _amplitudeVoltage = new LinkedPointAnalysis("Amplitude Voltage") {
            @Override
            public Point2D ExecutePointAnalysis(Object newData) {
                return new Point2D.Double((double) newData, parsVoltage[0]);
            }
        };
        getLinkedAnalysisCollection().add(_amplitudeVoltage);

        _amplitudeCurrent = new LinkedPointAnalysis("Amplitude Current") {
            @Override
            public Point2D ExecutePointAnalysis(Object newData) {
                return new Point2D.Double((double) newData, parsCurrent[0]);
            }
        };
        getLinkedAnalysisCollection().add(_amplitudeCurrent);

        _angularFreqVoltage = new LinkedPointAnalysis("Freq. Voltage") {
            @Override
            public Point2D ExecutePointAnalysis(Object newData) {
                return new Point2D.Double((double) newData, parsVoltage[1] / (2 * 3.1415926536));
            }
        };
        getLinkedAnalysisCollection().add(_angularFreqVoltage);

        _angularFreqCurrent = new LinkedPointAnalysis("Freq. Current") {
            @Override
            public Point2D ExecutePointAnalysis(Object newData) {
                return new Point2D.Double((double) newData, parsCurrent[1] / (2 * 3.1415926536));
            }
        };
        getLinkedAnalysisCollection().add(_angularFreqCurrent);

        _phaseShift = new LinkedPointAnalysis("Phase Shift") {
            @Override
            public Point2D ExecutePointAnalysis(Object newData) {

                double phaseShift = parsVoltage[2] - parsCurrent[2];

                if (phaseShift < -Math.PI)
                    phaseShift += 2 * Math.PI;

                if (phaseShift > Math.PI)
                    phaseShift -= 2 * Math.PI;

                return new Point2D.Double((double) newData, phaseShift);
            }
        };

        getLinkedAnalysisCollection().add(_phaseShift);

        _amplitudeImpedence = new LinkedPointAnalysis("Amplitude Impedence") {
            @Override
            public Point2D ExecutePointAnalysis(Object newData) {
                return new Point2D.Double((double) newData, parsVoltage[0] / parsCurrent[0]);
            }
        };
        getLinkedAnalysisCollection().add(_amplitudeImpedence);
    }

    private double[] parsVoltage;
    private double[] parsCurrent;

    @Override
    public Object ExecuteAnalysis(Object newData) {
        ArrayList<double[]> values = ArrayList.class.cast(newData);
        double avgCh1 = Arrays.stream(values.get(0)).average().getAsDouble();
        double avgCh2 = Arrays.stream(values.get(1)).average().getAsDouble();

        ArrayList<Point2D> Voltage = new ArrayList<Point2D>();
        ArrayList<Point2D> Current = new ArrayList<Point2D>();

        final double dtMillis = _moira.getTimeBin();

        for (int i = 0; i < values.get(1).length; i++) {
            double x = i * dtMillis;
            Voltage.add(new Point2D.Double(x, values.get(0)[i] - avgCh1));
            Current.add(new Point2D.Double(x, _moira.convertToCurrent(values.get(1)[i] - avgCh2)));
        }

        try {
            parsVoltage = CurvesUtils.FitHarmonic(Voltage, parsVoltage);
            parsCurrent = CurvesUtils.FitHarmonic(Current, parsCurrent);
        } catch (Exception e) {
        }

        return (System.currentTimeMillis() - GlobalVar.start) * 1e-3;
    }

    public XYSeries getVoltageAmplitude() {
        return _amplitudeVoltage.getSeries();
    }

    public XYSeries getCurrentAmplitude() {
        return _amplitudeCurrent.getSeries();
    }

    public XYSeries getVoltageFreq() {
        return _angularFreqVoltage.getSeries();
    }

    public XYSeries getCurrentFreq() {
        return _angularFreqCurrent.getSeries();
    }

    public XYSeries getPhaseShift() {
        return _phaseShift.getSeries();
    }

    public XYSeries getAmplitudeImpedence() {
        return _amplitudeImpedence.getSeries();
    }
}
