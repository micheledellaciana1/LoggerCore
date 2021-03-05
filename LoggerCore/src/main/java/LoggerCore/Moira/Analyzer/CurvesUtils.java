package LoggerCore.Moira.Analyzer;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.fitting.HarmonicCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoint;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

public class CurvesUtils {

    static public double[] FitHarmonic(ArrayList<Point2D> curve, double[] starting_point) {
        HarmonicCurveFitter harmonicFitter = HarmonicCurveFitter.create();

        if (starting_point != null) {
            harmonicFitter.withStartPoint(starting_point);
        }
        // harmonicFitter.withMaxIterations(50);
        ArrayList<WeightedObservedPoint> points = new ArrayList<WeightedObservedPoint>();

        for (int i = 0; i < curve.size(); i++)
            points.add(new WeightedObservedPoint(1, curve.get(i).getX(), curve.get(i).getY()));

        double[] parHarmonic = new double[] { -1, -1, -1 };

        try {
            parHarmonic = harmonicFitter.fit(points);
        } catch (Exception e) {
        }

        return parHarmonic;
    }

    static public double[] FitHarmonic2(double[] curve) {

        FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);
        double[] curvepadded = new double[8192];
        for (int i = 0; i < 8192; i++) {
            if (i < curve.length)
                curvepadded[i] = curve[i];
            else
                curvepadded[i] = 0;
        }

        Complex[] ftCurve = fft.transform(curvepadded, TransformType.FORWARD);

        double[] amplitude = new double[ftCurve.length];
        double[] phase = new double[ftCurve.length];

        double maxAmplitude = 0;
        int maxAmplitudeIdx = 0;

        for (int i = 0; i < ftCurve.length; i++) {
            amplitude[i] = Math.sqrt(ftCurve[i].getReal() * ftCurve[i].getReal()
                    + ftCurve[i].getImaginary() * ftCurve[i].getImaginary());
            phase[i] = Math.atan2(ftCurve[i].getReal(), ftCurve[i].getImaginary());

            if (amplitude[i] >= maxAmplitude) {
                maxAmplitude = amplitude[i];
                maxAmplitudeIdx = i;
            }
        }

        double[] parHarmonic = new double[] { amplitude[maxAmplitudeIdx], maxAmplitudeIdx, phase[maxAmplitudeIdx] };
        return parHarmonic;
    }

    static public double[] fft_mod(double[] curve, int padvalue) {

        FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);
        double[] curvepadded = new double[padvalue];
        for (int i = 0; i < padvalue; i++) {
            if (i < curve.length)
                curvepadded[i] = curve[i];
            else
                curvepadded[i] = 0;
        }

        Complex[] ftCurve = fft.transform(curvepadded, TransformType.FORWARD);

        double[] amplitude = new double[ftCurve.length];

        for (int i = 0; i < ftCurve.length; i++) {
            amplitude[i] = Math.log(Math.sqrt(ftCurve[i].getReal() * ftCurve[i].getReal()
                    + ftCurve[i].getImaginary() * ftCurve[i].getImaginary()));
        }

        return amplitude;
    }
}
