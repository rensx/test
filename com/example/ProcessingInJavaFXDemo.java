package com.example;

import javafx.animation.RotateTransition;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Geometry;
import processing.core.PApplet;
import processing.core.PGraphics;
import javafx.animation.AnimationTimer;
public class ProcessingInJavaFXDemo extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        ProcessingNode pNode = new ProcessingNode(800, 600);
        pNode.getPxScene().addObject(new PxAnimCircle(100, 100, 50).setColor(0xff00cc99));

        pNode.getPxScene().addObject(new PxRect(100, 100));
        pNode.getPxScene().addObject(new PxBatikSvgObject("assets/test.svg",100, 100));


        PxLatexObject latexObj = new PxLatexObject(
            "E=mc^2, \\color{red}{\\text{中文测试}}", // 公式内容
            0, 1.5,                      // 物理坐标 (x, y)，单位米
            MassType.NORMAL,             // 可移动
            0.8,                         // 弹性系数
            32,                          // 字体像素大小
            100.0                        // 物理缩放（1米=100像素）
        );
        pNode.getPxScene().addObject(latexObj);

PxPhysicsObject ball = new PxPhysicsObject(Geometry.createCircle(0.2), 0, 0, MassType.NORMAL, 0.9) {};
PxPhysicsObject floor = new PxPhysicsObject(Geometry.createRectangle(6.0, 0.2), 0, -2.5, MassType.INFINITE, 0.5) {};
pNode.getPxScene().add(ball,floor);

        /*
PxCircle circle = PxScene.Create.circle().withRadius(50);
PxRectangle rect = PxScene.Create.rectangle().withSize(100, 100);
PxText text = PxScene.Create.text("Hello").withTextSize(24);


pNode.getPxScene().add(circle, rect, text);

// Animate them
pNode.getPxScene().animate(circle)
     .moveTo(100, 100, 1.0)
     .rotateTo(45, 0.5);

pNode.getPxScene().animate(rect)
     .scaleTo(2, 2, 1.0);

pNode.getPxScene().animate(text)
     .fadeTo(0.5f, 0.5);
  */  
        Group mainGroup = new Group();
        StackPane stack = new StackPane(mainGroup);
        mainGroup.getChildren().add(pNode);

        // 加入原生JavaFX控件
        Button fxButton = new Button("旋转Processing内容");
        fxButton.setOnAction(e -> {
            RotateTransition rot = new RotateTransition(Duration.seconds(1), pNode);
            rot.setByAngle(360);
            rot.play();
        });
        stack.getChildren().add(fxButton);
/*
        Button fxButtonz = new Button("zoom");
        fxButton.setOnAction(e -> {
    pNode.getPxScene().zoomToObject(latexObj);
        });
        stack.getChildren().add(fxButtonz);*/
        Scene scene = new Scene(stack, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Processing + JavaFX + SVG Demo");
        primaryStage.show();
}

    public static void main(String[] args) { launch(args); }
}