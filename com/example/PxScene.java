package com.example;



import processing.core.PGraphics;

import processing.core.PVector;



import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;

import java.io.File;

import java.util.*;

import java.util.function.Predicate;

import java.util.logging.Level;

import java.util.stream.Collectors;

import javafx.scene.image.WritableImage;





/**

* 增强型物理场景，支持批量对象管理、动画、帧导出、日志、样式、相机等功能。

* 部分API风格参考JMathAnimScene。

*/

public class PxScene {

    // 场景对象

    protected final List<PxObject> objects = new ArrayList<>();

    protected final Set<PxObject> objectsAlreadyDrawn = new HashSet<>();

    protected final List<PxObject> objectsToBeRemoved = new ArrayList<>();

    protected final List<PxAnim> animations = new ArrayList<>();

    private final PxPhysicsWorld physicsWorld = new PxPhysicsWorld();



    // 相机参数

    protected float viewScale = 100.0f;

    protected PVector viewCenter = new PVector(0, 0);



    // 渲染相关

    protected final int width, height;

    protected final PGraphics pg;

    protected int frameCount = 0;



    // 日志

    protected static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger("com.example.PxScene");



    public PxScene(PGraphics pg) {

        this.pg = pg;

        this.width = pg.width;

        this.height = pg.height;

    }

    public PxScene(int w, int h) {

        this.width = w;

        this.height = h;

        this.pg = new processing.awt.PGraphicsJava2D();

        this.pg.setPrimary(false);

        this.pg.setSize(width, height);

        // 不要在这里 beginDraw/endDraw!

    }

    public PGraphics getPg() {return this.pg;}

    public void add(PxPhysicsObject obj) {

        objects.add(obj);

        physicsWorld.addBody(obj.getBody());

    }



    public void remove(PxPhysicsObject obj) {

        objects.remove(obj);

        physicsWorld.removeBody(obj.getBody());

    }



    public void step(double dt) {

        physicsWorld.step(dt);

        // 可以更新PxObject的状态（如同步物理位置到可视对象等）

    }

    public void addObject(PxObject o) {

        this.objects.add(o);

    }



    public void update() {

        for (PxObject o : objects) o.update();

    }



    public void renderTo(WritableImage image) {

        pg.beginDraw();

        try {

            pg.background(255);

            for (PxObject o : objects) {

                o.draw(pg);

            }

        } finally {

            pg.endDraw();

        }

        pg.loadPixels();

        image.getPixelWriter().setPixels(

            0, 0, width, height,

            javafx.scene.image.PixelFormat.getIntArgbInstance(),

            pg.pixels, 0, width

        );

    }

    // ----------- 场景对象管理 -----------

    public synchronized void add(PxObject... objs) {

        for (PxObject obj : objs) {

            if (obj != null && !objects.contains(obj)) {

                objects.add(obj);

                obj.addToSceneHook(this);

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

            }

        }

    }



    public synchronized void removeIf(Predicate<PxObject> pred) {

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



    // ----------- 相机/视角控制 -----------

    public void zoomToAllObjects() {

        List<PxPhysicsObject> phys = getObjectsOfType(PxPhysicsObject.class);

        if (phys.isEmpty()) return;

        float minX = Float.POSITIVE_INFINITY, minY = Float.POSITIVE_INFINITY;

        float maxX = Float.NEGATIVE_INFINITY, maxY = Float.NEGATIVE_INFINITY;

        for (PxPhysicsObject obj : phys) {

            PVector pos = obj.getPosition();

            float size = obj.getBoundingSize() / 2;

            minX = Math.min(minX, pos.x - size);

            minY = Math.min(minY, pos.y - size);

            maxX = Math.max(maxX, pos.x + size);

            maxY = Math.max(maxY, pos.y + size);

        }

        viewCenter.set((minX + maxX) / 2, (minY + maxY) / 2);

        float worldW = maxX - minX, worldH = maxY - minY, padding = 0.4f;

        worldW += padding; worldH += padding;

        float sx = width / worldW, sy = height / worldH;

        viewScale = Math.min(sx, sy);

    }



    public void zoomToObject(PxPhysicsObject obj) {

        PVector pos = obj.getPosition();

        viewCenter.set(pos.x, pos.y);

        float margin = 1.5f;

        float size = obj.getBoundingSize();

        viewScale = Math.min(width, height) / (size * margin);

    }



    public void resetCamera() {

        viewCenter.set(0, 0);

        viewScale = 100.0f;

    }



    // ----------- 动画管理 -----------

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



    // ----------- 渲染与导出 -----------

    public void draw() {

        pg.beginDraw();

        try {

            pg.background(255);

            pg.pushMatrix();

            pg.translate(width / 2f, height / 2f);

            pg.scale(viewScale, -viewScale);

            pg.translate(-viewCenter.x, -viewCenter.y);



            for (PxObject obj : objects) {

                if (obj.isVisible()) obj.draw(pg);

            }

            pg.popMatrix();

        } finally {

            pg.endDraw();

        }

    }



    /** 导出当前帧为图片（PNG） */

    public void exportFrame(String path) {

        try {

            draw();

            BufferedImage img = (BufferedImage) pg.getNative();

            ImageIO.write(img, "png", new File(path));

            logger.info("导出帧: " + path);

        } catch (Exception ex) {

            logger.log(Level.SEVERE, "导出帧失败", ex);

        }

    }



    /** 批量导出图片序列（用于视频合成） */

    public void exportFrames(int nFrames, String prefix) {

        for (int i = 0; i < nFrames; i++) {

            advanceFrame();

            exportFrame(String.format("%s_%05d.png", prefix, i));

        }

    }



    // ----------- 简单样式/日志接口示例 -----------

    public void infoMessage(String msg) {

        logger.info(msg);

    }



    // ----------- 其它辅助 -----------



    /** 标记对象已绘制，用于避免重复绘制或后处理 */

    public void markAsAlreadyDrawn(PxObject obj) {

        objectsAlreadyDrawn.add(obj);

    }



    public boolean isAlreadyDrawn(PxObject obj) {

        return objectsAlreadyDrawn.contains(obj);

    }

}