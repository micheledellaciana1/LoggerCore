package LoggerCore.Communication;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import com.fazecast.jSerialComm.SerialPort;

public class SerialBuffer {
    private InputStream _in;
    private OutputStream _out;

    private byte[] _readBuffer;
    private int _posRB;
    private byte[] _writeBuffer;
    private int _posWB;

    public boolean verbose = true;

    private ArrayList<String> _LinesBuffer;

    private SerialPort _serial;
    public int delayBeforeUpdate;

    public SerialBuffer(SerialPort port) {
        _serial = port;
        _in = _serial.getInputStream();
        _out = _serial.getOutputStream();

        _readBuffer = new byte[1000000];
        _posRB = 0;
        _writeBuffer = new byte[1000000];
        _posWB = 0;
        _LinesBuffer = new ArrayList<String>();

        delayBeforeUpdate = 0;
    }

    public int bytesAvaible() {
        return _posRB;
    }

    private void update() throws IOException {
        if (_in.available() > 0) {
            delay(delayBeforeUpdate);
            byte[] newBytes = new byte[_in.available()];
            _in.read(newBytes);
            for (int i = 0; i < newBytes.length; i++) {
                _readBuffer[_posRB++] = newBytes[i];
            }
        }
    }

    static public void delay(int millis) {
        if (millis > 0) {
            try {
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void flushWriter() {
        try {
            _out.write(_writeBuffer, 0, _posWB);
        } catch (IOException e) {
            if (verbose)
                e.printStackTrace();
        }
        _posWB = 0;
    }

    public void write(String value) {
        byte[] newBytes = value.getBytes();
        for (int i = 0; i < newBytes.length; i++)
            _writeBuffer[_posWB++] = newBytes[i];
    }

    public void print(String value) {
        if (_serial.isOpen()) {
            write(value);
            flushWriter();
        }
    }

    public void println(String value) {
        if (_serial.isOpen()) {
            write(value);
            WriteNewLine();
            flushWriter();
        }
    }

    public void WriteNewLine() {
        _writeBuffer[_posWB++] = '\n';
    }

    public String readLine() {
        try {
            update();
        } catch (IOException e) {
            if (verbose)
                e.printStackTrace();
        }

        String R = null;
        int IndexStartLine = 0;

        for (int i = IndexStartLine; i < _posRB; i++)
            if (_readBuffer[i] == '\n') {
                R = new String(_readBuffer, IndexStartLine, i - IndexStartLine);
                R = R.trim();

                _LinesBuffer.add(R);
                IndexStartLine = i + 1;
            }

        for (int j = 0; j < _posRB - IndexStartLine; j++)
            _readBuffer[j] = _readBuffer[j + IndexStartLine];

        _posRB = _posRB - IndexStartLine;

        String NextLine = null;
        if (_LinesBuffer.size() > 0) {
            NextLine = _LinesBuffer.get(0);
            _LinesBuffer.remove(0);
        }

        return NextLine;
    }

    public String WaitToReadLine() {
        String S = null;
        while (S == null)
            S = readLine();
        return S;
    }

    public void resetReader() {
        _posRB = 0;
    }

    public void resetWriter() {
        _posWB = 0;
    }

    public void EstablishConnection(String DeviceName) {
        String readString = null;
        do {
            while (readString == null) {
                delay(10);
                readString = readLine();
            }

        } while (!readString.equalsIgnoreCase(DeviceName + "_Ready"));

        while (!readString.equalsIgnoreCase("EstablishedConnection")) {
            do {
                write(DeviceName + "_Open");
                WriteNewLine();
                flushWriter();
                readString = readLine();
            } while (readString == null);
        }

        resetReader();
        System.out.println("<EstablishConnection>");
    }

    public SerialPort getSerialPort() {
        return _serial;
    }
}