package com.example;

/**
 * 基础动画类。。
 */
public abstract class PxAnim {
    protected PxScene scene;
    protected boolean finished = false; // Field to track animation state

    public void setScene(PxScene scene) {
        this.scene = scene;
    }

    public PxScene getScene() {
        return scene;
    }

    /** * 每帧调用 (Called every frame).
     * Concrete implementations should call {@link #markAsFinished()} when the animation completes.
     */
    public abstract void update();

    /**
     * Checks if the animation has completed.
     * @return true if the animation is finished, false otherwise.
     */
    public boolean isFinished() {
        return finished;
    }

    /**
     * Marks this animation as finished.
     * Should be called by concrete animation classes within their update() logic
     * once the animation's criteria for completion are met.
     */
    protected void markAsFinished() {
        this.finished = true;
    }
}