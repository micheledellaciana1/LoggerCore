package LoggerCore;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;

public class test {
    public static void main(String[] args) throws IOException {
        DataManger2 dataManager = new DataManger2(CSVFormat.DEFAULT.withDelimiter(' ').withHeader());
        CSVParser parser = dataManager.loadData(new File("C:\\Users\\utente\\Desktop\\prova.txt"));

        ArrayList<ArrayList<Double>> columns = dataManager
                .parseDoubleColumnsAUTO(new File("C:\\Users\\utente\\Desktop\\prova.txt"));

        System.out.println(parser.getHeaderNames());
        for (int i = 0; i < columns.size(); i++) {
            for (int j = 0; j < columns.get(i).size(); j++) {
                System.out.print(" ");
                System.out.print(columns.get(i).get(j));
            }
            System.out.print('\n');
        }

        dataManager.saveData(new File("C:\\Users\\utente\\Desktop\\prova2.txt"), parser.getHeaderNames(), columns);
    }
}
