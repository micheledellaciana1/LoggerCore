package LoggerCore.Moira;

import java.util.ArrayList;

import org.knowm.waveforms4j.DWF;

import LoggerCore.Communication.Command;
import LoggerCore.Communication.Device;
import LoggerCore.Communication.StringCommand;

public class AnalogDiscovery2 extends Device {

    dwf_extended _dwf;

    public AnalogDiscovery2() {
        super("AnalogDiscovery2");
        _dwf = new dwf_extended();

        _commands.add(new Command("Open") {
            @Override
            protected Object execute(Object arg) {
                boolean success = _dwf.FDwfDeviceOpen();
                return success && _dwf.FDwfDeviceAutoConfigureSet(true);
            }
        });

        _commands.add(new Command("Close") {
            @Override
            protected Object execute(Object arg) {
                return _dwf.FDwfDeviceCloseAll();
            }
        });

        _commands.add(new StringCommand("Set_oscilloscope") {
            @Override
            protected Object execute(String arg) {
                String par[] = arg.split(" ");

                double TimeBase, RangeCh1, RangeCh2;
                try {
                    TimeBase = Double.valueOf(par[0]);
                    RangeCh1 = Double.valueOf(par[1]);
                    RangeCh2 = Double.valueOf(par[2]);
                } catch (Exception e) {
                    if (verbose)
                        e.printStackTrace();
                    return false;
                }

                int bufferSize = DWF.AD2_MAX_BUFFER_SIZE;
                double SampleFrequency = bufferSize / TimeBase;

                if (SampleFrequency > 1e8) {
                    SampleFrequency = 1e8;
                    bufferSize = (int) Math.round(TimeBase * SampleFrequency);
                }

                return _dwf.startAnalogCaptureBothChannels(SampleFrequency, bufferSize, RangeCh1, RangeCh2);
            }
        });

        _commands.add(new StringCommand("Set_time_base") {
            @Override
            protected Object execute(String arg) {
                try {
                    double timeBase = Double.valueOf(arg);
                    int bufferSize = DWF.AD2_MAX_BUFFER_SIZE;
                    double desiredFrequency = bufferSize / timeBase;
                    int scaleFactor = (int) (1e8 / desiredFrequency);

                    double SampleFrequency;

                    if (scaleFactor > 1)
                        SampleFrequency = 1e8 / scaleFactor;
                    else {
                        SampleFrequency = 1e8;
                    }

                    bufferSize = (int) (timeBase * SampleFrequency);

                    if (bufferSize > DWF.AD2_MAX_BUFFER_SIZE)
                        bufferSize = DWF.AD2_MAX_BUFFER_SIZE;

                    boolean success = true;
                    success = success && _dwf.FDwfAnalogInFrequencySet(SampleFrequency);
                    success = success && _dwf.FDwfAnalogInBufferSizeSet(bufferSize);
                    success = success && _dwf.FDwfAnalogInConfigure(true, true);
                    return success;
                } catch (Exception e) {
                    if (verbose)
                        e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected Object executeSimulation(Object arg) {
                double timeBase = Double.valueOf((String) arg);
                int bufferSize = 1024;
                double desiredFrequency = bufferSize / timeBase;
                int scaleFactor = (int) (1e8 / desiredFrequency);

                double SampleFrequency;
                SampleFrequency = 1e8 / scaleFactor;

                _dwf.FDwfAnalogInFrequencySet(SampleFrequency);
                return true;
            }
        });

        _commands.add(new StringCommand("Read_oscilloscope_single_channel") {
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

                return values;
            }
        });

        _commands.add(new Command("Read_oscilloscope_both_channel") {
            @Override
            protected Object execute(Object arg) {
                if (_dwf.FDwfAnalogInStatus(true) != 2)
                    return null;

                int sampleValid = _dwf.FDwfAnalogInStatusSamplesValid();
                double[] values0 = _dwf.FDwfAnalogInStatusData(0, sampleValid);
                double[] values1 = _dwf.FDwfAnalogInStatusData(1, sampleValid);

                ArrayList<double[]> values = new ArrayList<double[]>();
                values.add(values0);
                values.add(values1);

                return values;
            }

            @Override
            protected Object executeSimulation(Object arg) {
                double[] values0 = new double[1000];
                double[] values1 = new double[1000];

                for (int i = 0; i < values0.length; i++) {
                    values0[i] = Math.random();
                    values1[i] = Math.random();
                }

                ArrayList<double[]> values = new ArrayList<double[]>();
                values.add(values0);
                values.add(values1);

                return values;
            }
        });

        _commands.add(new StringCommand("Set_oscilloscope_trigger") {
            @Override
            protected Object execute(String arg) {
                String par[] = arg.split(" ");
                int TriggerChannel, TriggerCondition;
                double TriggerLevel, TriggerPosSec, TimeoutSec;
                try {
                    TriggerChannel = Integer.valueOf(par[0]);
                    TriggerLevel = Double.valueOf(par[1]);
                    TriggerPosSec = Double.valueOf(par[2]) + _dwf.getAnalogInBufferSize() * getTimeBin() * 0.5;
                    TriggerCondition = Integer.valueOf(par[3]);
                    TimeoutSec = Double.valueOf(par[4]);
                } catch (Exception e) {
                    if (verbose)
                        e.printStackTrace();
                    return false;
                }

                return _dwf.setTriggerAnalogIn(TriggerChannel, TriggerLevel, TriggerPosSec, TriggerCondition,
                        TimeoutSec);
            }

            @Override
            protected Object executeSimulation(Object arg) {
                return true;
            }
        });

        _commands.add(new Command("Stop_oscilloscope_trigger") {
            @Override
            protected Object execute(Object arg) {
                return _dwf.stopTriggerAnalogIn();
            }
        });

        _commands.add(new StringCommand("Set_voltage_range") {
            @Override
            protected Object execute(String arg) {
                String par[] = arg.split(" ");
                int idxChannel;
                double value;
                try {
                    idxChannel = Integer.valueOf(par[0]);
                    value = Double.valueOf(par[1]);
                } catch (Exception e) {
                    if (verbose)
                        e.printStackTrace();
                    return false;
                }

                boolean success = true;
                success = success && _dwf.FDwfAnalogInChannelRangeSet(idxChannel, value);
                success = success && _dwf.FDwfAnalogInConfigure(true, true);

                if (!success)
                    success = success && _dwf.FDwfAnalogInChannelEnableSet(idxChannel, true);
                return success;
            }
        });

        addCommand(new StringCommand("Set_DC_voltage") {
            @Override
            protected Object execute(String arg) {
                String par[] = arg.split(" ");
                int idxChannel;
                double value;
                try {
                    idxChannel = Integer.valueOf(par[0]);
                    value = Double.valueOf(par[1]);
                } catch (Exception e) {
                    if (verbose)
                        e.printStackTrace();
                    return false;
                }

                _dwf.FDwfAnalogOutConfigure(idxChannel, false);
                boolean success = _dwf.FDwfAnalogOutNodeOffsetSet(idxChannel, value);
                return success;
            }
        });

        addCommand(new StringCommand("Digital_write") {
            @Override
            protected Object execute(String arg) {
                String par[] = arg.split(" ");
                int idxChannel;
                int value;
                try {
                    idxChannel = Integer.valueOf(par[0]);
                    value = Integer.valueOf(par[1]);
                } catch (Exception e) {
                    if (verbose)
                        e.printStackTrace();
                    return false;
                }

                return _dwf.DigitalWrite(idxChannel, value);
            }
        });

        addCommand(new StringCommand("Set_power_supply") {
            @Override
            protected Object execute(String arg) {
                try {
                    String args[] = arg.split(" ");
                    int idxChannel = Integer.valueOf(args[0]);
                    double value = Double.valueOf(args[1]);

                    return _dwf.setPowerSupply(idxChannel, value);
                } catch (Exception e) {
                    return false;
                }
            }
        });

        addCommand(new StringCommand("Start_wave") {
            @Override
            protected Object execute(String arg) {
                try {
                    String args[] = arg.split(" ");
                    int idxChannel = Integer.valueOf(args[0]);
                    int waveform = Integer.valueOf(args[1]);
                    double frequency = Double.valueOf(args[2]);
                    double amplitude = Double.valueOf(args[3]);
                    double offset = Double.valueOf(args[4]);
                    double dutyCycle = Double.valueOf(args[5]);

                    return _dwf.startWave(idxChannel, waveform, frequency, amplitude, offset, dutyCycle);
                } catch (Exception e) {
                    return false;
                }
            }
        });

        addCommand(new StringCommand("Set_freq_wave") {
            @Override
            protected Object execute(String arg) {
                try {
                    String args[] = arg.split(" ");
                    int idxChannel = Integer.valueOf(args[0]);
                    double frequency = Double.valueOf(args[1]);

                    boolean success = true;
                    success = success && _dwf.FDwfAnalogOutNodeFrequencySet(idxChannel, frequency);
                    success = success && _dwf.FDwfAnalogOutConfigure(idxChannel, true);

                    return success;
                } catch (Exception e) {
                    return false;
                }
            }
        });

        addCommand(new Command("Stop_wave") {
            @Override
            protected Object execute(Object arg) {
                try {
                    String args = String.class.cast(arg);
                    int idxChannel = Integer.valueOf(args);

                    return _dwf.stopWave(idxChannel);
                } catch (Exception e) {
                    return false;
                }
            }
        });
    }

    public synchronized double getTimeBin() {
        return _dwf.getTimeBin();
    }

    public synchronized double getVoltageRange(int channel) {
        return _dwf.getVoltageRange(channel);
    }

    public double getOffsetVoltage(int channel) {
        return _dwf.getDCOffset(channel);
    }

    public boolean setOscilloscope(double timeBaseSeconds, double range1, double range2) {
        return (boolean) executeCommand("Set_oscilloscope",
                Double.valueOf(timeBaseSeconds) + " " + range1 + " " + range2);
    }

    public boolean SetTimeBase(double seconds) {
        return (boolean) executeCommand("Set_time_base", Double.toString(seconds));
    }

    public double[] ReadOscilloscopeSingleChannel(int channelIdx) {
        return (double[]) executeCommand("Read_oscilloscope_single_channel", Integer.toString(channelIdx));
    }

    public ArrayList<double[]> ReadOscilloscopeBothChannel() {
        return (ArrayList<double[]>) executeCommand("Read_oscilloscope_both_channel", null);
    }

    public boolean StartWave(int idxChannel, int waveform, double frequency, double amplitude, double offset,
            double dutyCycle) {
        return (boolean) executeCommand("Start_wave",
                idxChannel + " " + waveform + " " + frequency + " " + amplitude + " " + offset + " " + dutyCycle);
    }

    public boolean StopWave(int idxChannel) {
        return (boolean) executeCommand("Stop_wave", Integer.toString(idxChannel));
    }

    public boolean SetFreqWave(int idxChannel, double freq) {
        return (boolean) executeCommand("Set_freq_wave", idxChannel + " " + freq);
    }

    public boolean SetOscilloscopeTrigger(int channel, double TriggerLevel, double TriggerPosSec, int trigCondition,
            double timeoutSec) {
        return (boolean) executeCommand("Set_oscilloscope_trigger",
                channel + " " + TriggerLevel + " " + TriggerPosSec + " " + trigCondition + " " + timeoutSec);
    }

    public boolean SetVoltageRange(int channel, double value) {
        return (boolean) executeCommand("Set_voltage_range", channel + " " + value);
    }

    public boolean StopOscillosopeTrigger() {
        return (boolean) executeCommand("Stop_oscilloscope_trigger", null);
    }

    public boolean SetPowerSupply(int idxChannel, double value) {
        return (boolean) executeCommand("Set_power_supply", idxChannel + " " + value);
    }

    public boolean setDCVoltage(int channel, double value) {
        return (boolean) executeCommand("Set_DC_voltage", channel + " " + value);
    }

    public boolean digitalWrie(int channel, int value) {
        return (boolean) executeCommand("Digital_write", channel + " " + value);
    }

    public dwf_extended get_dwf() {
        return _dwf;
    }
}
