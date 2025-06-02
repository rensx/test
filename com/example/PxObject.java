package com.example;

import processing.core.PGraphics;

public abstract class PxObject {
    protected PxScene scene; // Set when added to the scene
    protected boolean visible = true;

    public void addToSceneHook(PxScene scene) {
        this.scene = scene;
    }

    public void removedFromSceneHook(PxScene scene) {
        this.scene = null;
    }

    public abstract void draw(PGraphics pg);

    public abstract void update();

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public abstract float getBoundingSize();
}