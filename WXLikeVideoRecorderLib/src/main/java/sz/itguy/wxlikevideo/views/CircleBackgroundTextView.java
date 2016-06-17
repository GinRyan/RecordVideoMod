package sz.itguy.wxlikevideo.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * Created by Administrator on 2015/9/15.
 */
public class CircleBackgroundTextView extends TextView {

    private static final int DEFAULT_STROKE_WIDTH = 2;
    public static final int PROGRESS_ARC_STROKE_WIDTH = 4;
    private Paint paint;
    private Paint paintOuter;

    private RectF rectF = new RectF();
    private RectF rectFOuter = new RectF();
    private float sweepedAngle = 0;
    public static final float DELTA_ANGLE = 1;
    public static final float STARTER_ANGLE_POS = -90;

    public CircleBackgroundTextView(Context context) {
        this(context, null);
    }

    public CircleBackgroundTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleBackgroundTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setGravity(Gravity.CENTER);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(DEFAULT_STROKE_WIDTH);//粗度2
        paint.setStyle(Paint.Style.STROKE);

        paintOuter = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintOuter.setColor(Color.RED);
        paintOuter.setStrokeWidth(PROGRESS_ARC_STROKE_WIDTH);//粗度4
        paintOuter.setStyle(Paint.Style.STROKE);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        rectF.set(DEFAULT_STROKE_WIDTH / 2f, DEFAULT_STROKE_WIDTH / 2f, canvas.getWidth() - DEFAULT_STROKE_WIDTH / 2f, canvas.getHeight() - DEFAULT_STROKE_WIDTH / 2f);
        rectFOuter.set(DEFAULT_STROKE_WIDTH / 2f, DEFAULT_STROKE_WIDTH / 2f, canvas.getWidth() - DEFAULT_STROKE_WIDTH / 2f, canvas.getHeight() - DEFAULT_STROKE_WIDTH / 2f);
        canvas.drawOval(rectF, paint);

        canvas.drawArc(rectFOuter, STARTER_ANGLE_POS, sweepedAngle, false, paintOuter);
        if (touching) {
            sweepedAngle += DELTA_ANGLE;
            invalidate();
        }
    }

    boolean touching = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                touching = true;
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                //do nothing.
                break;
            case MotionEvent.ACTION_UP:
                touching = false;
                sweepedAngle = 0f;
                break;
        }
        super.onTouchEvent(event);
        return true;
    }

}
