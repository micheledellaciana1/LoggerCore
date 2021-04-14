package LoggerCore.Zefiro.WatchDog;

import java.awt.geom.*;

import javax.swing.JOptionPane;

import LoggerCore.LinkedAnalysis;
import LoggerCore.LinkedPointAnalysis;
import LoggerCore.themal.IFeedbackController;

public class ChamberTempDog extends LinkedAnalysis {

    protected double _thresholdDanger;

    protected IFeedbackController _heaterFeedback;

    public ChamberTempDog(IFeedbackController heaterFeedback, double thresholdDanger) {
        super("Chamber Temperature Dog");
        _heaterFeedback = heaterFeedback;
        _thresholdDanger = thresholdDanger;
    }

    @Override
    public Object ExecuteAnalysis(Object newData) {
        Point2D Ctemp = Point2D.Double.class.cast(newData);

        if (Ctemp.getY() > _thresholdDanger) {
            _heaterFeedback.setFeedbackON(false);
            _heaterFeedback.set_target_value(0);

            JOptionPane.showMessageDialog(null, "Chamber safety temperature exceeded: " + _thresholdDanger + " °C",
                    "ChamberTempDog", JOptionPane.WARNING_MESSAGE);
            return true;
        }
        return false;
    }

    public double get_thresholdDanger() {
        return _thresholdDanger;
    }

    public void set_thresholdDanger(double _thresholdDanger) {
        this._thresholdDanger = _thresholdDanger;
    }
}
