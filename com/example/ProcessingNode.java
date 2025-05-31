package com.example;

import javafx.animation.AnimationTimer;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Region;
// import processing.core.PGraphics; // Not directly used by ProcessingNode, PxScene likely handles it

/**
 * A JavaFX Region that hosts and renders a PxScene (a Processing-like scene).
 * It can operate in two primary modes based on the 'autoRender' constructor flag:
 * 1. Auto-rendering: Continuously updates and renders the PxScene using an AnimationTimer.
 * In this mode, both PxScene.update() and PxScene.renderTo() are called.
 * 2. Manual rendering: PxScene.renderTo() is called only when requestRender() is invoked.
 * In this mode, PxScene.update() is expected to be managed externally by the application logic.
 */
public class ProcessingNode extends Region {
    private final int initialWidth;
    private final int initialHeight;

    private final ImageView imageView;
    private WritableImage writableImage; // Can change if resizing is implemented
    private final PxScene pxScene;
    private AnimationTimer animationTimer;

    private final boolean autoRenderingConfigured; // True if configured for automatic rendering

    /**
     * Constructs a ProcessingNode.
     *
     * @param width The width of the rendering area.
     * @param height The height of the rendering area.
     * @param autoRender If true, an AnimationTimer will be started to continuously call
     * {@code pxScene.update()} and then {@code pxScene.renderTo(writableImage)}.
     * If false, rendering to the image only occurs upon calling {@link #requestRender()},
     * which itself only calls {@code pxScene.renderTo(writableImage)}.
     * In this manual mode, {@code pxScene.update()} should be handled by external application logic.
     */
    public ProcessingNode(int width, int height, boolean autoRender) {
        this.initialWidth = width;
        this.initialHeight = height;
        this.writableImage = new WritableImage(this.initialWidth, this.initialHeight);
        this.imageView = new ImageView(this.writableImage);

        this.pxScene = new PxScene(this.initialWidth, this.initialHeight);

        getChildren().add(imageView);
        setPrefSize(this.initialWidth, this.initialHeight);
        // For a strictly fixed size, you could also uncomment these:
        // setMinWidth(this.initialWidth); setMaxWidth(this.initialWidth);
        // setMinHeight(this.initialHeight); setMaxHeight(this.initialHeight);

        this.autoRenderingConfigured = autoRender;

        if (this.autoRenderingConfigured) {
            startAnimationLoopInternal();
        }
    }

    /**
     * Constructs a ProcessingNode with auto-rendering enabled by default.
     *
     * @param width The width of the rendering area.
     * @param height The height of the rendering area.
     */
    public ProcessingNode(int width, int height) {
        this(width, height, true); // Default to auto-rendering
    }

    private void startAnimationLoopInternal() {
        if (animationTimer != null) {
            animationTimer.stop();
        }
        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                pxScene.update(); // Update scene logic (physics, animations, etc.)
                pxScene.renderTo(writableImage); // Render the current state of PxScene
            }
        };
        animationTimer.start();
    }

    /**
     * Stops the automatic rendering loop if it was active and configured.
     * Does nothing if auto-rendering was not enabled at construction or if the timer is already stopped.
     *//*
    public void stopAnimationLoop() {
        if (this.autoRenderingConfigured && animationTimer != null) {
            animationTimer.stop();
            // animationTimer = null; // Or keep the instance if you might restart it without re-creation
        }
    }*/
// Inside your ProcessingNode.java class

// Add this field to the class:
private long lastUpdateTimeNanos = 0;

// Modify your method that starts the AnimationTimer (e.g., startAnimationLoopInternal or constructor)
private void startAnimationLoop() { // Or whatever your method is named
    if (animationTimer != null) {
        animationTimer.stop();
    }
    // Reset lastUpdateTimeNanos when timer (re)starts
    this.lastUpdateTimeNanos = 0;

    animationTimer = new AnimationTimer() {
        @Override
        public void handle(long nowNanos) {
            if (lastUpdateTimeNanos == 0) { // First frame after start/restart
                lastUpdateTimeNanos = nowNanos;
                // Optionally render a static first frame if needed, or just return
                // For simplicity, just skip update logic on the very first handle() call
                // to ensure a valid dt for the next one.
                // However, rendering an initial state might be desirable.
                if (pxScene != null && writableImage != null) {
                     // pxScene.update(0); // Optionally, update with 0 dt for initial setup
                     pxScene.renderTo(writableImage); // Render initial state
                }
                return;
            }

            double deltaTimeSeconds = (nowNanos - lastUpdateTimeNanos) / 1_000_000_000.0;
            lastUpdateTimeNanos = nowNanos;

            // Optional: Clamp delta time to prevent unusually large steps (e.g., after a long pause)
            // This can help stabilize physics and animations.
            final double MAX_DT = 1.0 / 30.0; // Max step is 1/30th of a second
            if (deltaTimeSeconds > MAX_DT) {
                deltaTimeSeconds = MAX_DT;
            }

            if (pxScene != null && writableImage != null) {
                 pxScene.update(); // Call PxScene's update with calculated delta time
                 pxScene.renderTo(writableImage);  // Render the updated scene
            }
        }
    };
    animationTimer.start();
}

