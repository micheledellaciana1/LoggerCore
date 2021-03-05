package LoggerCore.Icaro;

import java.util.ArrayList;

import java.awt.geom.*;

import com.fazecast.jSerialComm.SerialPort;

import LoggerCore.Communication.Command;
import LoggerCore.Communication.SerialDevice;
import LoggerCore.Communication.StringCommand;
import LoggerCore.themal.LookUpTable;

public class Icaro extends SerialDevice {

    protected double _VoltageHeater;
    protected int _ADCAvegMillis;

    protected LookUpTable _LUTHeater;

    public Icaro(String name, LookUpTable LUTHeater, SerialPort port) {
        super(name, port);
        _LUTHeater = LUTHeater;

        addCommand(new StringCommand("Set voltage heater") {
            @Override
            protected Object execute(String arg) {
                try {
                    _VoltageHeater = Double.valueOf(arg);
                } catch (Exception e) {
                    if (verbose)
                        e.printStackTrace();
                    return false;
                }

                _serialBuffer.println("Set_Voltage_Heater");
                _serialBuffer.println(arg);
                return true;
            }

            @Override
            protected Object executeSimulation(Object arg) {
                try {
                    _VoltageHeater = Double.valueOf(String.class.cast(arg));
                } catch (Exception e) {
                    if (verbose)
                        e.printStackTrace();
                    return false;
                }

                return true;
            }
        });

        addCommand(new Command("ReadPT100Resistance") {
            @Override
            protected Object execute(Object arg) {
                return executeCommand("askDouble", "Read_PT100_Resistance");
            }

            @Override
            protected Object executeSimulation(Object arg) {
                return 12 + _VoltageHeater + Math.random();
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

                _serialBuffer.println("Set_Average_Time");
                _serialBuffer.println(arg);
                return true;
            }
        });

        addCommand(new Command("ReadTempSensor") {
            @Override
            protected Object execute(Object arg) {
                return executeCommand("askDouble", "Read_Temp_Sensor");
            }

            @Override
            protected Object executeSimulation(Object arg) {
                return 25 + Math.random();
            }
        });

        addCommand(new Command("ReadRHSensor") {
            @Override
            protected Object execute(Object arg) {
                return executeCommand("askDouble", "Read_RH_Sensor");
            }

            @Override
            protected Object executeSimulation(Object arg) {
                return 40 + Math.random();
            }
        });

        addCommand(new Command("ReadTempBoard") {
            @Override
            protected Object execute(Object arg) {
                return executeCommand("askDouble", "Read_Temp_Board");
            }

            @Override
            protected Object executeSimulation(Object arg) {
                return 40 + Math.random();
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

    public double getResistanceHeater() {
        double resistance = Double.class.cast(executeCommand("ReadPT100Resistance", null));
        return resistance;
    }

    public double getTemperatureHeater() {
        try {
            return _LUTHeater.getX(getResistanceHeater());
        } catch (Exception e) {
            return getResistanceHeater();
        }
    }

    public double getVoltageHeater() {
        return _VoltageHeater;
    }

    public double getTemperatureBoard() {
        double temperature = Double.class.cast(executeCommand("ReadTempBoard", null));
        return temperature;
    }

    public void set_LUTHeater(LookUpTable _LUTHeater) {
        this._LUTHeater = _LUTHeater;
    }

    public LookUpTable get_LUTHeater() {
        return _LUTHeater;
    }
}
