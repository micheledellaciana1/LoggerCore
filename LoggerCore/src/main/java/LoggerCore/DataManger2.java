package LoggerCore;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class DataManger2 {
    private CSVFormat _format;
    char[] _possibleDivider = new char[] { ' ', ',', '\t', ':', ';' };

    public DataManger2(CSVFormat format) {
        _format = format;
    }

    public DataManger2() {
        _format = GlobalVar.defaulFormat;
    }

    public CSVParser loadData(File file) {
        try {
            return CSVParser.parse(file, Charset.defaultCharset(), _format);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<ArrayList<Double>> parseDoubleColumnsAUTO(File file) {
        try {
            for (char c : _possibleDivider) {
                CSVFormat format = _format.withDelimiter(c);
                CSVParser parser = CSVParser.parse(file, Charset.defaultCharset(), format);

                ArrayList<ArrayList<Double>> result = parseDoubleColumns(parser.getRecords());
                if (result != null) {
                    System.out.println("Detected delimiter: " + c);
                    return result;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static ArrayList<ArrayList<Double>> parseDoubleColumns(List<CSVRecord> records) {
        try {
            double sizes[] = new double[records.size()];
            for (int i = 0; i < sizes.length; i++)
                sizes[i] = records.get(i).size();

            double NumberColumns = Arrays.stream(sizes).max().getAsDouble();

            ArrayList<ArrayList<Double>> ListOfColumns = new ArrayList<ArrayList<Double>>();
            for (int i = 0; i < NumberColumns; i++)
                ListOfColumns.add(new ArrayList<Double>());

            for (int i = 0; i < records.size(); i++) {
                CSVRecord record = records.get(i);

                ArrayList<Double> DoubleRecord = new ArrayList<Double>();
                try {
                    for (int j = 0; j < record.size(); j++)
                        DoubleRecord.add(Double.valueOf(record.get(j)));
                } catch (Exception e) {
                    continue;
                }

                for (int j = 0; j < NumberColumns; j++) {
                    try {
                        if (j < record.size())
                            ListOfColumns.get(j).add(DoubleRecord.get(j));
                    } catch (Exception e) {
                    }
                }
            }

            for (int i = 0; i < ListOfColumns.size(); i++)
                if (ListOfColumns.get(i).size() == 0) {
                    ListOfColumns.remove(ListOfColumns.get(i--));
                }

            if (ListOfColumns.size() == 0)
                return null;

            return ListOfColumns;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public <T> void saveData(File output, List<String> header, List<? extends List<T>> ListOfColumns) {
        try {
            CSVPrinter printer = _format.print(output, Charset.defaultCharset());
            ArrayList<List<String>> toPrint = new ArrayList<List<String>>();
            toPrint.add(header);

            double sizes[] = new double[ListOfColumns.size()];
            for (int i = 0; i < sizes.length; i++)
                sizes[i] = ListOfColumns.get(i).size();

            double maxSize = Arrays.stream(sizes).max().getAsDouble();

            for (int i = 0; i < maxSize; i++) {
                ArrayList<String> record = new ArrayList<String>();
                for (int j = 0; j < ListOfColumns.size(); j++) {
                    String valueToAdd = ".";
                    if (i < ListOfColumns.get(j).size())
                        valueToAdd = ListOfColumns.get(j).get(i).toString();
                    record.add(valueToAdd);
                }

                toPrint.add(record);
            }

            printer.printRecords(toPrint);
            printer.close(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveData(File output, List<String> header, XYSeries... series) {
        try {
            ArrayList<ArrayList<Double>> Columns = new ArrayList<ArrayList<Double>>();
            for (XYSeries xySeries : series) {
                ArrayList<Double> X = new ArrayList<Double>();
                ArrayList<Double> Y = new ArrayList<Double>();

                for (Object a : xySeries.getItems()) {
                    X.add(XYDataItem.class.cast(a).getXValue());
                    Y.add(XYDataItem.class.cast(a).getYValue());
                }

                Columns.add(X);
                Columns.add(Y);
            }

            saveData(output, header, Columns);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveData(File output, XYSeries... series) {
        ArrayList<String> headers = new ArrayList<String>();
        for (int i = 0; i < series.length; i++) {
            headers.add(series[i].getKey().toString() + "_X");
            headers.add(series[i].getKey().toString() + "_Y");
        }

        saveData(output, headers, series);
    }

    public void saveData(File output, List<String> header, XYSeriesCollection series) {
        saveData(output, header, XYSeries[].class.cast(series.getSeries().toArray()));
    }

    public void saveData(File output, XYSeriesCollection series) {
        ArrayList<String> headers = new ArrayList<String>();
        for (int i = 0; i < series.getSeriesCount(); i++)
            headers.add(series.getSeries(i).getKey().toString());

        saveData(output, headers, series);
    }

    public static <T> ArrayList<ArrayList<T>> combineColumns(ArrayList<ArrayList<T>>... data) {
        ArrayList<ArrayList<T>> result = new ArrayList<ArrayList<T>>();
        for (ArrayList<ArrayList<T>> arrayList : data) {
            for (ArrayList<T> arrayList2 : arrayList) {
                result.add(arrayList2);
            }
        }

        return result;
    }

    public static XYSeries combineColumns(Comparable<?> key, ArrayList<Double> x, ArrayList<Double> y) {
        XYSeries result = new XYSeries(key, false);
        for (int i = 0; i < x.size(); i++)
            result.add(x.get(i), y.get(i));

        return result;
    }

    public static XYSeries createHistogram(Comparable<?> key, ArrayList<Double> members, int NumberBinning) {
        XYSeries result = new XYSeries(key, true);

        double max = Collections.max(members);
        double min = Collections.min(members);

        double dx = (max - min) / NumberBinning;

        double y;
        for (double x = min; x <= max; x += dx) {
            y = 0;
            for (int i = 0; i < members.size(); i++)
                if (members.get(i) >= x && members.get(i) <= x + dx)
                    y++;
            result.add(x, y);
        }

        return result;
    }
}
