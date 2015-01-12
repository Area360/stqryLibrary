package nz.co.stqry.library.maths.utils;

import java.util.Arrays;

/**
 * Created by Marc Giovannoni on 30/09/14.
 */
public class MathsUtils {

    public static float mod(float a, float b) {
        return (a % b + b) % b;
    }

    public static float remap(float value, float low1, float high1, float low2, float high2){
        return low2 + (value - low1) * (high2 - low2) / (high1 - low1);
    }

    public static int median(Integer[] values) {
        Arrays.sort(values);
        int median;
        int mid = values.length / 2;

        if (values.length % 2 == 0) {
            median = (values[mid] + values[mid - 1]) / 2;
        } else
            median = (values[mid]);
        return median;
    }
}
