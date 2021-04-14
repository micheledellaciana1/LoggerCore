package LoggerCore.Moira;

import java.util.ArrayList;
import java.util.Arrays;

import LoggerCore.Communication.Command;
import LoggerCore.Communication.StringCommand;

public class Moira extends AnalogDiscovery2 {

    private int _switchGPIO;

    private muxMAX338 _MuxGR;
    private muxMAX338 _MuxPGA;
    private double[] _CorGains;
    private double _shuntResistor;

    private boolean _offsetCorrection;
    private double _offsetOpAmpIC2_IC3 = 0;
    private double _offsetOpAmpIC4 = 0;

    private MoiraState _state;

    public enum MoiraState {
        DirectCoupled(0), LEDCoupled(1);

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

    public Moira(boolean OffsetCorrection) {
        super();
        _offsetCorrection = OffsetCorrection;
        OverrideCommand("Open", new Command("Open") {
            @Override
            protected Object execute(Object arg) {
                boolean success = _dwf.FDwfDeviceOpen();
                setOscilloscope(1e-3, 2.5, 2.5);

                if (_offsetCorrection)
                    InitOffsets();

                success = success && _MuxPGA.selectLine(0);
                success = success && _MuxGR.selectLine(0);
                success = success && _MuxGR.disable();
                success = success && SetSwitch(true);
                return success;
            }
        });

        OverrideCommand("Read_oscilloscope_both_channel", new Command("Read_oscilloscope_both_channel") {
            @Override
            protected Object execute(Object arg) {
                if (_dwf.FDwfAnalogInStatus(true) != 2)
                    return null;

                int sampleValid = _dwf.FDwfAnalogInStatusSamplesValid();
                double[] values0 = _dwf.FDwfAnalogInStatusData(0, sampleValid);
                double[] values1 = _dwf.FDwfAnalogInStatusData(1, sampleValid);

                if (_offsetCorrection)
                    for (int i = 0; i < sampleValid; i++) {
                        values0[i] -= _offsetOpAmpIC2_IC3;
                        values1[i] -= _offsetOpAmpIC4;
                    }

                ArrayList<double[]> values = new ArrayList<double[]>();
                values.add(values0);
                values.add(values1);

                return values;
            }
        });
        OverrideCommand("Read_oscilloscope_single_channel", new StringCommand("Read_oscilloscope_single_channel") {
            @Override
            protected Object execute(String arg) {
                int idxCh;
                try {
                    idxCh = Integer.valueOf(arg);
                } catch (Exception e) {
                    if (verbose)
                        e.printStackTrace();
                    return null;
                }

                if (_dwf.FDwfAnalogInStatus(true) != 2)
                    return null;

                int sampleValid = _dwf.FDwfAnalogInStatusSamplesValid();
                double[] values = _dwf.FDwfAnalogInStatusData(idxCh, sampleValid);

                if (_offsetCorrection) {
                    double offset = 0;

                    switch (idxCh) {
                        case 0:
                            offset = _offsetOpAmpIC2_IC3;
                            break;
                        case 1:
                            offset = _offsetOpAmpIC2_IC3;
                            break;
                        default:
                            break;
                    }

                    for (int i = 0; i < sampleValid; i++)
                        values[i] -= offset;
                }

                return values;
            }
        });
        addCommand(new Command("Init_offsets") {
            @Override
            protected Object execute(Object arg) {
                _offsetOpAmpIC2_IC3 = 0;
                _offsetOpAmpIC4 = 0;

                boolean success = SetSwitch(false);
                success = success && _MuxGR.enable();
                success = success && _MuxGR.selectLine(0);
                success = success && _MuxPGA.selectLine(7);

                try {
                    Thread.sleep(1000);
                    ArrayList<double[]> values = ReadOscilloscopeBothChannel();

                    double avgVoltage1 = Arrays.stream(values.get(0)).average().getAsDouble();
                    double avgVoltage2 = Arrays.stream(values.get(1)).average().getAsDouble();

                    _offsetOpAmpIC2_IC3 = avgVoltage1;
                    _offsetOpAmpIC4 = avgVoltage2 / getCurrentGainPGA();

                    System.out.println("OffsetOpAmpIC2_IC3: " + _offsetOpAmpIC2_IC3);
                    System.out.println("OffsetOpAmpIC4: " + _offsetOpAmpIC2_IC3);

                    return success;
                } catch (InterruptedException e) {
                    return false;
                }
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
                    return digitalWrie(_switchGPIO, 1);
                else
                    return digitalWrie(_switchGPIO, 0);
            }
        });

        _switchGPIO = 7;
        _MuxPGA = new muxMAX338(1, 8, 0, -1);
        _MuxGR = new muxMAX338(2, 11, 10, 9);
        _CorGains = new double[] { 1, 2, 4, 8, 16, 32, 64, 128 };
        _shuntResistor = 1;

        _state = MoiraState.DirectCoupled;
    }

    public boolean SetSwitch(boolean value) {
        return (boolean) executeCommand("Set_switch", Boolean.toString(value));
    }

    public boolean InitOffsets() {
        return (boolean) executeCommand("Init_offsets", null);
    }

    public double convertToCurrent(double voltage) {
        return voltage / (_shuntResistor * _CorGains[_MuxPGA.get_selectedLine()]);
    }

    public double[] get_CorGains() {
        return _CorGains;
    }

    public double getCurrentGainPGA() {
        return _CorGains[_MuxPGA.get_selectedLine()];
    }

    public int getCurrentGainPGAIndex() {
        return _MuxPGA.get_selectedLine();
    }

    public double setGain(int index) {
        _MuxPGA.selectLine(index);
        return getCurrentGainPGA();
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
