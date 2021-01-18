package LoggerCore.Moira;

import LoggerCore.Communication.Command;

public class Moira extends AnalogDiscovery2 {

    private int _switchDriver;

    private muxMAX338 _MuxPGA;
    private double[] _CurrentGains;
    private double _shuntResistor;

    private MoiraState _state;

    public enum MoiraState {
        freeRun(0), triggered(1);

        private int _id;

        private MoiraState(int id) {
            _id = id;
        }

        public int getID() {
            return _id;
        }
    }

    public MoiraState getMoiraState() {
        return _state;
    }

    public void setMoiraState(MoiraState State) {
        _state = State;
    }

    public Moira() {
        super();

        OverrideCommand("Open", new Command("Open") {
            @Override
            protected Object execute(Object arg) {
                boolean success = _dwf.FDwfDeviceOpen();
                digitalWrie(_switchDriver, 1);
                return success;
            }
        });

        addCommand(new Command("Set_current_gain") {
            @Override
            protected Object execute(Object arg) {
                try {
                    int idx = Integer.valueOf(String.class.cast(arg));
                    return _MuxPGA.selectLine(idx);
                } catch (Exception e) {
                    return false;
                }
            }
        });

        addCommand(new Command("Set_switch") {
            @Override
            protected Object execute(Object arg) {
                boolean value = Boolean.parseBoolean(arg.toString());
                if (value)
                    return digitalWrie(_switchDriver, 1);
                else
                    return digitalWrie(_switchDriver, 0);
            }
        });

        _switchDriver = 0;
        _MuxPGA = new muxMAX338(1, 2, 3, -1);
        _CurrentGains = new double[] { 1, 2, 4, 8, 16, 32, 64, 128 };
        _shuntResistor = 1;
        _state = MoiraState.freeRun;
    }

    public boolean SetSwitch(boolean value) {
        return (boolean) executeCommand("Set_switch", Boolean.toString(value));
    }

    public double convertToCurrent(double voltage) {
        return voltage / (_shuntResistor * _CurrentGains[_MuxPGA.get_selectedLine()]);
    }

    private class muxMAX338 {

        private int _pinA0;
        private int _pinA1;
        private int _pinA2;
        private int _Enable;
        private int _selectedLine;

        public muxMAX338(int pinA0, int pinA1, int pinA2, int Enable) {
            _pinA0 = pinA0;
            _pinA1 = pinA1;
            _pinA2 = pinA2;
            _Enable = Enable;

            selectLine(0);
        }

        public boolean disable() {
            if (_Enable > 0)
                return digitalWrie(_Enable, 0);
            return false;
        }

        public boolean enable() {
            if (_Enable > 0)
                return digitalWrie(_Enable, 1);
            return false;
        }

        public int get_selectedLine() {
            return _selectedLine;
        }

        public boolean selectLine(int line) {
            _selectedLine = line;
            boolean res = true;
            switch (line) {
                case 0:
                    if (_Enable > 0)
                        res = res && digitalWrie(_Enable, 1);
                    res = res && digitalWrie(_pinA0, 0);
                    res = res && digitalWrie(_pinA1, 0);
                    res = res && digitalWrie(_pinA2, 0);
                    break;

                case 1:
                    if (_Enable > 0)
                        res = res && digitalWrie(_Enable, 1);
                    res = res && digitalWrie(_pinA0, 1);
                    res = res && digitalWrie(_pinA1, 0);
                    res = res && digitalWrie(_pinA2, 0);
                    break;

                case 2:
                    if (_Enable > 0)
                        res = res && digitalWrie(_Enable, 1);
                    res = res && digitalWrie(_pinA0, 0);
                    res = res && digitalWrie(_pinA1, 1);
                    res = res && digitalWrie(_pinA2, 0);
                    break;

                case 3:
                    if (_Enable > 0)
                        res = res && digitalWrie(_Enable, 1);
                    res = res && digitalWrie(_pinA0, 1);
                    res = res && digitalWrie(_pinA1, 1);
                    res = res && digitalWrie(_pinA2, 0);
                    break;

                case 4:
                    if (_Enable > 0)
                        res = res && digitalWrie(_Enable, 1);
                    res = res && digitalWrie(_pinA0, 0);
                    res = res && digitalWrie(_pinA1, 0);
                    res = res && digitalWrie(_pinA2, 1);
                    break;

                case 5:
                    if (_Enable > 0)
                        res = res && digitalWrie(_Enable, 1);
                    res = res && digitalWrie(_pinA0, 1);
                    res = res && digitalWrie(_pinA1, 0);
                    res = res && digitalWrie(_pinA2, 1);
                    break;

                case 6:
                    if (_Enable > 0)
                        res = res && digitalWrie(_Enable, 1);
                    res = res && digitalWrie(_pinA0, 0);
                    res = res && digitalWrie(_pinA1, 1);
                    res = res && digitalWrie(_pinA2, 1);
                    break;

                case 7:
                    if (_Enable > 0)
                        res = res && digitalWrie(_Enable, 1);
                    res = res && digitalWrie(_pinA0, 1);
                    res = res && digitalWrie(_pinA1, 1);
                    res = res && digitalWrie(_pinA2, 1);
                    break;

                default:
                    break;
            }

            return res;
        }
    }
}
