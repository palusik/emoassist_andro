package emoassist.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.speech.tts.Voice;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import emoassist.type.VoiceType;

/**
 * The view that renders the emotion value
 */
public class EmotionBarView extends RelativeLayout {

    /**
     * The current emotion value
     */
    public float emotionValue = 0;
    /**
     * The default color of the bar
     */
    public int color = Color.BLACK;
    /**
     * The voice identifier in a multi voice app
     */
    public VoiceType voiceType;

    /**
     * Required initializer of the emotion bar view
     * @param context Activity context
     */
    public EmotionBarView(Context context)
    {
        super(context);
    }

    /**
     * Required initializer of the emotion bar view
     * @param context Activity context
     * @param attrs View attributes
     */
    public EmotionBarView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    /**
     * Required initializer of the emotion bar view
     * @param context Activity context
     * @param attrs View attributes
     * @param defStyle View def style
     */
    public EmotionBarView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    /**
     * Triggered when the view needs to redraw
     * @param canvas The canvas object used for drawing contents
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if( voiceType != null ) {
            Rect rectangle = null;
            switch (voiceType) {
                case Voice1:
                    rectangle = new Rect(0, 0, (int)(getWidth() * emotionValue), getHeight());
                    break;
                case Voice2:
                    rectangle = new Rect((int)(getWidth() * (1 - emotionValue)), 0, (int)getWidth(), getHeight());
                    break;
            }

            Paint paint = new Paint();
            paint.setColor(color);
            canvas.drawRect(rectangle, paint);
        }
    }
}
