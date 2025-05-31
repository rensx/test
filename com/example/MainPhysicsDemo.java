package com.example;

import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Geometry;
import processing.core.PApplet;
import processing.core.PGraphics;

public class MainPhysicsDemo extends PApplet {
    PxPhysicsWorld physicsWorld;
    PxPhysicsObject ball, floor;
PxLatexObject latexObj;
    public void settings() {
        size(800, 600);
    }

public void setup() {
    physicsWorld = new PxPhysicsWorld();

ball = new PxPhysicsObject(Geometry.createCircle(0.2), 0, 0, MassType.NORMAL, 0.9) {};
floor = new PxPhysicsObject(Geometry.createRectangle(6.0, 0.2), 0, -2.5, MassType.INFINITE, 0.5) {};
 latexObj = new PxLatexObject(
    "E=mc^2, \\\\text{中文测试}", // 公式内容
    0, 1.5,                      // 物理坐标 (x, y)，单位米
    MassType.NORMAL,             // 可移动
    0.8,                         // 弹性系数
    32,                          // 字体像素大小
    100.0                        // 物理缩放（1米=100像素）
);
    physicsWorld.add(ball,floor,latexObj);
  //  physicsWorld.add(floor);
    //physicsWorld.add(latexObj);

}

public void draw() {
    background(255);

    physicsWorld.step(1.0 / 60);

    ball.update();
    floor.update();
    latexObj.update();

    ball.draw(g);
    floor.draw(g);
    latexObj.draw(g);
}
    public static void main(String[] args) {
        PApplet.main(MainPhysicsDemo.class);
    }
}