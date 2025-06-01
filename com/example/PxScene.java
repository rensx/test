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
        this.physicsWorld.getWorld().setGravity(0, -981); // Pixels/s²
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

    public synchronized void clearObjects() {
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
            System.out.println("Drawing scene with " + objects.size() + " objects");
            for (PxObject obj : objects) {
                System.err.println("Checking object: " + obj.getClass().getSimpleName() + ", visible: " + obj.isVisible());
                if (obj.isVisible()) {
                    System.err.println("Scene draw: " + obj.getClass().getSimpleName());
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

    public void resetCamera() {
        // No-op for pixel coordinates
    }

    public PVector worldToScreen(PVector world) {
        return new PVector(world.x, world.y);
    }

    public PVector screenToWorld(PVector screen) {
        return new PVector(screen.x, screen.y);
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
        width = newWidth;
        height = newWidth;
        pg.setSize(newWidth, newHeight);
    }
}