package nz.co.stqry.library.android.bitmap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by Marc Giovannoni on 15/12/14.
 */
public class BitmapUtils {

    public static Bitmap scalePower2(Bitmap bitmap) {
        if (((Math.log((double) bitmap.getWidth()) / Math.log(2.0)) - Math.floor((Math.log((double) bitmap.getWidth()) / Math.log(2.0)))) != 0) {

            int aimWidth = (int) Math.pow(2, Math.ceil(Math.log((double)
                    bitmap.getWidth()) / Math.log(2.0)));

            bitmap = Bitmap.createScaledBitmap(bitmap, aimWidth, bitmap.getHeight(), false);
        } else if (((Math.log((double) bitmap.getHeight()) / Math.log(2.0)) - Math.floor((Math.log((double) bitmap.getHeight()) / Math.log(2.0)))) != 0) {

            int aimHeight = (int) Math.pow(2, Math.ceil(Math.log((double)bitmap.getHeight()) / Math.log(2.0)));

            bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), aimHeight, false);
        }
        return bitmap;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
