package com.example;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.dyn4j.geometry.MassType;
import javafx.animation.RotateTransition;
import javafx.util.Duration;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;

public class ProcessingInJavaFXDemo extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        ProcessingNode pNode = new ProcessingNode(800, 600);
        PxScene scene = pNode.getPxScene();
        PxLatexObject latexObj = new PxLatexObject(
            "E=mc^2, \\text{中文测试🧁}", // 公式内容
            0,100,                      // 物理坐标 (x, y)，单位米
            MassType.INFINITE,             // 可移动
            0.8,                         // 弹性系数
            32,                          // 字体像素大小
            0.01                       // 物理缩放（1米=100像素）
        );
scene.add(latexObj);
        // Create sprite and circle
        PxSpriteObject sprite = new PxSpriteObject("/spritesheet.png", 4, 4, 32, 32, 10, 0, 5);
        PxCircle circle = new PxCircle(-1, 3, 0.3, MassType.INFINITE, 0xFFFF0000);
        circle.setRestitution(0.9);

        // Create sequence with effects
        PxSequenceAnim sequence = new PxSequenceAnim()
            .addWithEffect(sprite, 0.0, new MoveInEffect(sprite, 0, 5, 1.0)) // Smooth add with moveIn
            .waitSeconds(1)
            .addWithEffect(circle, 0.0, new MoveInEffect(circle, -1, 3, 1.0)) // Smooth add with moveIn
            .setPositionWithEffect(circle, 0, 5, 0.0, new ShiftEffect(circle, 0, 5, 1.0)) // Reset position smoothly

            .setPositionWithEffect(circle, 1, 0, 0.0, new ShiftEffect(circle, 1, 0, 1.0)) // Reset position smoothly

            .waitSeconds(5);

        // Play the sequence
        scene.playAnimation(sequence);

        // Existing shapes
        PxRectangle floor = new PxRectangle(0, -2, 12, 0.2, MassType.INFINITE, 0xFF888888);
        scene.add(floor);

        // Camera setup
        scene.resetCamera();
        scene.viewScale = 100.0f;
        scene.viewCenter.set(1, 1); // Center at y=1

        // JavaFX controls
        Group mainGroup = new Group();
        StackPane stack = new StackPane(mainGroup);
        mainGroup.getChildren().add(pNode);

        Button fxButton = new Button("Rotate Processing Content");
        fxButton.setOnAction(e -> {
            RotateTransition rot = new RotateTransition(Duration.seconds(1), pNode);
            rot.setByAngle(360);
            rot.play();
        });
     //   stack.getChildren().add(fxButton);

        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(Color.ORANGE);
        material.setSpecularColor(Color.BLACK);

        Sphere sphere = new Sphere(100);
        sphere.setMaterial(material);
   //     stack.getChildren().add(sphere);

        Scene fxScene = new Scene(stack, 800, 600);
        primaryStage.setScene(fxScene);
        primaryStage.setTitle("Sprite Animation Demo");
        primaryStage.show();
    }

    public static void main(String[] args) {
        System.setProperty("prism.forceGPU", "true");
        launch(args);
    }
}