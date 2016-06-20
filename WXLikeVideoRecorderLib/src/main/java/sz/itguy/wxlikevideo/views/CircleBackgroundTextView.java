package sz.itguy.wxlikevideo.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
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
    private OnFingerDownListener mOnFingerDownListener;
    private OnFingerUpListener mOnFingerUpListener;
    private OnFingerMoveListener mOnFingerMoveListener;

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

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x1000) {
                if (mOnProgressListener != null) {
                    mOnProgressListener.onProgress(msg.arg1);
                }
            }
        }
    };
    OnProgressListener mOnProgressListener;

    public void setOnProgressListener(OnProgressListener mOnProgressListener) {
        this.mOnProgressListener = mOnProgressListener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        rectF.set(DEFAULT_STROKE_WIDTH / 2f, DEFAULT_STROKE_WIDTH / 2f, canvas.getWidth() - DEFAULT_STROKE_WIDTH / 2f, canvas.getHeight() - DEFAULT_STROKE_WIDTH / 2f);
        rectFOuter.set(DEFAULT_STROKE_WIDTH / 2f, DEFAULT_STROKE_WIDTH / 2f, canvas.getWidth() - DEFAULT_STROKE_WIDTH / 2f, canvas.getHeight() - DEFAULT_STROKE_WIDTH / 2f);
        canvas.drawOval(rectF, paint);

        canvas.drawArc(rectFOuter, STARTER_ANGLE_POS, sweepedAngle, false, paintOuter);
        if (touching) {
            Message msg = Message.obtain();
            msg.what = 0x1000;
            msg.arg1 = (int) (sweepedAngle * 100 / 360f);
            if (sweepedAngle < 360) {
                sweepedAngle += DELTA_ANGLE;
                invalidate();
                handler.sendMessage(msg);
            } else {
                handler.sendMessage(msg);
                sweepedAngle = 0;
            }
        }
    }


    public void reset() {
        sweepedAngle = 0;
        touching = false;
    }

    boolean touching = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean superResult = super.onTouchEvent(event);
        boolean handled = false;
        int action = MotionEventCompat.getActionMasked(event);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                touching = true;
                handled = true;
                invalidate();
                if (mOnFingerDownListener != null) {
                    mOnFingerDownListener.onDown(this, event);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mOnFingerMoveListener != null) {
                    mOnFingerMoveListener.onMove(this, event);
                }
                break;
            case MotionEvent.ACTION_UP:
                touching = false;
                sweepedAngle = 0f;
                handled = false;
                if (mOnFingerUpListener != null) {
                    mOnFingerUpListener.onUp(this, event);
                }
                handler.removeMessages(0x1000);
                break;
        }
        if (handled) {
            return true;
        }
        return superResult;
    }

    public CircleBackgroundTextView setOnFingerMoveListener(OnFingerMoveListener ofd) {
        this.mOnFingerMoveListener = ofd;
        return this;
    }

    public CircleBackgroundTextView setOnFingerDownListener(OnFingerDownListener ofd) {
        this.mOnFingerDownListener = ofd;
        return this;
    }

    public CircleBackgroundTextView setOnFingerUpListener(OnFingerUpListener ofu) {
        this.mOnFingerUpListener = ofu;
        return this;
    }

    public CircleBackgroundTextView setOnProgressFullListener(OnFingerUpListener ofu) {
        this.mOnFingerUpListener = ofu;
        return this;
    }


    public interface OnFingerDownListener {
        public void onDown(CircleBackgroundTextView view, MotionEvent event);
    }

    public interface OnFingerMoveListener {
        public void onMove(CircleBackgroundTextView view, MotionEvent event);
    }

    public interface OnFingerUpListener {
        public void onUp(CircleBackgroundTextView view, MotionEvent event);
    }

    public interface OnProgressListener {
        public void onProgress(int progress);
    }
}
