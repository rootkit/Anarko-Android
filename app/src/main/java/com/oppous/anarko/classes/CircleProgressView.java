package com.oppous.anarko.classes;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.oppous.anarko.R;

import java.util.Timer;
import java.util.TimerTask;

public class CircleProgressView extends View {
    private int startProgress = -90;//top
    private int progress = 0;
    private int style = 2;
    private int topProgress = 360;
    // blue
    private int colorProgress = Color.parseColor("#33CCFF"); //ring color
    // green
    private int colorDone = Color.parseColor("#8DF821"); //complete color
    private int colorWrong = Color.RED;
    private int colorBaseFill = Color.WHITE;
    // grey
    private int colorBaseStroke = Color.parseColor("#D8D8D8");
    private int colorWrongBaseStroke = Color.parseColor("#D8D8D8");
    private boolean init = true;
    private boolean error = false;
    private Paint paint;
    private Paint paintDone;
    private Paint paintWrong;
    private Paint paintBaseFill;
    private Paint paintBaseStroke;
    private int strokeWidth = 6;
    private RectF oval = new RectF();
    private float radius;
    private float center_x, center_y;
    private Timer timer;
    private MyTimerTask myTimerTask;
    private int progressPeriodMills = 100;
    private int duration = 10;
    private CircleProgressCallback mCallback = null;
    private Context context;
    private boolean isStarted = false;

    public CircleProgressView(Context context) {
        this(context, null);
//        this.setOnClickListener(clickListener);
    }

    public CircleProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public CircleProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        getAttr(context, attrs);
        setPaint();
//        this.setOnClickListener(clickListener);
    }


    public CircleProgressView(Context context, CircleProgressViewStyles clock, int colorProgress, CircleProgressCallback mCallback) {
        super(context);
        this.context = context;
        this.style = clock.getValue();
        this.colorProgress = colorProgress;
        this.mCallback = mCallback;
//        this.setOnClickListener(clickListener);
        setPaint();
    }

    private void getAttr(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.CircleProgressView, 0, 0);
        colorProgress = a.getColor(R.styleable.CircleProgressView_colorProgress,colorProgress);
//        colorDone = a.getColor(R.styleable.CircleProgressView_colorDone, colorDone);
        colorDone = 0x00000000;
        colorWrong = a.getColor(R.styleable.CircleProgressView_colorWrong,colorWrong);
