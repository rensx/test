package com.example;

public abstract class PxAnim {
    protected PxScene scene;
    protected boolean finished = false;

    public void setScene(PxScene scene) {
        this.scene = scene;
    }

    public PxScene getScene() {
        return scene;
    }

    public abstract void update();

    public boolean isFinished() {
        return finished;
    }

    protected void markAsFinished() {
        this.finished = true;
    }
}