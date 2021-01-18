package LoggerCore;

import java.io.*;
import java.util.*;

import org.jfree.data.xy.XYSeries;

public class DataManager {

    private ArrayList<ArrayList<Double>> _data;
    private ArrayList<String> _label;
    private String _divider;

    public boolean verbose = true;

    public DataManager(String divider) {
        _data = new ArrayList<ArrayList<Double>>();
        _label = new ArrayList<String>();
        _divider = divider;
    }

    public DataManager(File csvFile, String divider) {
        _data = new ArrayList<ArrayList<Double>>();
        _label = new ArrayList<String>();
        _divider = divider;

        load(csvFile);
    }

    public void addColoumn(String name) {
        if (_label.contains(name)) {
            System.out.println(name + " already present");
            return;
        }

        _label.add(name);
        _data.add(new ArrayList<Double>());
    }

    public void add(int index, double data) {
        _data.get(index).add(data);
    }

    public void add(String name, double data) {
        if (_label.contains(name))
            _data.get(_label.indexOf(name)).add(data);
    }

    public String getLabel(int i) {
        return _label.get(i);
    }

    public ArrayList<Double> getData(int i) {
        return _data.get(i);
    }

    public ArrayList<ArrayList<Double>> getData() {
        return _data;
    }

    public void load(String fileName) {
        File file = new File(fileName);
        load(file);
    }

    String[] _possibleDivider = new String[] { " ", ",", "\t", ":", ";", "  ", "   ", "    " };

    private int FirstDataLine(File file) throws FileNotFoundException {
        Scanner reader = new Scanner(file);
        for (int i = 0; reader.hasNextLine(); i++)
            for (String divider : _possibleDivider) {
                String element[] = reader.nextLine().split(divider);
                for (int j = 0; j < element.length; j++)
                    try {
                        Double.valueOf(element[j].trim());
                        return i;
                    } catch (Exception e) {
                    }
            }

        return -1;
    }

    public void load(File file) {

        for (String divider : _possibleDivider) {
            EraseAllData();
            try {
                Scanner reader = new Scanner(file);
                String title[] = reader.nextLine().split(divider);

                for (int i = 0; i < title.length; i++)
                    addColoumn(file.getName() + "/" + title[i].trim());

                while (reader.hasNextLine()) {
                    String element[] = reader.nextLine().split(divider);
                    for (int i = 0; i < element.length; i++)
                        add(i, Double.valueOf(element[i].trim()));
                }

                reader.close();
                break;
            } catch (Exception e) {
            }
        }
    }

    public void save(String fileName) {
        if (isEmpty())
            return;

        File _file = new File(fileName);

        try {
            FileWriter _fileWriter = new FileWriter(_file);
            BufferedWriter _bufferedWriter = new BufferedWriter(_fileWriter);

            ArrayList<Integer> sizes = new ArrayList<Integer>();
            for (int i = 0; i < _data.size(); i++)
                sizes.add(_data.get(i).size());

            int max_size = Collections.max(sizes);

            for (int i = 0; i < _data.size() - 1; i++)
                _bufferedWriter.write(_label.get(i) + _divider);
            _bufferedWriter.write(_label.get(_data.size() - 1));
            _bufferedWriter.newLine();

            for (int i = 0; i < max_size; i++) {
                for (int j = 0; j < _data.size() - 1; j++)
                    if (i < _data.get(j).size())
                        _bufferedWriter.write(_data.get(j).get(i) + _divider);
                    else
                        _bufferedWriter.write("." + _divider);

                if (i < _data.get(_data.size() - 1).size())
                    _bufferedWriter.write(_data.get(_data.size() - 1).get(i).toString());
                _bufferedWriter.newLine();
            }

            _bufferedWriter.flush();
            _fileWriter.close();
            _bufferedWriter.close();

        } catch (IOException e) {
            if (verbose)
                e.printStackTrace();
        }
    }

    public XYSeries getAsXYSeries(Comparable<?> KeySeries, int XIdx, int YIdx) {
        XYSeries serie = new XYSeries(KeySeries, false);

        try {
            for (int i = 0; i < _data.get(XIdx).size(); i++)
                serie.add(_data.get(XIdx).get(i), _data.get(YIdx).get(i));

        } catch (Exception e) {
            throw e;
        }

        return serie;
    }

    public void add(DataManager addend) {
        for (int i = 0; i < addend._data.size(); i++) {
            if (!_label.contains(addend.getLabel(i))) {
                _label.add(addend.getLabel(i));
                _data.add(addend.getData(i));
            } else {
                System.out.println(addend.getLabel(i) + " already presents");
            }
        }
    }

    public boolean isEmpty() {
        if (_label.size() == 0)
            return true;
        else
            return false;
    }

    public void EraseAllData() {
        _data = new ArrayList<ArrayList<Double>>();
        _label = new ArrayList<String>();
    }
}