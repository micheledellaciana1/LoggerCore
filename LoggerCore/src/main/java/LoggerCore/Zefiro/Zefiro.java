package LoggerCore.Zefiro;

import java.util.ArrayList;

import java.awt.geom.*;

import com.fazecast.jSerialComm.SerialPort;

import LoggerCore.Communication.Command;
import LoggerCore.Communication.SerialDevice;
import LoggerCore.Communication.StringCommand;
import LoggerCore.themal.LookUpTable;

public class Zefiro extends SerialDevice {

    protected double _voltageSensorDAC;
    protected double _CurrentHeater;
    protected double _CurrentOutpu2;
    protected int _ADCAvegMillis;
    protected int _AmpMeterIdx;

    protected LookUpTable _LUTHeater;

    public Zefiro(String name, LookUpTable LUTHeater, SerialPort port) {
        super(name, port);
        _LUTHeater = LUTHeater;

        addCommand(new Command("ReadVoltageSensor") {
            @Override
            protected Object execute(Object arg) {
                return executeCommand("askDouble", "ReadVoltageSensor");
            }

            @Override
            protected Object executeSimulation(Object arg) {
                return _voltageSensorDAC;
            }
        });

        addCommand(new Command("ReadCurrentSensor") {
            @Override
            protected Object execute(Object arg) {
                return executeCommand("askDouble", "ReadCurrentSensor");
            }

            @Override
            protected Object executeSimulation(Object arg) {
                return _voltageSensorDAC + Math.random();
            }
        });

        addCommand(new StringCommand("Set DAC voltage sensor") {
            @Override
            protected Object execute(String arg) {
                try {
                    _voltageSensorDAC = Double.valueOf(arg);
                } catch (Exception e) {
                    if (verbose)
                        e.printStackTrace();
                    return false;
                }

                _serialBuffer.println("SetDACvoltagesensor");
                _serialBuffer.println(arg);
                return true;
            }

            @Override
            protected Object executeSimulation(Object arg) {
                try {
                    _voltageSensorDAC = Double.valueOf(String.class.cast(arg));
                } catch (Exception e) {
                    if (verbose)
                        e.printStackTrace();
                    return false;
                }
                return true;
            }
        });

        addCommand(new StringCommand("Set current heater") {
            @Override
            protected Object execute(String arg) {
                try {
                    _CurrentHeater = Double.valueOf(arg);
                } catch (Exception e) {
                    if (verbose)
                        e.printStackTrace();
                    return false;
                }

                _serialBuffer.println("Setcurrentheater");
                _serialBuffer.println(arg);
                return true;
            }

            @Override
            protected Object executeSimulation(Object arg) {
                try {
                    _CurrentHeater = Double.valueOf(String.class.cast(arg));
                } catch (Exception e) {
                    if (verbose)
                        e.printStackTrace();
                    return false;
                }

                return true;
            }
        });

        addCommand(new Command("ReadHeaterResistance") {
            @Override
            protected Object execute(Object arg) {
                return executeCommand("askDouble", "ReadHeaterResistance");
            }

            @Override
            protected Object executeSimulation(Object arg) {
                return 12 + _CurrentHeater + Math.random();
            }
        });

        addCommand(new StringCommand("Set average time") {
            @Override
            protected Object execute(String arg) {
                try {
                    _ADCAvegMillis = Integer.valueOf(arg);
                } catch (Exception e) {
                    if (verbose)
                        e.printStackTrace();
                    return false;
                }

                _serialBuffer.println("SetAverageTime");
                _serialBuffer.println(arg);
                return true;
            }
        });

        addCommand(new StringCommand("Set_current_LED") {
            @Override
            protected Object execute(String arg) {
                try {
                    _CurrentOutpu2 = Double.valueOf(arg);
                } catch (Exception e) {
                    if (verbose)
                        e.printStackTrace();
                    return false;
                }

                _serialBuffer.println("SetcurrentLED");
                _serialBuffer.println(arg);

                return true;
            }
        });

        addCommand(new Command("ReadTempSensor") {
            @Override
            protected Object execute(Object arg) {
                return executeCommand("askDouble", "ReadTempSensor");
            }

            @Override
            protected Object executeSimulation(Object arg) {
                return 25 + Math.random();
            }
        });

        addCommand(new Command("ReadRHSensor") {
            @Override
            protected Object execute(Object arg) {
                return executeCommand("askDouble", "ReadRHSensor");
            }

            @Override
            protected Object executeSimulation(Object arg) {
                return 40 + Math.random();
            }
        });

        addCommand(new Command("ReadOxygenSensor") {
            @Override
            protected Object execute(Object arg) {
                return executeCommand("askDouble", "ReadOxygenSensor");
            }

            @Override
            protected Object executeSimulation(Object arg) {
                return 20 + Math.random();
            }
        });

        addCommand(new Command("DisableCurrentSensorAutorange") {
            @Override
            protected Object execute(Object arg) {
                _serialBuffer.println("DisableCurrentSensorAutorange");
                return null;
            }
        });

        addCommand(new Command("EnableCurrentSensorAutorange") {
            @Override
            protected Object execute(Object arg) {
                _serialBuffer.println("EnableCurrentSensorAutorange");
                return null;
            }
        });

        addCommand(new StringCommand("Set AmpMeter Idx") {
            @Override
            protected Object execute(String arg) {
                try {
                    _AmpMeterIdx = Integer.valueOf(arg);
                } catch (Exception e) {
                    if (verbose)
                        e.printStackTrace();
                    return false;
                }

                if (_AmpMeterIdx >= 0 && _AmpMeterIdx < 8) {
                    _serialBuffer.println("DisableCurrentSensorAutorange");
                    _serialBuffer.println("SetAmpMeterIdx");
                    _serialBuffer.println(arg);
                } else {
                    _serialBuffer.println("EnableCurrentSensorAutorange");
                }

                return true;
            }
        });

        addCommand(new Command("ReadHeaterResistance_withExitation") {
            @Override
            protected Object execute(Object arg) {
                double extitationCurrent = Double.class.cast(arg);
                _serialBuffer.println("ReadHeaterResistance_withExitation");
                _serialBuffer.println(Double.toString(extitationCurrent));
                return Double.valueOf(_serialBuffer.WaitToReadLine());
            }

            @Override
            protected Object executeSimulation(Object arg) {
                return 13 + Math.random();
            }
        });

        addCommand(new StringCommand("ReadFastValues") {
            @Override
            protected Object execute(String arg) {
                String args[] = String.class.cast(arg).split(" ");

                _serialBuffer.println("ReadFastValues");
                _serialBuffer.println(args[0]);
                _serialBuffer.println(args[1]);

                int NValues = Integer.valueOf(args[1]);
                double dt = Float.valueOf(_serialBuffer.WaitToReadLine()) / NValues;
                ArrayList<Point2D> values = new ArrayList<Point2D>();

                for (int i = 0; i < NValues; i++)
                    values.add(new Point2D.Double(i * dt, Float.valueOf(_serialBuffer.WaitToReadLine())));

                return values;
            }
        });
    }

    public double readVoltageFallSensor() {
        return Double.class.cast(executeCommand("ReadVoltageSensor", null));

    }

    public double readCurrentSensor() {
        return Double.class.cast(executeCommand("ReadCurrentSensor", null));

    }

    public void setVoltageFallSensor(double voltage) {
        executeCommand("Set DAC voltage sensor", Double.toString(voltage));
    }

    public void setCurrentHeater(double current) {
        executeCommand("Setcurrentheater", Double.toString(current));
    }

    public double getResistanceHeater() {
        double resistance = Double.class.cast(executeCommand("ReadHeaterResistance", null));
        return resistance;
    }

    public double getTemperatureHeater() {
        try {
            return _LUTHeater.getX(getResistanceHeater());
        } catch (Exception e) {
            return getResistanceHeater();
        }
    }

    public double getVoltageSensorDAC() {
        return _voltageSensorDAC;
    }

    public double getCurrentHeater() {
        return _CurrentHeater;
    }

    public void set_LUTHeater(LookUpTable _LUTHeater) {
        this._LUTHeater = _LUTHeater;
    }

    public LookUpTable get_LUTHeater() {
        return _LUTHeater;
    }
}
