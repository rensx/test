package com.example;

import processing.core.PGraphics;
import processing.core.PVector;
import org.dyn4j.dynamics.Body;
import javafx.scene.image.WritableImage;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.awt.image.BufferedImage;
public class PxScene {
    protected final List<PxObject> objects = new ArrayList<>();
    protected final Set<PxObject> objectsAlreadyDrawn = new HashSet<>();
    protected final List<PxAnim> animations = new ArrayList<>();
    private final PxPhysicsWorld physicsWorld = new PxPhysicsWorld();
    protected float viewScale = 100.0f;
    protected PVector viewCenter = new PVector(0, 0);
    protected int width, height;
    protected final PGraphics pg;
    protected int frameCount = 0;
    protected static final Logger logger = Logger.getLogger("com.example.PxScene");

    public PxScene(int w, int h) {
        this.width = w;
        this.height = h;
        this.pg = new processing.awt.PGraphicsJava2D();
        this.pg.setPrimary(false);
        this.pg.setSize(width, height);
        this.physicsWorld.getWorld().setGravity(0, -9.81); // 设置重力
    }

    public PGraphics getPg() { return this.pg; }

    public synchronized void add(PxObject... objs) {
        for (PxObject obj : objs) {
            if (obj != null && !objects.contains(obj)) {
                objects.add(obj);
                obj.addToSceneHook(this);
                if (obj instanceof PxPhysicsObject) {
                    physicsWorld.addBody(((PxPhysicsObject) obj).getBody());
                }
            }
        }
    }

    public synchronized void add(List<? extends PxObject> objs) {
        for (PxObject obj : objs) add(obj);
    }

    public synchronized void remove(PxObject... objs) {
        for (PxObject obj : objs) {
            if (obj != null && objects.contains(obj)) {
                objects.remove(obj);
                obj.removedFromSceneHook(this);
                if (obj instanceof PxPhysicsObject) {
                    physicsWorld.removeBody(((PxPhysicsObject) obj).getBody());
                }
            }
        }
    }

    public synchronized void removeIf(java.util.function.Predicate<PxObject> pred) {
        List<PxObject> toRemove = objects.stream().filter(pred).collect(Collectors.toList());
        remove(toRemove.toArray(new PxObject[0]));
    }

    public synchronized void clear() {
        remove(objects.toArray(new PxObject[0]));
        objectsAlreadyDrawn.clear();
    }

    public List<PxObject> getObjects() {
        return Collections.unmodifiableList(objects);
    }

    public <T extends PxObject> List<T> getObjectsOfType(Class<T> cls) {
        return objects.stream().filter(cls::isInstance).map(cls::cast).collect(Collectors.toList());
    }

    public void update(double dt) {
        physicsWorld.step(dt);
        for (PxObject o : objects) o.update();
        animations.removeIf(PxAnim::isFinished);
        for (PxAnim anim : animations) anim.update();
    }

    public void draw() {
        pg.beginDraw();
        try {
            pg.background(255);
            pg.pushMatrix();
            pg.translate(width / 2f, height / 2f);
            pg.scale(viewScale, -viewScale);
            pg.translate(-viewCenter.x, -viewCenter.y);
            for (PxObject obj : objects) {
                if (obj.isVisible()) {
                    obj.draw(pg);
                }
            }
            pg.popMatrix();
        } finally {
            pg.endDraw();
        }
    }

    public void renderTo(WritableImage image) {
        draw();
        pg.loadPixels();
        image.getPixelWriter().setPixels(
            0, 0, width, height,
            javafx.scene.image.PixelFormat.getIntArgbInstance(),
            pg.pixels, 0, width
        );
    }

    public void zoomToAllObjects() {
        List<PxPhysicsObject> phys = getObjectsOfType(PxPhysicsObject.class);
        if (phys.isEmpty()) return;
        float minX = Float.POSITIVE_INFINITY, minY = Float.POSITIVE_INFINITY;
        float maxX = Float.NEGATIVE_INFINITY, maxY = Float.NEGATIVE_INFINITY;
        for (PxPhysicsObject obj : phys) {
            PVector pos = obj.getPosition();
            float size = obj.getBoundingSize() / 2;
            minX = Math.min(minX, pos.x / viewScale - size);
            minY = Math.min(minY, pos.y / viewScale - size);
            maxX = Math.max(maxX, pos.x / viewScale + size);
            maxY = Math.max(maxY, pos.y / viewScale + size);
        }
        viewCenter.set((minX + maxX) / 2, (maxY + minY) / 2);
        float worldW = maxX - minX, worldH = maxY - minY, padding = 0.4f;
        worldW += padding; worldH += padding;
        float sx = width / worldW, sy = height / worldH;
        viewScale = Math.min(sx, sy);
    }

    public void zoomToObject(PxPhysicsObject obj) {
        PVector pos = obj.getPosition();
        viewCenter.set(pos.x / viewScale, pos.y / viewScale);
        float margin = 1.5f;
        float size = obj.getBoundingSize();
        viewScale = Math.min(width, height) / (size * margin);
    }

    public void resetCamera() {
        viewCenter.set(0, 0);
        viewScale = 100.0f;
    }

    public void playAnimation(PxAnim... anims) {
        for (PxAnim anim : anims) {
            if (anim != null) {
                anim.setScene(this);
                animations.add(anim);
            }
        }
    }

    public void advanceFrame() {
        for (PxAnim anim : animations) {
            anim.update();
        }
        draw();
        frameCount++;
    }

    public void exportFrame(String path) {
        try {
            draw();
            BufferedImage img = (BufferedImage) pg.getNative();
            javax.imageio.ImageIO.write(img, "png", new java.io.File(path));
            logger.info("Exported frame: " + path);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Failed to export frame", ex);
        }
    }

    public void exportFrames(int nFrames, String prefix) {
        for (int i = 0; i < nFrames; i++) {
            advanceFrame();
            exportFrame(String.format("%s_%05d.png", prefix, i));
        }
    }

    public void infoMessage(String msg) {
        logger.info(msg);
    }

    public void markAsAlreadyDrawn(PxObject obj) {
        objectsAlreadyDrawn.add(obj);
    }

    public boolean isAlreadyDrawn(PxObject obj) {
        return objectsAlreadyDrawn.contains(obj);
    }

    public void resize(int newWidth, int newHeight) {
        this.width = newWidth;
        this.height = newHeight;
        pg.setSize(newWidth, newHeight);
    }
}