// If you have a method to stop the loop, like stopAnimationLoop(),
// you might also want to reset lastUpdateTimeNanos there or upon restarting.
public void stopAnimationLoop() {
    if (animationTimer != null) {
        animationTimer.stop();
    }
    // lastUpdateTimeNanos = 0; // Reset so that next start begins dt calculation fresh
                           // This is already handled in startAnimationLoop.
}
    /**
     * Restarts the animation loop if auto-rendering was configured at construction
     * and the timer is not currently running (e.g., after a call to {@link #stopAnimationLoop()}).
     * Does nothing if auto-rendering was not configured.
     */
    public void ensureAnimationLoopStarted() {
        if (this.autoRenderingConfigured) {
            if (animationTimer == null) { // If timer was stopped and nulled
                startAnimationLoopInternal();
            } else { // If timer instance exists but might be stopped
                animationTimer.start();
            }
        }
    }

    /**
     * Requests a manual render of the PxScene's current state to the JavaFX image.
     * This method is primarily effective if auto-rendering was disabled at construction
     * (i.e., {@code autoRender} was {@code false}).
     * It directly calls {@code pxScene.renderTo(writableImage)}.
     * <p>
     * If auto-rendering is active, this method typically does nothing, as the
     * {@link AnimationTimer} is responsible for rendering. This adheres to the
     * "choose one of two" (AnimationTimer or requestRender) rendering approaches.
     * </p>
     * <p>
     * Note: This method does NOT call {@code pxScene.update()}. In manual mode,
     * any scene updates should be performed by your application logic before calling this method.
     * </p>
     */
    public void requestRender() {
        if (!this.autoRenderingConfigured) {
            pxScene.renderTo(writableImage);
        }
        // If autoRenderingConfigured is true, the AnimationTimer is responsible for rendering.
    }

    /**
     * @return The underlying {@link PxScene} instance for direct manipulation or setup.
     */
    public PxScene getPxScene() {
        return pxScene;
    }

    /**
     * Call this method to clean up resources, especially to stop the AnimationTimer
     * when this node is no longer needed (e.g., when removing it from the scene graph).
     * This is important to prevent the AnimationTimer from running indefinitely.
     */
    public void dispose() {
        if (animationTimer != null) {
            animationTimer.stop();
            animationTimer = null;
        }
        // Any other cleanup for PxScene or WritableImage if necessary
    }

    // --- Optional: Resizing Support ---
    // The following is a conceptual example. To enable dynamic resizing, PxScene
    // must also be able to adapt to new dimensions (e.g., by resizing its internal PGraphics).

    /*
    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        final double newWidth = getWidth();
        final double newHeight = getHeight();

        // Check if size has actually changed and is valid (greater than 0)
        if (newWidth > 0 && newHeight > 0 &&
            (this.writableImage.getWidth() != newWidth || this.writableImage.getHeight() != newHeight)) {

            boolean timerWasEffectivelyRunning = false;
            if (this.autoRenderingConfigured && this.animationTimer != null) {
                // AnimationTimer has no direct isRunning() state check.
                // We assume if autoRenderingConfigured and timer exists, it should be running.
                this.animationTimer.stop();
                timerWasEffectivelyRunning = true;
            }

            this.writableImage = new WritableImage((int)newWidth, (int)newHeight);
            this.imageView.setImage(this.writableImage);

            // PxScene needs to adapt to the new size.
            // This might involve re-creating its internal PGraphics or calling a specific resize method.
            // Example: if PxScene has a method like pxScene.resize((int)newWidth, (int)newHeight);
            // For this example, we'll assume pxScene must be notified:
            // pxScene.notifyResize((int)newWidth, (int)newHeight); // Hypothetical method

            // After PxScene is adjusted for the new size, render its current state.
            // If PxScene was re-initialized or its content cleared on resize, this might show a default state.
            pxScene.renderTo(this.writableImage); // Render the current state of PxScene to the new image

            if (timerWasEffectivelyRunning) {
                this.animationTimer.start(); // Restart the timer
            }
        }

        // Ensure ImageView fills the region and is positioned correctly
        this.imageView.setFitWidth(newWidth);
        this.imageView.setFitHeight(newHeight);
        this.imageView.relocate(0, 0);
    }

    // If you implement layoutChildren for resizing, also make the Region resizable:
    @Override
    public boolean isResizable() {
        return true;
    }
    */
}