package com.example;

import processing.core.PGraphics;

public class PxRect extends PxObject {
    private float width, height;
    
    public PxRect(float width, float height) {
        super();
        this.width = width;
        this.height = height;
    }
    
    @Override
    public void draw(PGraphics pg) {
        pg.pushMatrix();
        pg.rectMode(pg.CENTER);
        pg.rect(0, 0, width, height);
        pg.popMatrix();
    }
    

    @Override
    public void update() {
        // Implementation if needed
    }
}