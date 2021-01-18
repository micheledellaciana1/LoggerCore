package LoggerCore.Communication;

import java.util.ArrayList;

import com.fazecast.jSerialComm.SerialPort;

public class SerialDevice extends Device {
    protected SerialPort _port;
    protected SerialBuffer _serialBuffer;

    public SerialDevice(String name, SerialPort port) {
        super(name);

        _port = port;
        if (port != null)
            _serialBuffer = new SerialBuffer(port);

        _commands.add(new Command("Open") {
            @Override
            public Object execute(Object arg) {
                if (!_port.isOpen())
                    _port.openPort();
                _serialBuffer.EstablishConnection(SerialDevice.this._name);
                return null;
            }
        });

        _commands.add(new Command("Close") {
            @Override
            public Object execute(Object arg) {
                _serialBuffer.println("Close");
                if (_port.isOpen())
                    _port.closePort();
                return null;
            }
        });

        _commands.add(new Command("Reset") {
            @Override
            public Object execute(Object arg) {
                _serialBuffer.println("Reset");
                return null;
            }
        });

        _commands.add(new Command("askDouble") {
            @Override
            public Object execute(Object arg) {
                _serialBuffer.println((String) arg);
                String answer = _serialBuffer.WaitToReadLine();
                return Double.valueOf(answer);
            }
        });

        _commands.add(new Command("askFloat") {
            @Override
            public Object execute(Object arg) {
                _serialBuffer.println((String) arg);
                String answer = _serialBuffer.WaitToReadLine();
                return Float.valueOf(answer);
            }
        });

        _commands.add(new Command("askInteger") {
            @Override
            public Object execute(Object arg) {
                _serialBuffer.println((String) arg);
                String answer = _serialBuffer.WaitToReadLine();
                return Integer.valueOf(answer);
            }
        });

        _commands.add(new Command("askBoolean") {
            @Override
            public Object execute(Object arg) {
                _serialBuffer.println((String) arg);
                String answer = _serialBuffer.WaitToReadLine();
                return Boolean.valueOf(answer);
            }
        });

        _commands.add(new Command("askDoubleArray") {
            @Override
            public Object execute(Object arg) {
                _serialBuffer.println((String) arg);
                String answer = _serialBuffer.WaitToReadLine();
                int NArray = Integer.valueOf(answer);
                ArrayList<Double> R = new ArrayList<Double>();
                for (int i = 0; i < NArray; i++)
                    R.add(Double.valueOf(_serialBuffer.WaitToReadLine()));
                return R;
            }
        });

        _commands.add(new Command("askFloatArray") {
            @Override
            public Object execute(Object arg) {
                _serialBuffer.println((String) arg);
                String answer = _serialBuffer.WaitToReadLine();
                int NArray = Integer.valueOf(answer);
                ArrayList<Float> R = new ArrayList<Float>();
                for (int i = 0; i < NArray; i++)
                    R.add(Float.valueOf(_serialBuffer.WaitToReadLine()));
                return R;
            }
        });

        _commands.add(new Command("askIntegerArray") {
            @Override
            public Object execute(Object arg) {
                _serialBuffer.println((String) arg);
                String answer = _serialBuffer.WaitToReadLine();
                int NArray = Integer.valueOf(answer);
                ArrayList<Integer> R = new ArrayList<Integer>();
                for (int i = 0; i < NArray; i++)
                    R.add(Integer.valueOf(_serialBuffer.WaitToReadLine()));
                return R;
            }
        });
    }
}