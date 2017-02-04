package at.danielbigler.viewpagerindicatorview.utils;

import android.content.res.Resources;
import android.util.TypedValue;

/**
 * Created on 28.11.2016.
 *
 * @version 1.0
 * @author Daniel Bigler
 */

/**
 * Class for converting density values
 */
public class DensityUtils {

    /**
     * Converts pixels to dip's
     *
     * @param px Pixels
     * @return Pixels converted to dip'ss
     */
    @SuppressWarnings("unused")
    public static int pxToDp(int px) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, px, Resources.getSystem().getDisplayMetrics()));
    }

    /**
     * Converts dip's to pixels
     *
     * @param dp Density independent pixels
     * @return Dip's converted to pixels
     */
    public static int dpToPx(int dp) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics()));
    }
}