//        colorBaseFill = a.getColor(R.styleable.CircleProgressView_colorBaseFill,colorBaseFill);
        colorBaseFill = 0x00000000;
        colorBaseStroke = a.getColor(R.styleable.CircleProgressView_colorBaseStroke, colorBaseStroke);
        colorWrongBaseStroke = a.getColor(R.styleable.CircleProgressView_colorWrongStroke, colorWrongBaseStroke);
        strokeWidth = a.getInt(R.styleable.CircleProgressView_strokeWidth, strokeWidth);
        progressPeriodMills = a.getInt(R.styleable.CircleProgressView_progressPeriodMills, progressPeriodMills);
        startProgress = a.getInt(R.styleable.CircleProgressView_startProgressPosition, startProgress);
        topProgress = a.getInt(R.styleable.CircleProgressView_fullProgressPosition, topProgress);
        style = a.getInt(R.styleable.CircleProgressView_style, style);
        init = a.getBoolean(R.styleable.CircleProgressView_shouldInit, init);
        error = a.getBoolean(R.styleable.CircleProgressView_isInError, error);
        duration = a.getInt(R.styleable.CircleProgressView_duration, duration);

        int attrPrgs = a.getInt(R.styleable.CircleProgressView_progress, -1);
        if(attrPrgs!=-1){
            progress = (int)((attrPrgs/100f)*topProgress);
        }

        /*-------------------*/
        progressPeriodMills = duration*1000/360;
        /*-------------------*/
        a.recycle();
    }

    private void setPaint() {
        paint = new Paint();
//        paint.setColor(colorProgress);
        paint.setStrokeWidth(strokeWidth);
        paintDone = new Paint();
        paintDone.setColor(colorDone);
        paintDone.setStyle(Paint.Style.FILL);
//        paintDone.setStrokeWidth(strokeWidth);
        paintWrong = new Paint();
        paintWrong.setColor(colorWrong);
        paintWrong.setStyle(Paint.Style.FILL);
//        paintWrong.setStrokeWidth(strokeWidth);
        paintBaseFill = new Paint();
        paintBaseFill.setColor(colorBaseFill);
//        paintBaseFill.setStrokeWidth(strokeWidth);
        paintBaseFill.setStyle(Paint.Style.FILL);
        paintBaseStroke = new Paint();
        paintBaseStroke.setColor(colorBaseStroke);
        paintBaseStroke.setStrokeWidth(strokeWidth);
        paintBaseStroke.setStyle(Paint.Style.STROKE);

    }

    public void onProgress(int progress1) {
        progress = progress1;
        this.invalidate();
        if (mCallback != null)
            mCallback.onProgress((int) ((progress * 100) / topProgress));
    }

    public void simulateProgress(final int to) {
        if (to == 0) {
            init = true;
            invalidate();
        } else {
            timer = new Timer();
            progress = 0;
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (progress < to) {
                        progress += 4;
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                onProgress(progress);
                            }
                        });

                    } else {
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                onSimulateTimerFinished();
                            }
                        });
                    }

                }
            }, 0, progressPeriodMills);
        }
    }

    public void onError() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        error = true;
        progress = 0;
        invalidate();
        if (mCallback != null)
            mCallback.onError(0);
    }

    public void onSimulateTimerFinished() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (mCallback != null)
            mCallback.onFinish(100);
    }

    public void onTimerFinished() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (mCallback != null)
            mCallback.onFinish((int) ((progress * 100) / topProgress));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float width = (float) getWidth();
        float height = (float) getHeight();
        if (width > height) {
            radius = height / 2.2f;
        } else {
            radius = width / 2.2f;
        }
        center_x = width / 2;
        center_y = height / 2;
        oval.set(center_x - radius, center_y - radius, center_x + radius, center_y + radius);

        if (init) {
            // before any progress
            canvas.drawArc(oval, -90, 360, false, paintBaseFill);
            canvas.drawArc(oval, -90, 360, false, paintBaseStroke);
            init = false;
        } else {
            if (error) {
                // on Error
                canvas.drawArc(oval, -90, 360, false, paintWrong);
                paintBaseStroke.setColor(colorWrongBaseStroke);
                canvas.drawArc(oval, -90, 360, false, paintBaseStroke);
                paintBaseStroke.setColor(colorBaseStroke);
            } else if (progress >= 360) {
                // it is done
                canvas.drawArc(oval, -90, 360, false, paintDone);
                paintBaseStroke.setColor(Color.WHITE);
                canvas.drawArc(oval, -90, 360, false, paintBaseStroke);
                paintBaseStroke.setColor(colorBaseStroke);
            } else {
                // in progress
                boolean useCenter = true;
                switch (style) {
                    case 0:
                        paint.setStyle(Paint.Style.FILL_AND_STROKE);
                        break;
                    case 1:
                        paint.setStyle(Paint.Style.STROKE);
                        break;
                    case 2:
//                        paint.setStyle(Paint.Style.FILL_AND_STROKE);
                        paint.setStyle(Paint.Style.STROKE);
                        useCenter = false;
                        break;

                    default:
                        paint.setStyle(Paint.Style.FILL_AND_STROKE);
                        break;
                }

                Shader shader = new SweepGradient(dpToPx(30), dpToPx(30), new int[]{0xFF000000, 0xFFFF0000, 0xFFEEB223}, null);
                final Matrix matrix = new Matrix();
                matrix.setRotate(-90, dpToPx(30), dpToPx(30));
                shader.setLocalMatrix(matrix);
                paint.setShader(shader);

                canvas.drawArc(oval, -90, 360, false, paintBaseFill);
                canvas.drawArc(oval, -90, 360, false, paintBaseStroke);
                canvas.drawArc(oval, startProgress, progress, useCenter, paint);
            }
        }
    }


