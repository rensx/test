package com.example;

import processing.core.PGraphics;

/**
 * 场景对象基类。所有可绘制对象都应继承此类。
 */
public abstract class PxObject {
    /** 可选：被添加到场景时的回调 */
    public void addToSceneHook(PxScene scene) {}

    /** 可选：被移除时的回调 */
    public void removedFromSceneHook(PxScene scene) {}

    /** 可选：可见性控制 */
    public boolean isVisible() { return true; }

    /** 必须实现：绘制自己 */
    public abstract void draw(PGraphics g);

    /** 可选：每帧更新逻辑 */
    public void update() {}
}