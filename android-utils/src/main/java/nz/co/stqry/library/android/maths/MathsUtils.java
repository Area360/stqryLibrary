package nz.co.stqry.library.android.maths;

/**
 * Created by Marc Giovannoni on 18/12/14.
 */
public class MathsUtils {

    public static int closestPowerOf2(int n)
    {
        int res = 2;

        while (res < n)
            res *= 2;
        return res;
    }
}
