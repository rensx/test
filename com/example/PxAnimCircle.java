package com.example;

import processing.core.PGraphics;

public class PxAnimCircle extends PxObject {
    private float x, y, radius;
    private int color = 0xff000000;
    private float vx = 2, vy = 1.5f;

    public PxAnimCircle(float x, float y, float radius) {
        this.x = x; this.y = y; this.radius = radius;
    }

    public PxAnimCircle setColor(int c) { this.color = c; return this; }
    public PxAnimCircle setVelocity(float vx, float vy) { this.vx = vx; this.vy = vy; return this; }

    @Override
    public void update() {
        x += vx;
        y += vy;
        if (x - radius < 0 || x + radius > 800) vx = -vx;
        if (y - radius < 0 || y + radius > 600) vy = -vy;
    }

    @Override
    public void draw(PGraphics g) {
        //if (!visible) return;
        g.pushMatrix();
        try {
            g.translate(x, y);
            g.fill(color);
            g.noStroke();
            g.ellipse(0, 0, 2 * radius, 2 * radius);
        } finally {
            g.popMatrix();
        }
    }
}