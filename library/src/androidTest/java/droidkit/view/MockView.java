package droidkit.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import droidkit.annotation.InjectView;

/**
 * @author Daniel Serdyukov
 */
public class MockView extends LinearLayout {

    @InjectView(droidkit.test.R.id.text1)
    public TextView mText1;

    public MockView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ViewInjector.inject(this, this);
    }

}
