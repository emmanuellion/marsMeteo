package com.example.marsmeteo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;
import java.util.Map;

public class WindRoseView extends View {
    private Paint circlePaint;
    private Paint trianglePaint;
    private Paint linePaint;
    private float maxValue = 0f;
    private Map<Integer, Float> windDirections;

    public WindRoseView(Context context) {
        super(context);
        init();
    }

    public WindRoseView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        circlePaint = new Paint();
        circlePaint.setColor(Color.GRAY);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(2f);
        circlePaint.setAntiAlias(true);

        trianglePaint = new Paint();
        trianglePaint.setColor(Color.parseColor("#64B5F6")); // Bleu clair
        trianglePaint.setStyle(Paint.Style.FILL);
        trianglePaint.setAntiAlias(true);

        linePaint = new Paint();
        linePaint.setColor(Color.GRAY);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(1f);
        linePaint.setAntiAlias(true);
    }

    public void setWindData(Map<Integer, Float> windDirections) {
        this.windDirections = windDirections;
        maxValue = 0f;
        for (Float value : windDirections.values()) {
            if (value > maxValue) maxValue = value;
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (windDirections == null) return;

        int width = getWidth();
        int height = getHeight();
        float centerX = width / 2f;
        float centerY = height / 2f;
        float radius = Math.min(width, height) / 2f - 20f;

        // Dessiner le cercle extérieur
        canvas.drawCircle(centerX, centerY, radius, circlePaint);

        // Dessiner les lignes de division (N, S, E, O)
        canvas.drawLine(centerX, centerY - radius, centerX, centerY + radius, linePaint);
        canvas.drawLine(centerX - radius, centerY, centerX + radius, centerY, linePaint);

        // Dessiner les triangles pour chaque direction
        for (Map.Entry<Integer, Float> entry : windDirections.entrySet()) {
            int direction = entry.getKey();
            float value = entry.getValue();
            float angle = direction * 22.5f; // 360° / 16 directions = 22.5°
            float length = (value / maxValue) * radius;

            drawTriangle(canvas, centerX, centerY, angle, length);
        }
    }

    private void drawTriangle(Canvas canvas, float centerX, float centerY, float angle, float length) {
        float angleRad = (float) Math.toRadians(angle - 90); // -90 pour aligner avec le Nord
        float baseWidth = length * 0.2f; // Largeur de la base du triangle

        Path path = new Path();
        path.moveTo(centerX, centerY);

        // Calculer les points du triangle
        float tipX = centerX + (float) (length * Math.cos(angleRad));
        float tipY = centerY + (float) (length * Math.sin(angleRad));
        
        float perpAngle = angleRad + (float) Math.PI/2;
        float leftX = centerX + (float) (baseWidth * Math.cos(perpAngle));
        float leftY = centerY + (float) (baseWidth * Math.sin(perpAngle));
        float rightX = centerX - (float) (baseWidth * Math.cos(perpAngle));
        float rightY = centerY - (float) (baseWidth * Math.sin(perpAngle));

        // Dessiner le triangle
        path.moveTo(leftX, leftY);
        path.lineTo(tipX, tipY);
        path.lineTo(rightX, rightY);
        path.close();

        canvas.drawPath(path, trianglePaint);
    }
} 