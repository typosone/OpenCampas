package jp.ac.it_college.std.nakasone.opencampas.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import org.jetbrains.annotations.NotNull;

import jp.ac.it_college.std.nakasone.opencampas.R;

public class BallCarrySurfaceView extends SurfaceView
        implements SurfaceHolder.Callback, Runnable {
    private static final int GOAL_HEIGHT = 150;
    private static final int START_HEIGHT = 150;
    private static final int OUT_WIDTH = 100;

    private static final float BALL_SIZE = 20.0f;

    private float[] mDifficulty = {11.0f, 6.5f, 15.5f}; // ゲームの難易度 [落とし穴の移動スピード]
    private float mCircleMagnification = 2.75f;  // ゲームの難易度 [プレイヤーの移動スピード]

    private int mCircleX = 0;
    private int mCircleY = 0;
    private int mWidth;
    private int mHeight;
    private int mHoleRadius;
    private int mHoleCoordinateRangeX;
    private float accelerationX = 0.0f;
    private float accelerationY = 0.0f;
    private boolean mIsGoal = false;
    private boolean mIsGone = false;
    private boolean mIsAttached;
    private Thread mThread;
    private Paint mPaint;
    private Path mGoalZone;
    private Path mStartZone;
    private Path mOutZoneL;
    private Path mOutZoneR;
    private Region mRegionGoalZone;
    private Region mRegionStartZone;
    private Region mRegionOutZoneL;
    private Region mRegionOutZoneR;

    private float[] mHoleX = new float[3];
    private float[] mHoleY = new float[3];
    private Path[] mHoleZone = new Path[3];
    private Region[] mRegionHoleZone = new Region[3];

    private Region mRegionWholeScreen;

    private long mStartTime;

    public BallCarrySurfaceView(Context context) {
        super(context);
        getHolder().addCallback(this);
    }

    private void zoneDecide() {
        mRegionWholeScreen = new Region(0, 0, mWidth, mHeight);
        mGoalZone = new Path();
        mGoalZone.addRect(OUT_WIDTH, 0, mWidth - OUT_WIDTH, GOAL_HEIGHT, Path.Direction.CW);
        mRegionGoalZone = new Region();
        mRegionGoalZone.setPath(mGoalZone, mRegionWholeScreen);

        mStartZone = new Path();
        mStartZone.addRect(OUT_WIDTH, mHeight - START_HEIGHT,
                mWidth - OUT_WIDTH, mHeight, Path.Direction.CW);
        mRegionStartZone = new Region();
        mRegionStartZone.setPath(mStartZone, mRegionWholeScreen);

        mOutZoneL = new Path();
        mOutZoneL.addRect(0, 0, OUT_WIDTH, mHeight, Path.Direction.CW);
        mRegionOutZoneL = new Region();
        mRegionOutZoneL.setPath(mOutZoneL, mRegionWholeScreen);

        mOutZoneR = new Path();
        mOutZoneR.addRect(mWidth - OUT_WIDTH, 0, mWidth, mHeight, Path.Direction.CW);
        mRegionOutZoneR = new Region();
        mRegionOutZoneR.setPath(mOutZoneR, mRegionWholeScreen);
    }

    private void holeDecide() {
        float heightPart = (mHeight - GOAL_HEIGHT - START_HEIGHT) / 3;
        for (int i = 0; i < mHoleY.length; i++) {
            mHoleY[i] = GOAL_HEIGHT + heightPart / 2 + heightPart * i;
        }
        for (int i = 0; i < mHoleX.length; i++) {
            mHoleX[i] = OUT_WIDTH + mHoleRadius +
                    (int) (Math.random() * (mHoleCoordinateRangeX - mHoleRadius * 2));
        }
        for (int i = 0; i < mHoleZone.length; i++) {
            mHoleZone[i] = new Path();
            mHoleZone[i].addCircle(mHoleX[i], mHoleY[i], mHoleRadius, Path.Direction.CW);
            mRegionHoleZone[i] = new Region();
            mRegionHoleZone[i].setPath(mHoleZone[i], mRegionWholeScreen);
        }
    }

    public void drawGameBoard() {
        if (mIsGone || mIsGoal) {
            return;
        }
        int oldCircleY = mCircleY;
        mCircleX -= accelerationX * mCircleMagnification;
        mCircleY += accelerationY * mCircleMagnification;

        if (mCircleY > mHeight) {
            mCircleY = (int) (oldCircleY - BALL_SIZE);
        }
        int moveHole;
        // 0番(奥)の穴を動かす
        moveHole = 0;
        if ((mHoleX[moveHole] < OUT_WIDTH + mHoleRadius)
                || (mHoleX[moveHole] > mWidth - OUT_WIDTH - mHoleRadius)) {
            mDifficulty[moveHole] *= -1;
        }
        mHoleX[moveHole] += mDifficulty[moveHole];
        mHoleZone[moveHole] = new Path();
        mHoleZone[moveHole].addCircle(mHoleX[moveHole], mHoleY[moveHole],
                mHoleRadius, Path.Direction.CW);
        mRegionHoleZone[moveHole] = new Region();
        mRegionHoleZone[moveHole].setPath(mHoleZone[moveHole], mRegionWholeScreen);

        // 1番(真ん中)の穴を動かす

        // 2番(手前)の穴を動かす


        Canvas canvas = getHolder().lockCanvas();
        if (canvas == null) {
            return;
        }
        canvas.drawColor(Color.LTGRAY);

        mPaint.setColor(Color.RED);
        canvas.drawPath(mGoalZone, mPaint);
        mPaint.setColor(Color.CYAN);
        canvas.drawPath(mStartZone, mPaint);
        mPaint.setColor(Color.BLACK);
        canvas.drawPath(mOutZoneL, mPaint);
        canvas.drawPath(mOutZoneR, mPaint);

        mPaint.setTextSize(50);

        canvas.drawText(getResources().getString(R.string.goal),
                mWidth / 2 - 50, 100, mPaint);
        canvas.drawText(getResources().getString(R.string.start),
                mWidth / 2 - 50, mHeight - 50, mPaint);

        for (Path aMHoleZone : mHoleZone) {
            canvas.drawPath(aMHoleZone, mPaint);
        }
        if (mRegionOutZoneL.contains(mCircleX, mCircleY)) {
            mIsGone = true;
        }
        if (mRegionOutZoneR.contains(mCircleX, mCircleY)) {
            mIsGone = true;
        }
        for (Region aMRegionHoleZone : mRegionHoleZone) {
            if (aMRegionHoleZone.contains(mCircleX, mCircleY)) {
                mIsGone = true;
            }
        }
        if (mRegionGoalZone.contains(mCircleX, mCircleY)) {
            mIsGoal = true;
            String msg = goaled();
            mPaint.setColor(Color.WHITE);
            canvas.drawText(msg, OUT_WIDTH + 10, GOAL_HEIGHT - 100, mPaint);
        }
        if (!(mIsGone || mIsGoal)) {
            mPaint.setColor(Color.YELLOW);
            canvas.drawCircle(mCircleX, mCircleY, BALL_SIZE, mPaint);
        }
        getHolder().unlockCanvasAndPost(canvas);
    }

    private String goaled() {
        long mEndTime = System.currentTimeMillis();
        long elapsedTime = mEndTime - mStartTime;
        int secTime = (int) (elapsedTime / 10);
        return "Goal! " + (secTime / 100.0) + "秒";
    }

    private void newBall() {
        mCircleX = mWidth / 2;
        mCircleY = mHeight - START_HEIGHT / 2;
        mIsGoal = false;
        mIsGone = false;
        mStartTime = System.currentTimeMillis();
        holeDecide();
    }

    public void setAcceleration(float ax, float ay) {
        accelerationX = ax;
        accelerationY = ay;
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setAntiAlias(true);
        mWidth = getWidth();
        mHeight = getHeight();
        mHoleRadius = (int) (((mHeight - GOAL_HEIGHT - START_HEIGHT) / 3 / 2) * 0.75);
        mHoleCoordinateRangeX = mWidth - OUT_WIDTH * 2;
        zoneDecide();
        holeDecide();
        newBall();
        mIsAttached = true;
        mThread = new Thread(this);
        mThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        mIsAttached = false;
        if (mThread.isAlive()) {
            try {
                mThread.join();
            } catch (InterruptedException e) {
                Log.e("destroy", e.getMessage(), e);
            }
        }
    }

    @Override
    public void run() {
        while (mIsAttached) {
            drawGameBoard();
        }
    }

    @Override
    public boolean onTouchEvent(@NotNull MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mRegionStartZone.contains((int) event.getX(), (int) event.getY())) {
                    newBall();
                }
                break;
            default:
                break;
        }
        return true;
    }
}
