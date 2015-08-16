package ryancheng.bus;

import android.content.Context;
import android.util.AttributeSet;

import xyz.danoz.recyclerviewfastscroller.sectionindicator.title.SectionTitleIndicator;

/**
 * Administrator
 * 2015/8/16 0016.
 */
public class StringSectionTitleIndicator extends SectionTitleIndicator<MainActivity.Item> {


    public StringSectionTitleIndicator(Context context) {
        super(context);
    }


    public StringSectionTitleIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public StringSectionTitleIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setSection(MainActivity.Item item) {
        setTitleText(item.name.charAt(0) + "");
    }

}
