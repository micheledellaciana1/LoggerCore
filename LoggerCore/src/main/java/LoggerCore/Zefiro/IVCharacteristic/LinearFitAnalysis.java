package LoggerCore.Zefiro.IVCharacteristic;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import org.apache.commons.math3.stat.regression.RegressionResults;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.jfree.data.xy.XYSeries;

import LoggerCore.GlobalVar;
import LoggerCore.LinkedArrayAnalysis;
import LoggerCore.LinkedPointAnalysis;

public class LinearFitAnalysis extends LinkedArrayAnalysis {

    private LinkedPointAnalysis _interceptAnalysis;
    private LinkedPointAnalysis _slopeAnalysis;

    public LinearFitAnalysis() {
        super("Fitting error");
        _interceptAnalysis = new LinkedPointAnalysis("Intercept") {
            @Override
            public Point2D ExecutePointAnalysis(Object newData) {
                return new Point2D.Double(_time, _intercept);
            }
        };
        getLinkedAnalysisCollection().add(_interceptAnalysis);

        _slopeAnalysis = new LinkedPointAnalysis("Slope") {
            @Override
            public Point2D ExecutePointAnalysis(Object newData) {
                return new Point2D.Double(_time, _slope);
            }
        };
        getLinkedAnalysisCollection().add(_slopeAnalysis);

    }

    private double _intercept;
    private double _slope;
    private double _time;

    @Override
    public ArrayList<Point2D> ExecuteArrayAnalysis(Object newData) {
        XYSeries curve = XYSeries.class.cast(newData);
        SimpleRegression sr = new SimpleRegression(true);

        for (int i = 0; i < curve.getItemCount(); i++)
            sr.addData(curve.getX(i).doubleValue(), curve.getY(i).doubleValue());

        RegressionResults rr = sr.regress();
        _intercept = rr.getParameterEstimate(0);
        _slope = rr.getParameterEstimate(1);

        _time = (System.currentTimeMillis() - GlobalVar.start) * 1e-3;

        ArrayList<Point2D> _error = new ArrayList<Point2D>();
        for (int i = 0; i < curve.getItemCount(); i++) {
            double yEstimated = curve.getX(i).doubleValue() * _slope + _intercept;
            _error.add(new Point2D.Double(curve.getX(i).doubleValue(), curve.getY(i).doubleValue() - yEstimated));
        }

        return _error;
    }

    public XYSeries getInterceptSeries() {
        return _interceptAnalysis.getSeries();
    }

    public XYSeries getSlopeSeries() {
        return _slopeAnalysis.getSeries();
    }
}
