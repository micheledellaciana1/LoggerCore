package LoggerCore.Menu;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JCheckBox;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import LoggerCore.themal.IFeedbackController;

public class MenuFeedBackController extends BasicMenu {

    protected IFeedbackController _feedbackController;

    public MenuFeedBackController(String name, IFeedbackController feedBackController) {
        super(name);
        setFeedbackController(feedBackController);
    }

    public void setFeedbackController(IFeedbackController feedbackController) {
        _feedbackController = feedbackController;
    }

    public IFeedbackController getFeedbackController() {
        return _feedbackController;
    }

    public JMenuItem BuildSetParametersItem(String name, final String message, final String initialValue) {
        return BuildArgStringMenuItem(new InputStringAction(name, message, name, initialValue) {
            @Override
            public void actionPerformed(String input) {
                try {
                    String par[] = input.split(" ");
                    _feedbackController.setParameters(0, Double.valueOf(par[0]));
                    _feedbackController.setParameters(1, Double.valueOf(par[1]));
                    _feedbackController.setParameters(2, Double.valueOf(par[2]));
                } catch (Exception e) {
                    if (verbose)
                        e.printStackTrace();
                }
            }
        });
    }

    public JMenuItem BuildSetTargetValueItem(String name, double min, double max, final double initialValue) {
        return BuildSliderMenuItem(name, min, max, initialValue, new DoubleJSliderChangeListener() {

            @Override
            public void valueChanged(double valueSlider) {
                try {
                    _feedbackController.set_target_value(valueSlider);
                } catch (Exception e) {
                    if (verbose)
                        e.printStackTrace();
                }
            }
        });
    }

    public JCheckBox BuildFeedBackOnCheckBox(String name) {
        final JCheckBox _feedbackONCB = new JCheckBox(name);
        _feedbackONCB.setSelected(_feedbackController.getFeedbackON());
        _feedbackONCB.setAction(new AbstractAction(name) {
            @Override
            public void actionPerformed(ActionEvent e) {
                _feedbackController.setFeedbackON(_feedbackONCB.isSelected());
            }
        });

        return _feedbackONCB;
    }

    @Override
    public JMenu BuildDefault() {
        removeAll();
        add(BuildFeedBackOnCheckBox("Feedback ON"));
        add(BuildSetParametersItem("Parameters", "Enter: <Par0> <Pars1> <Pars2>", _feedbackController.getParameters(0)
                + " " + _feedbackController.getParameters(1) + " " + _feedbackController.getParameters(2)));
        add(BuildSetTargetValueItem("Target value", 0., 1500., _feedbackController.getTarget()));
        return this;
    }
}
