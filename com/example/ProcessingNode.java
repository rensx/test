package com.example;

import javafx.animation.AnimationTimer;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Region;

public class ProcessingNode extends Region {
    private final int initialWidth;
    private final int initialHeight;
    private final ImageView imageView;
    private WritableImage writableImage;
    private final PxScene pxScene;
    private AnimationTimer animationTimer;
    private final boolean autoRenderingConfigured;
    private long lastUpdateTimeNanos = 0;

    public ProcessingNode(int width, int height, boolean autoRender) {
        this.initialWidth = width;
        this.initialHeight = height;
        this.writableImage = new WritableImage(this.initialWidth, this.initialHeight);
        this.imageView = new ImageView(this.writableImage);
        this.pxScene = new PxScene(this.initialWidth, this.initialHeight);
        getChildren().add(imageView);
        setPrefSize(this.initialWidth, this.initialHeight);
        this.autoRenderingConfigured = autoRender;
        if (this.autoRenderingConfigured) {
            startAnimationLoopInternal();
        }
    }

    public ProcessingNode(int width, int height) {
        this(width, height, true);
    }

    private void startAnimationLoopInternal() {
        if (animationTimer != null) {
            animationTimer.stop();
        }
        lastUpdateTimeNanos = 0;
        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long nowNanos) {
                System.out.println("Animation loop running at " + nowNanos);
                if (lastUpdateTimeNanos == 0) {
                    lastUpdateTimeNanos = nowNanos;
                    pxScene.renderTo(writableImage);
                    return;
                }
                double dt = (nowNanos - lastUpdateTimeNanos) / 1_000_000_000.0;
                lastUpdateTimeNanos = nowNanos;
                if (dt > 1.0 / 30.0) dt = 1.0 / 30.0;
                pxScene.update(dt);
                pxScene.renderTo(writableImage);
            }
        };
        animationTimer.start();
    }

    public void stopAnimationLoop() {
        if (animationTimer != null) {
            animationTimer.stop();
        }
    }

    public void ensureAnimationLoopStarted() {
        if (this.autoRenderingConfigured) {
            if (animationTimer == null) {
                startAnimationLoopInternal();
            } else {
                animationTimer.start();
            }
        }
    }

    public void requestRender() {
        if (!this.autoRenderingConfigured) {
            pxScene.renderTo(writableImage);
        }
    }

    public PxScene getPxScene() {
        return pxScene;
    }

    public void dispose() {
        if (animationTimer != null) {
            animationTimer.stop();
            animationTimer = null;
        }
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        double newWidth = getWidth();
        double newHeight = getHeight();
        if (newWidth > 0 && newHeight > 0 &&
            (Math.abs(writableImage.getWidth() - newWidth) > 1 ||
             Math.abs(writableImage.getHeight() - newHeight) > 1)) {
            boolean wasRunning = autoRenderingConfigured && animationTimer != null;
            if (wasRunning) animationTimer.stop();
            writableImage = new WritableImage((int)newWidth, (int)newHeight);
            imageView.setImage(writableImage);
            pxScene.resize((int)newWidth, (int)newHeight);
            pxScene.renderTo(writableImage);
            if (wasRunning) animationTimer.start();
        }
        imageView.setFitWidth(newWidth);
        imageView.setFitHeight(newHeight);
        imageView.relocate(0, 0);
    }

    @Override
    public boolean isResizable() {
        return true;
    }
}