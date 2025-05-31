package com.example;

import processing.core.PGraphics;
import processing.core.PVector;
// import processing.core.PConstants; // Keep if PxObject implementations need it

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.stream.Collectors;
import javafx.scene.image.WritableImage;

public class PxScene {
    // Scene objects
    protected final List<PxObject> objects = new ArrayList<>();
    protected final Set<PxObject> objectsAlreadyDrawn = new HashSet<>(); // For specific object logic
    protected final List<PxObject> objectsToBeRemoved = new ArrayList<>(); // For deferred removal
    protected final List<PxAnim> animations = new ArrayList<>();
    private final PxPhysicsWorld physicsWorld = new PxPhysicsWorld();

    // Camera parameters (world units)
    protected float viewScale = 100.0f; // Pixels per world unit
    protected PVector viewCenter = new PVector(0, 0); // World coordinates at the center of the view

    // Rendering related
    protected final int width, height;
    protected final PGraphics pg;
    protected int frameCount = 0;
    protected int backgroundColor; // Stored as a Processing color int

    // Logging
    protected static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(PxScene.class.getName());

    public PxScene(PGraphics pg) {
        this.pg = pg;
        this.width = pg.width;
        this.height = pg.height;
        this.backgroundColor = pg.color(255); // Default white background
        logger.info("PxScene initialized with existing PGraphics.");
    }

    public PxScene(int w, int h) {
        this.width = w;
        this.height = h;
        this.pg = new processing.awt.PGraphicsJava2D();
        this.pg.setPrimary(false);
        this.pg.setSize(this.width, this.height);
        this.backgroundColor = pg.color(255); // Default white background
        logger.info("PxScene initialized with new PGraphics (" + w + "x" + h + ").");
    }

    public PGraphics getPg() {
        return this.pg;
    }

    public void update(double dt) {
        processPendingRemovals();

        Iterator<PxAnim> animIterator = animations.iterator();
        while (animIterator.hasNext()) {
            PxAnim anim = animIterator.next();
            anim.update();
            if (anim.isFinished()) { // Now calls the method from PxAnim
                animIterator.remove();
            }
        }

        for (PxObject obj : objects) {
            obj.update();
        }

        physicsWorld.step(dt);
        frameCount++;
    }

    private synchronized void processPendingRemovals() {
        if (objectsToBeRemoved.isEmpty()) {
            return;
        }
        for (PxObject obj : objectsToBeRemoved) {
            if (objects.remove(obj)) {
                obj.removedFromSceneHook(this);
                if (obj instanceof PxPhysicsObject) {
                    // Corrected: Pass the Body to PxPhysicsWorld
                    physicsWorld.removeBody(((PxPhysicsObject) obj).getBody());
                }
                objectsAlreadyDrawn.remove(obj);
            }
        }
        objectsToBeRemoved.clear();
    }


    public synchronized void add(PxObject... objs) {
        for (PxObject obj : objs) {
            if (obj != null && !objects.contains(obj)) {
                objects.add(obj);
                obj.addToSceneHook(this);
                if (obj instanceof PxPhysicsObject) {
                    // Corrected: Pass the Body to PxPhysicsWorld
                    physicsWorld.addBody(((PxPhysicsObject) obj).getBody());
                }
            }
        }
    }
    
    /**
     * Convenience method to add a single PxObject. Delegates to {@link #add(PxObject...)}.
     * @param obj The PxObject to add.
     */
    public synchronized void addObject(PxObject obj) { // Added for compatibility
        add(obj); // Delegates to the varargs version
    }


    public synchronized void add(List<? extends PxObject> objs) {
        if (objs != null) {
            add(objs.toArray(new PxObject[0]));
        }
    }

    public synchronized void remove(PxObject... objs) {
        for (PxObject obj : objs) {
            if (obj != null && objects.contains(obj) && !objectsToBeRemoved.contains(obj)) {
                objectsToBeRemoved.add(obj);
            }
        }
    }

    public synchronized void removeIf(Predicate<PxObject> predicate) {
        List<PxObject> toRemove = objects.stream().filter(predicate).collect(Collectors.toList());
        if (!toRemove.isEmpty()) {
            remove(toRemove.toArray(new PxObject[0]));
        }
    }

