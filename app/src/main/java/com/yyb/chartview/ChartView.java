package com.yyb.chartview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;

import static android.view.View.MeasureSpec.AT_MOST;

public class ChartView extends View {
    //不允许用户修改，想要修改宽高，使用mWidth、mBaseHeight。
    protected final float DEF_WIDTH = 650;
    protected final float DEF_HIGHT = 400;

    //测量的控件宽高，会在onMeasure中进行测量。
    protected int mBaseWidth;
    protected int mBaseHeight;
    private Paint mInnerXPaint;
    private Paint mLoadingPaint;
    private Paint mXYPaint;
    private Paint mLinePaint;
    private Paint dotPaint;
    //上下左右padding,允许修改
    //x、y轴指示文字字体的大小
    protected float mXYTextSize = 25;
    //左侧文字距离左边线线的距离
    protected float mLeftTxtPadding = 64;
    //底部文字距离底部线的距离
    protected float mBottomTxtPadding = 40;

    protected float mBasePaddingTop = 200;
    protected float mBasePaddingBottom = 200;
    protected float mBasePaddingLeft = 150;
    protected float mBasePaddingRight = 50;
    private ArrayList<Point> mPoints;
    private float mPerX;
    private float mPerY;

    public ChartView(Context context) {
        this(context, null);
    }

