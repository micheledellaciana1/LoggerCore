package LoggerCore.themal;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

public class LookUpTable {

    private ArrayList<Double> _X;
    private ArrayList<Double> _Y;

    public LookUpTable(String FileName) {
        importFromFile(FileName);
    }

    public void importFromFile(String FileName) {
        BufferedReader BufferR = null;
        _X = new ArrayList<Double>();
        _Y = new ArrayList<Double>();
        try {
            BufferR = new BufferedReader(new FileReader(FileName));

            String line = new String();

            while ((line = BufferR.readLine()) != null) {
                String[] lineSplitted = line.split("\t");
                try {
                    _X.add(Double.valueOf(lineSplitted[0]));
                    _Y.add(Double.valueOf(lineSplitted[1]));
                } catch (Exception e) {
                }
            }
        } catch (Exception e) {
        } finally {
            try {
                BufferR.close();
            } catch (Exception e) {
            }
        }
    }

    public void exportToFile(String FileName) {
        BufferedWriter BufferW = null;

        try {
            BufferW = new BufferedWriter(new FileWriter(FileName));
            for (int i = 0; i < _X.size(); i++) {
                BufferW.write(_X.get(i) + "\t" + _Y.get(i) + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                BufferW.close();
            } catch (Exception e) {
            }
        }
    }

    public LookUpTable(ArrayList<Double> XArray, ArrayList<Double> YArray) {
        _X = (ArrayList<Double>) XArray.clone();
        _Y = (ArrayList<Double>) YArray.clone();
    }

    public double getY(double X) {
        try {
            for (int i = 0; i < _Y.size() - 1; i++)
                if (X > _X.get(i) && X <= _X.get(i + 1)) {
                    return _Y.get(i) + (_Y.get(i + 1) - _Y.get(i)) / (_X.get(i + 1) - _X.get(i)) * (X - _X.get(i));
                }
            return 0.;
        } catch (Exception e) {
            return 0.;
        }
    }

    public double getX(double Y) {
        try {
            for (int i = 0; i < _X.size() - 1; i++)
                if (Y > _Y.get(i) && Y <= _Y.get(i + 1)) {
                    return _X.get(i) + (_X.get(i + 1) - _X.get(i)) / (_Y.get(i + 1) - _Y.get(i)) * (Y - _Y.get(i));
                }
            return 0.;
        } catch (Exception e) {
            return 0.;
        }
    }

    public void set_X(ArrayList<Double> _X) {
        this._X = _X;
    }

    public void set_Y(ArrayList<Double> _Y) {
        this._Y = _Y;
    }

    public ArrayList<Double> get_X() {
        return _X;
    }

    public ArrayList<Double> get_Y() {
        return _Y;
    }
}