    public synchronized void clear() {
        remove(objects.toArray(new PxObject[0]));
        objectsAlreadyDrawn.clear();
    }

    public List<PxObject> getObjects() {
        return Collections.unmodifiableList(objects);
    }

    public <T extends PxObject> List<T> getObjectsOfType(Class<T> type) {
        return objects.stream()
                      .filter(type::isInstance)
                      .map(type::cast)
                      .collect(Collectors.toList());
    }

    public void setViewScale(float scale) { this.viewScale = scale; }
    public float getViewScale() { return this.viewScale; }
    public void setViewCenter(float x, float y) { this.viewCenter.set(x, y); }
    public PVector getViewCenter() { return this.viewCenter.copy(); }

    public void zoomToAllObjects() {
        List<PxPhysicsObject> phys = getObjectsOfType(PxPhysicsObject.class);
        if (phys.isEmpty()) return;

        float minX = Float.POSITIVE_INFINITY, minY = Float.POSITIVE_INFINITY;
        float maxX = Float.NEGATIVE_INFINITY, maxY = Float.NEGATIVE_INFINITY;

        for (PxPhysicsObject obj : phys) {
            PVector pos = obj.getPosition();
            float size = obj.getBoundingSize() / 2f;
            minX = Math.min(minX, pos.x - size);
            minY = Math.min(minY, pos.y - size);
            maxX = Math.max(maxX, pos.x + size);
            maxY = Math.max(maxY, pos.y + size);
        }

        if (Float.isInfinite(minX)) return;

        viewCenter.set((minX + maxX) / 2f, (minY + maxY) / 2f);
        float worldW = maxX - minX;
        float worldH = maxY - minY;
        float padding = Math.max(worldW, worldH) * 0.1f;
        worldW += padding;
        worldH += padding;

        if (worldW <= 0 || worldH <= 0) {
             viewScale = 100.0f;
             return;
        }

        float scaleX = (float)width / worldW;
        float scaleY = (float)height / worldH;
        viewScale = Math.min(scaleX, scaleY);
    }

    public void zoomToObject(PxPhysicsObject obj) {
        if (obj == null) return;
        PVector pos = obj.getPosition();
        viewCenter.set(pos.x, pos.y);
        float size = obj.getBoundingSize();
        float marginFactor = 1.5f;
        if (size <= 0) {
            viewScale = 100.0f;
            return;
        }
        viewScale = Math.min(width, height) / (size * marginFactor);
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

    public void setBackgroundColor(int gray) {
        this.backgroundColor = pg.color(gray);
    }

    public void setBackgroundColor(int r, int g, int b) {
        this.backgroundColor = pg.color(r, g, b);
    }

    public void draw() {
        pg.beginDraw();
        try {
            pg.background(backgroundColor);
            pg.pushMatrix();
            pg.translate(width / 2.0f, height / 2.0f);
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
        if (pg.pixels != null && pg.pixels.length > 0) {
            image.getPixelWriter().setPixels(
                0, 0, width, height,
                javafx.scene.image.PixelFormat.getIntArgbInstance(),
                pg.pixels, 0, width
            );
        } else {
            logger.warning("PGraphics pixel data is null or empty. Cannot render to WritableImage.");
        }
    }

    public void advanceFrameForExport(double fixedDt) {
        update(fixedDt);
    }

    public void exportFrame(String filePath) {
        try {
            draw();
            Object nativeImage = pg.getNative();
            if (nativeImage instanceof BufferedImage) {
                ImageIO.write((BufferedImage) nativeImage, "png", new File(filePath));
                logger.log(Level.INFO, "Exported frame: " + filePath);
            } else {
                logger.log(Level.SEVERE, "Failed to export frame: PGraphics native image is not a BufferedImage.");
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Exporting frame to " + filePath + " failed.", ex);
        }
    }

    public void exportFrames(int numFrames, String filePrefix, double fixedDt) {
        logger.info("Starting batch frame export: " + numFrames + " frames with prefix '" + filePrefix + "'.");
        for (int i = 0; i < numFrames; i++) {
            advanceFrameForExport(fixedDt);
            exportFrame(String.format("%s_%05d.png", filePrefix, i));
        }
        logger.info("Finished batch frame export.");
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
    public void update(){
        update(1d/60);
    }
}