package ryancheng.bus.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.graphics.drawable.DrawableCompat;

import ryancheng.bus.R;

/**
 * Created by ryan on 2015/5/29.
 */
public class Util {
    public static Drawable getDrawableTint(Context context, int id) {
        Drawable drawable;
        if (Build.VERSION.SDK_INT >= 21) {
            drawable = context.getResources().getDrawable(id, context.getTheme());
        } else {
            drawable = context.getResources().getDrawable(id);
        }
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, context.getResources().getColor(R.color.primary_color));
        return drawable;
    }

}
