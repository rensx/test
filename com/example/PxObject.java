package com.example;

import processing.core.PGraphics;

public abstract class PxObject {
    public void addToSceneHook(PxScene scene) {}
    public void removedFromSceneHook(PxScene scene) {}
    public boolean isVisible() { return true; }
    public abstract void draw(PGraphics g);
    public void update() {}
}