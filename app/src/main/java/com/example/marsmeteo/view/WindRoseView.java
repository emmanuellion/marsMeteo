package com.example.marsmeteo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class WindRoseView extends View {
    private Paint circlePaint;
    private Paint trianglePaint;
    private Paint gridPaint;
    private Map<String, Integer> windCounts;
    private int maxCount;

    public WindRoseView(Context context) {
        super(context);
        init();
    }

    public WindRoseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        circlePaint = new Paint();
        circlePaint.setColor(Color.GRAY);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(2);
        circlePaint.setAntiAlias(true);

        trianglePaint = new Paint();
        trianglePaint.setColor(Color.rgb(135, 206, 235));
        trianglePaint.setStyle(Paint.Style.FILL);
        trianglePaint.setAntiAlias(true);

        gridPaint = new Paint();
        gridPaint.setColor(Color.DKGRAY);
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setStrokeWidth(1);
        gridPaint.setAntiAlias(true);

        windCounts = new HashMap<>();
    }

    public void setWindData(JSONObject windData) {
        try {
            windCounts.clear();
            maxCount = 0;

            for (int i = 0; i < 16; i++) {
                String key = String.valueOf(i);
                if (windData.has(key)) {
                    JSONObject direction = windData.getJSONObject(key);
                    int count = direction.getInt("ct");
                    windCounts.put(key, count);
                    maxCount = Math.max(maxCount, count);
                }
            }

            invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        int centerX = width / 2;
        int centerY = height / 2;
        int radius = Math.min(width, height) / 2 - 20;

        for (int i = 1; i <= 4; i++) {
            float currentRadius = radius * i / 4f;
            canvas.drawCircle(centerX, centerY, currentRadius, gridPaint);
        }

        canvas.drawLine(centerX, centerY - radius, centerX, centerY + radius, gridPaint);
        canvas.drawLine(centerX - radius, centerY, centerX + radius, centerY, gridPaint);

        double diagonal = radius * Math.cos(Math.PI / 4);
        canvas.drawLine(
            (float)(centerX - diagonal), (float)(centerY - diagonal),
            (float)(centerX + diagonal), (float)(centerY + diagonal),
            gridPaint
        );
        canvas.drawLine(
            (float)(centerX - diagonal), (float)(centerY + diagonal),
            (float)(centerX + diagonal), (float)(centerY - diagonal),
            gridPaint
        );

        canvas.drawCircle(centerX, centerY, radius, circlePaint);

        for (int i = 0; i < 16; i++) {
            String key = String.valueOf(i);
            if (windCounts.containsKey(key)) {
                int count = windCounts.get(key);
                float percentage = count / (float) maxCount;
                float angle = (float) (i * 22.5 * Math.PI / 180);

                float triangleHeight = radius * percentage;
                float triangleWidth = (float) (triangleHeight * Math.tan(Math.PI / 16));

                canvas.save();

                canvas.translate(centerX, centerY);
                canvas.rotate(i * 22.5f);

                Path path = new Path();
                path.moveTo(0, 0);
                path.lineTo(-triangleWidth, -triangleHeight);
                path.lineTo(triangleWidth, -triangleHeight);
                path.close();
                canvas.drawPath(path, trianglePaint);

                canvas.restore();
            }
        }
    }
} 