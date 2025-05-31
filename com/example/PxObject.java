package com.example;

import processing.core.PGraphics;
import processing.core.PVector;

public abstract class PxObject {
    protected final PVector position = new PVector();
    protected float rotation = 0;
    protected float scale = 1;
    protected float opacity = 1;
    protected boolean visible = true;
    
    protected abstract void draw(PGraphics pg);
    
    protected void addToSceneHook(PxScene scene) {}
    protected void removedFromSceneHook(PxScene scene) {}
    
    // Simple setters that can be chained
    public PxObject at(float x, float y) {
        position.set(x, y);
        return this;
    }
    
    public PxObject rotate(float angle) {
        rotation = angle;
        return this;
    }
    
    public PxObject scale(float s) {
        scale = s;
        return this;
    }
    
    public PxObject opacity(float o) {
        opacity = Math.max(0, Math.min(1, o));
        return this;
    }
    
    public PxObject visible(boolean v) {
        visible = v;
        return this;
    }
    
    public boolean isVisible() {
        return visible;
    }
    public void update(){}
}