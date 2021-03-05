package LoggerCore.Icaro.ThermistorLUT;

import java.util.ArrayList;

import LoggerCore.themal.LookUpTable;

/**
 * NTC_100K
 */
public class NTC_100K {

    static public LookUpTable instance = calcolateLUT(-50, 500, 4066);

    private NTC_100K() {
    }

    static public LookUpTable calcolateLUT(double minTemp, double maxTemp, double beta) {
        ArrayList<Double> Temp = new ArrayList<Double>();
        ArrayList<Double> Resistance = new ArrayList<Double>();

        for (double T = maxTemp; T > minTemp; T -= 1) {
            Temp.add(T);
            Resistance.add(100e3 * Math.exp(-beta * (1 / 298.15 - 1. / (T + 273.15))));
        }
        return new LookUpTable(Resistance, Temp);
    }
}