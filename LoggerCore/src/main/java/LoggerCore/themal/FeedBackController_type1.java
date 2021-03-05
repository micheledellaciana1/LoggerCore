package LoggerCore.themal;

public class FeedBackController_type1 implements IFeedbackController {

    private double _integral;
    private double _differential;
    private double _sensitivity;

    private double _Error = 0.;

    private double _target_value = 0.;

    private Long _timeLastCall = System.nanoTime();
    public double MAX_responce = Double.POSITIVE_INFINITY;
    public double MIN_responce = Double.NEGATIVE_INFINITY;
    public double TOLLERATED_ERROR = 0;
    private boolean _feedbackON = true;

    public FeedBackController_type1(double integral, double differential, double sensitivity) {
        _integral = integral;
        _differential = differential;
        _sensitivity = sensitivity;
        reset();
    }

    private boolean _justChangedTarget = false;

    public void set_target_value(double target_value) {
        _justChangedTarget = true;
        _target_value = target_value;
    }

    public void setFeedbackON(boolean feedbackON) {
        _justChangedTarget = true;
        _feedbackON = feedbackON;
        reset();
    }

    public boolean getFeedbackON() {
        return _feedbackON;
    }

    public double responce(double measuredValue, double actual_responce) {
        if (!_feedbackON)
            return actual_responce;

        if (actual_responce < MIN_responce)
            return MIN_responce;

        Long currentTime = System.nanoTime();
        double deltaTime = (currentTime - _timeLastCall) * 1e-9;
        _timeLastCall = currentTime;

        double NewError = _target_value - measuredValue;
        double differentialResponce = EvalueteOnIperboloid(NewError - _Error, _differential, _sensitivity) / deltaTime;

        if (_justChangedTarget) {
            differentialResponce = 0;
            _justChangedTarget = false;
        }

        double responce = EvalueteOnIperboloid(NewError, _integral, _sensitivity) * deltaTime + differentialResponce;

        _Error = NewError;

        if (Math.abs(_Error) < TOLLERATED_ERROR)
            return actual_responce;

        if (actual_responce + responce < MIN_responce)
            return MIN_responce;

        if (actual_responce + responce > MAX_responce)
            return MAX_responce;

        return actual_responce + responce;
    }

    private double EvalueteOnIperboloid(double x, double m, double b) {
        return m * (Math.sqrt(b * b + x * x) - b) * Math.signum(x);
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
                _sensitivity = value;
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
                return _sensitivity;
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
        TOLLERATED_ERROR = tolleratedError;
    }
}