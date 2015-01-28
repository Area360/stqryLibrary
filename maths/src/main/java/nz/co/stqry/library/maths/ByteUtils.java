package nz.co.stqry.library.maths;

import java.util.Arrays;

/**
 * Created by Marc Giovannoni on 7/11/14.
 */
public class ByteUtils {

    private static final char[] HEX_ARRAY = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
    private static final int MAX_UNSIGNED_SHORT = Double.valueOf(Math.pow(2.0D, 16.0D)).intValue();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for ( int j = 0; j < bytes.length; j++ ) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte[] toTwoByteArray(int value)
    {
        return new byte[] { (byte)value, (byte)(value >> 8) };
    }

    public static boolean validateUnsignedShort(int value) {
        return value > 0 && value < MAX_UNSIGNED_SHORT;
    }

    public static byte[] invert(byte[] array)
    {
        int size = array.length;
        int limit = size / 2;
        int i = 0;

        while (i < limit) {
            byte temp = array[i];
            array[i] = array[(size - 1 - i)];
            array[(size - 1 - i)] = temp;
            i++;
        }

        return array;
    }

    public static int byteArrayToInt(byte[] b)
    {
        int value = 0;

        for (int i = 0; i < b.length; i++) {
            int shift = (b.length - 1 - i) * 8;
            value += (b[i] & 0x000000FF) << shift;
        }
        return value;
    }

    public static String byteToString(byte[] value) {
        int nbChar = 0;

        while ((nbChar < value.length) && (value[nbChar] != 0)) {
            ++nbChar;
        }
        return new String(Arrays.copyOf(value, nbChar));
    }
}
