package LoggerCore.Zefiro.IVCharacteristic;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import org.apache.commons.math3.analysis.ParametricUnivariateFunction;
import org.apache.commons.math3.fitting.SimpleCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoint;
import org.jfree.data.xy.XYSeries;

import LoggerCore.GlobalVar;
import LoggerCore.LinkedArrayAnalysis;
import LoggerCore.LinkedPointAnalysis;

public class PowerLowFitAnalysis extends LinkedArrayAnalysis {

    private LinkedPointAnalysis _kAnalysis;
    private LinkedPointAnalysis _alphaAnalysis;
    private ParametricUnivariateFunction _func;

    public PowerLowFitAnalysis() {
        super("Fitting error");
        _kAnalysis = new LinkedPointAnalysis("k") {
            @Override
            public Point2D ExecutePointAnalysis(Object newData) {
                return new Point2D.Double(_time, _k);
            }
        };
        getLinkedAnalysisCollection().add(_kAnalysis);

        _alphaAnalysis = new LinkedPointAnalysis("alpha") {
            @Override
            public Point2D ExecutePointAnalysis(Object newData) {
                return new Point2D.Double(_time, _alpha);
            }
        };
        getLinkedAnalysisCollection().add(_alphaAnalysis);

        _func = new ParametricUnivariateFunction() {

            @Override
            public double value(double x, double... parameters) {
                return parameters[0] * Math.pow(x, parameters[1]);
            }

            @Override
            public double[] gradient(double x, double... parameters) {
                double[] grad = new double[2];
                grad[0] = Math.pow(x, parameters[1]);
                grad[1] = parameters[0] * Math.pow(x, parameters[1]) * Math.log(x);
                return grad;
            }
        };
    }

    private double _k;
    private double _alpha;
    private double _time;
    private double[] _par = new double[] { 1, 1 };

    @Override
    public ArrayList<Point2D> ExecuteArrayAnalysis(Object newData) {
        XYSeries curve = XYSeries.class.cast(newData);

        ArrayList<WeightedObservedPoint> points = new ArrayList<WeightedObservedPoint>();
        for (int i = 0; i < curve.getItemCount(); i++) {
            double x = Math.abs(curve.getX(i).doubleValue());
            double y = Math.abs(curve.getY(i).doubleValue());
            points.add(new WeightedObservedPoint(1, Math.abs(x), Math.abs(y)));
        }

        _par = SimpleCurveFitter.create(_func, _par).fit(points);

        _k = _par[0];
        _alpha = _par[1];
        _time = (System.currentTimeMillis() - GlobalVar.start) * 1e-3;

        ArrayList<Point2D> _error = new ArrayList<Point2D>();
        for (int i = 0; i < curve.getItemCount(); i++) {
            double x = curve.getX(i).doubleValue();
            double y = curve.getY(i).doubleValue();
            double yEstimated = _func.value(Math.abs(x), _par) * Math.signum(x);
            _error.add(new Point2D.Double(x, y - yEstimated));
        }

        return _error;
    }

    public XYSeries getKSeries() {
        return _kAnalysis.getSeries();
    }

    public XYSeries getAlphaSeries() {
        return _alphaAnalysis.getSeries();
    }
}