    public ChartView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthSpecMode == AT_MOST && heightSpecMode == AT_MOST) {
            setMeasuredDimension((int) DEF_WIDTH, (int) DEF_HIGHT);
        } else if (widthSpecMode == AT_MOST) {
            setMeasuredDimension((int) DEF_WIDTH, heightSpecSize);
        } else if (heightSpecMode == AT_MOST) {
            setMeasuredDimension(widthSpecSize, (int) DEF_HIGHT);
        } else {
            setMeasuredDimension(widthSpecSize, heightSpecSize);
        }
        mBaseWidth = getMeasuredWidth();
        mBaseHeight = getMeasuredHeight();
    }

    /**
     * 初始化各种画笔设置
     */
    private void init() {
        //初始化进度显示
        mLoadingPaint = new Paint();
        mLoadingPaint.setColor(getContext().getResources().getColor(R.color.colorAccent));
        mLoadingPaint.setTextSize(14);
        mLoadingPaint.setAntiAlias(true);
        //平行于x轴的
        mInnerXPaint = new Paint();
        mInnerXPaint.setColor(Color.LTGRAY);
        mInnerXPaint.setStrokeWidth(1);
        mInnerXPaint.setStyle(Paint.Style.STROKE);
        mInnerXPaint.setAntiAlias(true);
        //x轴y轴的实现
        mXYPaint = new Paint();
        mXYPaint.setColor(Color.GRAY);
        mXYPaint.setTextSize(25);
        mXYPaint.setAntiAlias(true);
        //折线
        mLinePaint = new Paint();
        mLinePaint.setColor(Color.RED);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setAntiAlias(true);
        mLinePaint.setTextSize(11);
        //买入卖出点
        dotPaint = new Paint();
        dotPaint.setColor(Color.BLUE);
        dotPaint.setStrokeWidth(1);
        dotPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //平行于X轴的横线
        drawInnerXPaint(canvas);
        //绘制xy轴的线
        drawXYPaint(canvas);
        //绘制折线图
        drawBrokenPaint(canvas);
        //绘制买入卖出点
        drawDot(canvas);
    }

    /**
     * 绘制买入卖出点
     *
     * @param canvas
     */
    private void drawDot(Canvas canvas) {
        //买入点假如是第五个点 卖出点假如是60个点
        float buyXDot = mBasePaddingLeft + (4 * mPerX);
        float buyYDot = (mBaseHeight - mBasePaddingTop) - (mPoints.get(4).y * mPerY);
        //先绘制买入点数据
        dotPaint.setStrokeWidth(Utils.dip2px(getContext(), 1));
        canvas.drawLine(buyXDot, buyYDot, buyXDot, buyYDot - Utils.dip2px(getContext(), 20), dotPaint);  //绘制红线
        dotPaint.setStrokeWidth(Utils.dip2px(getContext(), 4));
        canvas.drawCircle(buyXDot, buyYDot, Utils.dip2px(getContext(), 2), dotPaint); //绘制圆点
        dotPaint.setStrokeWidth(Utils.dip2px(getContext(), 0.5f));
        dotPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(buyXDot - Utils.dip2px(getContext(), 30), buyYDot - Utils.dip2px(getContext(), 26),
                buyXDot, buyYDot - Utils.dip2px(getContext(), 13), dotPaint);
        dotPaint.setTextSize(Utils.dip2px(getContext(), 11));
        dotPaint.setColor(Color.WHITE);
        canvas.drawText("买入", buyXDot - Utils.dip2px(getContext(), 30 / 2) - dotPaint.measureText("买入") / 2, buyYDot - Utils.dip2px(getContext(), 15), dotPaint);
        float saleXDot = mBasePaddingLeft + (20 * mPerX);
        float saleYDot = (mBaseHeight - mBasePaddingTop) - (mPoints.get(20).y * mPerY);
        //先绘制卖出点数据
        dotPaint.setColor(Color.GREEN);
        dotPaint.setStrokeWidth(Utils.dip2px(getContext(), 1));
        canvas.drawLine(saleXDot, saleYDot, saleXDot, saleYDot - Utils.dip2px(getContext(), 20), dotPaint);  //绘制红线
        dotPaint.setStrokeWidth(Utils.dip2px(getContext(), 4));
        canvas.drawCircle(saleXDot, saleYDot, Utils.dip2px(getContext(), 2), dotPaint); //绘制圆点
        dotPaint.setStrokeWidth(Utils.dip2px(getContext(), 0.5f));
        dotPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(saleXDot - Utils.dip2px(getContext(), 30), saleYDot - Utils.dip2px(getContext(), 26),
                saleXDot, saleYDot - Utils.dip2px(getContext(), 13), dotPaint);
        dotPaint.setTextSize(Utils.dip2px(getContext(), 11));
        dotPaint.setColor(Color.WHITE);
        canvas.drawText("卖出", saleXDot - Utils.dip2px(getContext(), 30 / 2) - dotPaint.measureText("卖出") / 2, saleYDot - Utils.dip2px(getContext(), 15), dotPaint);

    }
        private void drawInnerXPaint (Canvas canvas){
            //画5条横轴的虚线
            //首先确定最大值和最小值的位置
            float perHight = (mBaseHeight - mBasePaddingBottom - mBasePaddingTop) / 4;
            //绘制基准线
            canvas.drawLine(0 + mBasePaddingLeft, mBasePaddingTop,
                    mBaseWidth - mBasePaddingRight, mBasePaddingTop, mInnerXPaint);//最上面的那一条

            canvas.drawLine(0 + mBasePaddingLeft, mBasePaddingTop + perHight * 1,
                    mBaseWidth - mBasePaddingRight, mBasePaddingTop + perHight * 1, mInnerXPaint);//2

            canvas.drawLine(0 + mBasePaddingLeft, mBasePaddingTop + perHight * 2,
                    mBaseWidth - mBasePaddingRight, mBasePaddingTop + perHight * 2, mInnerXPaint);//3

            canvas.drawLine(0 + mBasePaddingLeft, mBasePaddingTop + perHight * 3,
                    mBaseWidth - mBasePaddingRight, mBasePaddingTop + perHight * 3, mInnerXPaint);//4

            canvas.drawLine(0 + mBasePaddingLeft, mBaseHeight - mBasePaddingBottom,
                    mBaseWidth - mBasePaddingRight, mBaseHeight - mBasePaddingBottom, mInnerXPaint);//最下面的那一条

        }

        private void drawXYPaint (Canvas canvas){
            //先处理y轴方向文字
            //现将最小值、最大值画好
            Paint.FontMetrics fontMetrics = mXYPaint.getFontMetrics();
            float height = fontMetrics.descent - fontMetrics.ascent;
            //draw min
            float txtWigth = Utils.dip2px(getContext(), 10) + mLeftTxtPadding;

            //因为横线是均分的，所以只要取到最大值最小值的差值，均分即可。
            float perYWidth = (mBaseHeight - mBasePaddingBottom - mBasePaddingTop) / 4;
            //从下到上依次画
            for (int i = 0; i < 5; i++) {
                canvas.drawText((20 * i) + "",
                        mBasePaddingLeft - txtWigth,
                        mBaseHeight - mBasePaddingBottom - perYWidth * i + height / 2, mXYPaint);
            }

            //处理x轴方向文字
            //x轴文字的高度
            float hight = mBaseHeight - mBasePaddingBottom + mBottomTxtPadding;

            //绘制X轴文字
            float space = (mBaseWidth - mBasePaddingLeft - mBasePaddingRight - mXYPaint.measureText("2020-02-02") * 2 - mXYPaint.measureText("2020-02-02") / 2) / 2;  //刨去文字剩余的空间
            canvas.drawText("2020-02-02", mBasePaddingLeft - mXYPaint.measureText("2020-02-02") / 2, hight, mXYPaint);
            canvas.drawText("2020-02-04", mBasePaddingLeft + space + mXYPaint.measureText("2020-02-02") / 2, hight, mXYPaint);
            canvas.drawText("2020-02-06", mBaseWidth - mBasePaddingRight - mXYPaint.measureText("2020-02-02"), hight, mXYPaint);
        }

        private void drawBrokenPaint (Canvas canvas){
            mPerX = (mBaseWidth - mBasePaddingLeft - mBasePaddingRight) / mPoints.size();
            mPerY = ((mBaseHeight - mBasePaddingTop - mBasePaddingBottom) / 80);
            Path path = new Path();
            path.moveTo(mBasePaddingLeft, (mBaseHeight - mBasePaddingTop) - (mPoints.get(0).y * mPerY));
            for (int i = 1; i < mPoints.size(); i++) {
                path.lineTo(mBasePaddingLeft + (i * mPerX), (mBaseHeight - mBasePaddingTop) - (mPoints.get(i).y * mPerY));
            }
            canvas.drawPath(path, mLinePaint);

        }

        public void setData (ArrayList < Point > points) {
            this.mPoints = points;
            invalidate();
        }
    }
