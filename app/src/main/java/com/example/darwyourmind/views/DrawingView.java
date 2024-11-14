package com.example.darwyourmind.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class DrawingView extends View {

    private Paint paint;
    private Path path;

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        path = new Path();

        // 기본 그리기 설정
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(8f);
        paint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(path, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(x, y);
                return true;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(x, y);
                break;
            case MotionEvent.ACTION_UP:
                // 그림을 마칠 때도 추가 로직이 필요할 경우 처리
                break;
            default:
                return false;
        }
        // 화면을 갱신하여 그림이 즉시 표시되도록 함
        invalidate();
        return true;
    }

    public void clearDrawing() {
        path.reset();
        invalidate(); // 화면 갱신
    }

    // Method to create a bitmap from the DrawingView content
    public Bitmap getBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        draw(canvas); // Draw the view’s content onto the canvas
        return bitmap;
    }

}

