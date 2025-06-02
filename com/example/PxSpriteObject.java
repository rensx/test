package com.example;

import javafx.animation.AnimationTimer;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;

public class PxSpriteObject extends PxObject {
    private final ImageView imageView;
    private final PImage[] frames;
    private final int frameWidth, frameHeight;
    private final int columns, totalFrames;
    private final float fps;
    private int currentFrame = 0;
    private long lastFrameTime = 0;
    private final AnimationTimer timer;
    private boolean isPlaying = false;
    private boolean loop = true;
    private int fillColor = 0xFFFFFFFF;
    protected PVector position;
    private boolean loadFailed = false;

    public PxSpriteObject(String spriteSheetPath, int columns, int totalFrames, int frameWidth, int frameHeight, float fps, float x, float y) {
        Image fxImage = null;
        try {
            fxImage = new Image(getClass().getResourceAsStream(spriteSheetPath));
            if (fxImage.isError()) {
                System.err.println("Failed to load sprite sheet: " + spriteSheetPath);
                loadFailed = true;
            }
        } catch (Exception e) {
            System.err.println("Error loading sprite sheet: " + e.getMessage());
            loadFailed = true;
        }
        this.imageView = new ImageView(fxImage);
        this.columns = columns;
        this.totalFrames = totalFrames;
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
        this.fps = fps;
        this.position = new PVector(x, y);
        if (fxImage != null) {
            this.imageView.setViewport(new Rectangle2D(0, 0, frameWidth, frameHeight));
        }

        this.frames = new PImage[totalFrames];
        if (!loadFailed && fxImage != null) {
            for (int i = 0; i < totalFrames; i++) {
                int col = i % columns;
                int row = i / columns;
                frames[i] = convertToPImage(fxImage, col * frameWidth, row * frameHeight, frameWidth, frameHeight);
                if (frames[i] == null) {
                    System.err.println("Failed to convert frame " + i);
                    frames[i] = createFallbackImage();
                } else {
                    // Check frame data
                    int pixel = frames[i].get(16, 16);
                    System.out.println("Frame " + i + " center pixel ARGB: " + Integer.toHexString(pixel));
                }
            }
        } else {
            for (int i = 0; i < totalFrames; i++) {
                frames[i] = createFallbackImage();
            }
        }
        System.out.println("Initialized sprite with " + totalFrames + " frames, loadFailed=" + loadFailed);

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!isPlaying) return;
                if (lastFrameTime == 0) lastFrameTime = now;
                long elapsedNanos = now - lastFrameTime;
                int frameJump = (int) Math.floor(elapsedNanos / (1_000_000_000.0 / fps));
                if (frameJump >= 1) {
                    lastFrameTime = now;
                    currentFrame = (currentFrame + frameJump) % totalFrames;
                    if (!loop && currentFrame + frameJump >= totalFrames) {
                        stop();
                        currentFrame = totalFrames - 1;
                    }
                    if (!loadFailed) updateViewport();
                    System.out.println("Sprite frame: " + currentFrame);
                }
            }
        };
    }

    private PImage createFallbackImage() {
        PImage fallback = new PImage(frameWidth, frameHeight, PImage.ARGB);
        for (int i = 0; i < frameWidth; i++) {
            for (int j = 0; j < frameHeight; j++) {
                fallback.set(i, j, 0xFFFF0000);
            }
        }
        return fallback;
    }

    private void updateViewport() {
        int col = currentFrame % columns;
        int row = currentFrame / columns;
        imageView.setViewport(new Rectangle2D(col * frameWidth, row * frameHeight, frameWidth, frameHeight));
    }

    public void play(boolean loop) {
        this.loop = loop;
        if (!isPlaying) {
            isPlaying = true;
            lastFrameTime = 0;
            timer.start();
            System.out.println("Sprite animation started");
        }
    }

    public void stop() {
        isPlaying = false;
        timer.stop();
        System.out.println("Sprite animation stopped");
    }

    public void setOpacity(float opacity) {
        int alpha = (int) (opacity * 255);
        fillColor = (fillColor & 0x00FFFFFF) | (alpha << 24);
        System.out.println("Sprite opacity set to: " + opacity + ", alpha=" + alpha);
    }

    @Override
    public void draw(PGraphics pg) {
        if (!isVisible()) {
            System.out.println("Sprite not visible");
            return;
        }
        if (scene == null) {
            System.err.println("Scene is null in PxSpriteObject.draw");
            return;
        }
        pg.pushMatrix();
        PVector screenPos = scene.worldToScreen(position);
        float scale = 4.0f; // Larger for visibility
        System.out.println("Drawing sprite at worldPos: (" + position.x + ", " + position.y + "), screenPos: (" + screenPos.x + ", " + screenPos.y + "), scale: " + scale + ", frame: " + currentFrame);
        pg.translate(screenPos.x, screenPos.y);
        pg.tint(255, 255, 255, 255); // Ignore fillColor for now
        if (frames[currentFrame] != null) {
            pg.image(frames[currentFrame], 0, 0, frameWidth * scale, frameHeight * scale);
        } else {
            System.err.println("Frame " + currentFrame + " is null");
            pg.fill(255, 0, 0);
            pg.rect(0, 0, frameWidth * scale, frameHeight * scale);
        }
        pg.popMatrix();
        pg.tint(255, 255);
    }

    private PImage convertToPImage(Image fxImage, int x, int y, int w, int h) {
        try {
            PImage pImage = new PImage(w, h, PImage.ARGB);
            javafx.scene.image.PixelReader reader = fxImage.getPixelReader();
            for (int i = 0; i < w; i++) {
                for (int j = 0; j < h; j++) {
                    int argb = reader.getArgb(x + i, y + j);
                    pImage.set(i, j, argb);
                }
            }
            return pImage;
        } catch (Exception e) {
            System.err.println("Error converting to PImage: " + e.getMessage());
            return createFallbackImage();
        }
    }

    @Override
    public float getBoundingSize() {
        return Math.max(frameWidth, frameHeight) * 4.0f;
    }

    @Override
    public void update() {
    }

    public PVector getPosition() {
        return position.copy();
    }

    public void setPosition(float x, float y) {
        position.set(x, y);
        System.out.println("Sprite position set to: (" + x + ", " + y + ")");
    }
}