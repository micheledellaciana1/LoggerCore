package LoggerCore.Icaro.ThermistorLUT;

import java.util.ArrayList;

import LoggerCore.themal.LookUpTable;

/**
 * PT100
 */
public class PT100 {

    static public LookUpTable instance = calcolateLUT(-273.15, 1500, 3.9083e-3, -5.775e-7);

    private PT100() {
    }

    static public LookUpTable calcolateLUT(double minTemp, double maxTemp, double alpha1, double alpha2) {
        ArrayList<Double> Temp = new ArrayList<Double>();
        ArrayList<Double> Resistance = new ArrayList<Double>();

        for (double T = minTemp; T < maxTemp; T += 1) {
            Temp.add(T);
            Resistance.add(100 * (1 + alpha1 * (T) + alpha2 * Math.pow(T, 2)));
        }
        return new LookUpTable(Temp, Resistance);
    }
}