package jp.ac.it_college.std.nakasone.opencampas.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class SensorView extends View {
    public static final float MAGNIFICATION = 40.0f;
    public static final float OUTER_RADIUS = 10.0f;
    public static final float RADIUS = 10.0f;
    public static final float STROKE_WIDTH = 3.0f;
    private Paint paint;
    private float x;
    private float y;

    public SensorView(Context context) {
        super(context);
        paint = new Paint();
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.translate(getWidth() / 2, getHeight() / 2);
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(x * MAGNIFICATION, y * MAGNIFICATION, RADIUS, paint);

        paint.setColor(Color.LTGRAY);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(STROKE_WIDTH);
        canvas.drawCircle(0, 0, OUTER_RADIUS * MAGNIFICATION, paint);
        canvas.drawLine(-10, 0, 10, 0, paint);
        canvas.drawLine(0, -10, 0, 10, paint);
    }
}
