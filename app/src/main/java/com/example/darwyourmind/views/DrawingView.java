package com.example.darwyourmind.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class DrawingView extends View {

    private Paint paint;
    private Path path;
    private List<PathWithColor> paths;
    private int currentColor = Color.BLACK; // Default color
    private boolean isEraserOn = false; // Flag for eraser mode
    private Bitmap tempBitmap; // Temporary bitmap for drawing
    private Canvas tempCanvas; // Canvas to draw on tempBitmap

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        path = new Path();
        paths = new ArrayList<>();

        // 기본 그리기 설정
        paint.setColor(currentColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(8f);
        paint.setAntiAlias(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // Create a bitmap and canvas for drawing
        tempBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        tempCanvas = new Canvas(tempBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Draw paths onto the temporary canvas
        for (PathWithColor p : paths) {
            paint.setColor(p.color);
            paint.setXfermode(p.isEraser ? new PorterDuffXfermode(PorterDuff.Mode.CLEAR) : null);
            tempCanvas.drawPath(p.path, paint);
        }

        // Draw the current path
        paint.setColor(currentColor);
        paint.setXfermode(isEraserOn ? new PorterDuffXfermode(PorterDuff.Mode.CLEAR) : null);
        tempCanvas.drawPath(path, paint);

        // Draw the temporary bitmap onto the actual canvas
        canvas.drawBitmap(tempBitmap, 0, 0, null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path = new Path(); // 새로운 path 생성
                path.moveTo(x, y);
                return true;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(x, y);
                break;
            case MotionEvent.ACTION_UP:
                // 경로 저장 (지우기 여부 포함)
                paths.add(new PathWithColor(new Path(path), currentColor, isEraserOn));
                path.reset();
                break;
            default:
                return false;
        }
        invalidate(); // 화면 갱신
        return true;
    }

    // 색상 변경 기능
    public void setColor(int color) {
        currentColor = color;
        isEraserOn = false; // 색상 변경 시 지우기 모드 해제
    }

    // 지우기 모드
    public void enableEraser() {
        isEraserOn = true;
    }

    // 그림 전체 지우기 기능
    public void clearDrawing() {
        path.reset();
        paths.clear();
        // Clear the temporary bitmap
        tempBitmap.eraseColor(Color.TRANSPARENT);
        invalidate();
    }

    // Method to create a bitmap from the DrawingView content
    public Bitmap getBitmap() {
        return tempBitmap; // Return the temporary bitmap
    }

    // 클래스 생성: Path와 Color를 함께 저장하며, 지우기 여부도 포함
    private static class PathWithColor {
        Path path;
        int color;
        boolean isEraser;

        PathWithColor(Path path, int color, boolean isEraser) {
            this.path = path;
            this.color = color;
            this.isEraser = isEraser;
        }
    }
}