package droidkit.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import droidkit.annotation.OnEvent;
import droidkit.app.EventBus;
import droidkit.app.MockEvent;

/**
 * @author Daniel Serdyukov
 */
public class MockFrameLayout extends FrameLayout {

    public MockEvent mEvent;

    public MockFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        EventBus.register(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        EventBus.unregister(this);
        super.onDetachedFromWindow();
    }

    @OnEvent
    void onMockEvent(MockEvent event) {
        mEvent = event;
    }

}
