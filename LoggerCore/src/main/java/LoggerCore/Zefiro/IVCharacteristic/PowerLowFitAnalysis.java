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
    private SimpleCurveFitter _fitter;

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

        _fitter = SimpleCurveFitter.create(new ParametricUnivariateFunction() {

            @Override
            public double value(double x, double... parameters) {
                return parameters[0] * Math.pow(x, parameters[1]);
            }

            @Override
            public double[] gradient(double x, double... parameters) {
                double d0 = Math.pow(x, parameters[1]);
                double d1 = parameters[0] * parameters[1] * Math.pow(x, parameters[1] - 1);

                return new double[] { d0, d1 };
            }

        }, new double[] { 1, 1 });
    }

    private double _k;
    private double _alpha;
    private double _time;

    @Override
    public ArrayList<Point2D> ExecuteArrayAnalysis(Object newData) {
        XYSeries curve = XYSeries.class.cast(newData);

        ArrayList<WeightedObservedPoint> points = new ArrayList<WeightedObservedPoint>();
        for (int i = 0; i < curve.getItemCount(); i++) {
            double x = curve.getX(i).doubleValue();
            double y = curve.getY(i).doubleValue();
            points.add(new WeightedObservedPoint(1, Math.abs(x), Math.abs(y)));
        }

        double[] par = _fitter.fit(points);

        _k = par[0];
        _alpha = par[1];
        _time = (System.currentTimeMillis() - GlobalVar.start) * 1e-3;

        ArrayList<Point2D> _error = new ArrayList<Point2D>();
        for (int i = 0; i < curve.getItemCount(); i++) {
            double x = curve.getX(i).doubleValue();
            double y = curve.getY(i).doubleValue();
            double yEstimated = Math.pow(Math.abs(x), _alpha) * _k * Math.signum(x);
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
