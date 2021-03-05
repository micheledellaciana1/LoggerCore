package LoggerCore.Moira;

import java.awt.EventQueue;

import java.util.ArrayList;

import org.jfree.data.xy.XYSeries;

import LoggerCore.ObjectStream;

public class VoltageMonitor extends ObjectStream {

    AnalogDiscovery2 _AD2;
    XYSeries _VoltageCh1;
    XYSeries _VoltageCh2;
    private boolean _flagIsRendering = false;

    public VoltageMonitor(Comparable<?> keyVoltageCh1, Comparable<?> keyVoltageCh2, AnalogDiscovery2 AD2) {
        super();
        _AD2 = AD2;
        _VoltageCh1 = new XYSeries(keyVoltageCh1);
        _VoltageCh2 = new XYSeries(keyVoltageCh2);
    }

    private synchronized void setflagIsRendering(boolean newValue) {
        _flagIsRendering = newValue;
    }

    private synchronized boolean isRendering() {
        return _flagIsRendering;
    }

    @Override
    public ArrayList<double[]> acquireObject() {

        Object result = null;
        final double dtMillis = _AD2.getTimeBin() * 1000;

        do {
            result = _AD2.ReadOscilloscopeBothChannel();
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                if (verbose)
                    e.printStackTrace();
            }
        } while (result == (null));

        final ArrayList<double[]> values = (ArrayList<double[]>) result;

        if (isRendering())
            return values;
        else {
            setflagIsRendering(true);

            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    _VoltageCh1.setNotify(false);
                    _VoltageCh2.setNotify(false);

                    _VoltageCh1.clear();
                    _VoltageCh2.clear();

                    for (int i = 0; i < values.get(1).length; i++) {
                        _VoltageCh1.add(i * dtMillis, values.get(0)[i]);
                        _VoltageCh2.add(i * dtMillis, values.get(1)[i]);
                    }

                    _VoltageCh1.setNotify(true);
                    _VoltageCh2.setNotify(true);

                    setflagIsRendering(false);
                }
            });
        }

        return values;
    }

    public XYSeries getVoltageCh1() {
        return _VoltageCh1;
    }

    public XYSeries getVoltageCh2() {
        return _VoltageCh2;
    }
}
