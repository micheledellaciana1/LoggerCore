package LoggerCore;

import org.apache.commons.csv.CSVFormat;

public class GlobalVar {
    public static long start = System.currentTimeMillis();
    public static CSVFormat defaulFormat = CSVFormat.DEFAULT.withDelimiter('\t');
    public static DataManger2 defaultDataManager = new DataManger2(defaulFormat);
}
