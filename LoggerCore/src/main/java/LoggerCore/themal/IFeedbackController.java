package LoggerCore.themal;

public interface IFeedbackController {
    public double responce(double measuredValue, double actual_responce);

    public void set_target_value(double target_value);

    public void setParameters(int index, double value);

    public double getParameters(int index);

    public double getTarget();

    public void reset();

    public void setFeedbackON(boolean feedbackON);

    public boolean getFeedbackON();

    public void setTolleratedError(double tolleratedError);
}