//    OnClickListener clickListener = new OnClickListener() {
//        @Override
//        public void onClick(View v) {
//
//        }
//    };

    public void start() {
        isStarted = true;
        timer = new Timer();
        myTimerTask = new MyTimerTask();
        progress = 0;
        timer.schedule(myTimerTask, 0, progressPeriodMills);
    }

    public void stop() {
        if (timer != null) {
            isStarted = false;
            timer.cancel();
        }
    }

    public boolean isStarted() {
        return isStarted;
    }

    public void setSizeInDp(int dpH, int dpW) {
        LayoutParams params = new LayoutParams(dpToPx(dpW), dpToPx(dpH));
        setLayoutParams(params);
    }

    public int getStartProgress() {
        return startProgress;
    }

    public void setStartProgress(int startProgress) {
        this.startProgress = startProgress;
    }

    public int getTopProgress() {
        return topProgress;
    }

    public void setTopProgress(int topProgress) {
        this.topProgress = topProgress;
    }

    public int getProgress() {
        return progress;
    }

    public int getStyle() {
        return style;
    }

    public void setStyle(int style) {
        this.style = style;
    }

    public int getColorProgress() {
        return colorProgress;
    }

    public void setColorProgress(int colorProgress) {
        this.colorProgress = colorProgress;
        setPaint();
    }

    public int getColorDone() {
        return colorDone;
    }

    public void setColorDone(int colorDone) {
        this.colorDone = colorDone;
        setPaint();
    }

    public int getColorWrong() {
        return colorWrong;
    }

    public void setColorWrong(int colorWrong) {
        this.colorWrong = colorWrong;
        setPaint();
    }

    public int getColorBaseFill() {
        return colorBaseFill;
    }

    public void setColorBaseFill(int colorBaseFill) {
        this.colorBaseFill = colorBaseFill;
        setPaint();
    }

    public int getColorBaseStroke() {
        return colorBaseStroke;
    }

    public void setColorBaseStroke(int colorBaseStroke) {
        this.colorBaseStroke = colorBaseStroke;
        setPaint();
    }

    public int getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
        setPaint();
    }

    public CircleProgressCallback getmCallback() {
        return mCallback;
    }

    public void setmCallback(CircleProgressCallback mCallback) {
        this.mCallback = mCallback;
    }

    public int getProgressPeriodMills() {
        return progressPeriodMills;
    }

    public void setProgressPeriodMills(int progressPeriodMills) {
        this.progressPeriodMills = progressPeriodMills;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getColorWrongBaseStroke() {
        return colorWrongBaseStroke;
    }

    public void setColorWrongBaseStroke(int colorWrongBaseStroke) {
        this.colorWrongBaseStroke = colorWrongBaseStroke;
    }

    public boolean isError() {
        return error;
    }

    public boolean isInit() {
        return init;
    }

    public void setInit(boolean init) {
        this.init = init;
    }

    @Override
    public Parcelable onSaveInstanceState() {

        Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState.CircleProgressView", super.onSaveInstanceState());
        bundle.putInt("CircleProgressView.progress", this.progress);
        bundle.putBoolean("CircleProgressView.error", this.error);
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {

        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            this.progress = bundle.getInt("CircleProgressView.progress");
            this.error = bundle.getBoolean("CircleProgressView.error");
            init = false;
            state = bundle.getParcelable("instanceState.CircleProgressView");
        }
        super.onRestoreInstanceState(state);
    }

    public void savedInstanceState(int progress, boolean error) {
        init = false;
        this.progress = progress;
        this.error = error;
    }


    private int dpToPx(int dp) {
//		DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
//		int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
//		return px;
        return (int) (dp * getContext().getResources().getDisplayMetrics().density + 0.5f);
    }

    public enum CircleProgressViewStyles {
        EMPTY_CIRCLE(2), CLOCK(1), FILLED_CIRCLE(0);
        private int value;

        private CircleProgressViewStyles(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }

    public interface CircleProgressCallback {
        public void onError(int progress1);

        public void onFinish(int progress1);

        public void onProgress(int progress1);
    }

    class MyTimerTask extends TimerTask {

        @Override
        public void run() {
//            Log.d("Progress", String.valueOf(progress));

            if (progress < 360) {
                progress += 1;
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onProgress(progress);
                    }
                });

            } else {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onTimerFinished();
                    }
                });
            }
        }
    }

}
