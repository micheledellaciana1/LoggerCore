package LoggerCore.themal;

import java.util.LinkedList;

import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoint;

public class FeedBackController_type2 implements IFeedbackController {

    private double _proportional;
    private double _integral;
    private double _differential;

    private LinkedList<WeightedObservedPoint> _lastMeasures;

    private double _target_value = 0.;

    private Long _timeLastCall = System.nanoTime();
    public double MAX_responce = Double.POSITIVE_INFINITY;
    public double MIN_responce = Double.NEGATIVE_INFINITY;
    private boolean _feedbackON = true;

    public FeedBackController_type2(double proportional, double integral, double differential) {
        _proportional = proportional;
        _integral = integral;
        _differential = differential;

        _lastMeasures = new LinkedList<WeightedObservedPoint>();
        reset();
    }

    public void set_target_value(double target_value) {
        _target_value = target_value;
    }

    public void setFeedbackON(boolean feedbackON) {
        _feedbackON = feedbackON;
        reset();
    }

    public boolean getFeedbackON() {
        return _feedbackON;
    }

    private double derivate() {
        try {
            return PolynomialCurveFitter.create(1).fit(_lastMeasures)[1];

        } catch (Exception e) {
            return 0;
        }
    }

    private double _integralError = 0;

    public double responce(double measuredValue, double actual_responce) {
        if (!_feedbackON)
            return actual_responce;

        Long currentTime = System.nanoTime();
        double deltaTime = (currentTime - _timeLastCall) * 1e-9;
        _timeLastCall = currentTime;

        double error = _target_value - measuredValue;
        _integralError += error * deltaTime;

        double responce = error * _proportional + _integralError * _integral + derivate() * _differential;

        if (responce < MIN_responce)
            responce = MIN_responce;

        if (responce > MAX_responce)
            responce = MAX_responce;

        if (_lastMeasures.size() < 20)
            _lastMeasures.add(new WeightedObservedPoint(1, currentTime, measuredValue));
        else {
            _lastMeasures.addLast(new WeightedObservedPoint(1, currentTime, measuredValue));
            _lastMeasures.removeFirst();
        }

        return responce;
    }

    public void setParameters(int index, double value) {
        switch (index) {
            case 0:
                _integral = value;
                break;
            case 1:
                _differential = value;
                break;
            case 2:
                _differential = value;
                break;
            default:
        }
    }

    public double getParameters(int index) {

        switch (index) {
            case 0:
                return _integral;
            case 1:
                return _differential;
            case 2:
                return _differential;
            default:
                return 0.;
        }
    }

    public void reset() {
        _timeLastCall = System.nanoTime();
    }

    public double getTarget() {
        return _target_value;
    }

    public void setTolleratedError(double tolleratedError) {
        // todo
    }
}
