package com.example;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.dyn4j.geometry.MassType;

public class ProcessingInJavaFXDemo extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        ProcessingNode pNode = new ProcessingNode(800, 600);
        PxScene scene = pNode.getPxScene();

        PxCircle circle = new PxCircle(200, 200, 30, MassType.NORMAL, 0xFFFF0000);
        circle.setRestitution(0.9);
        PxRectangle square = PxRectangle.createSquare(400, 100, 60, MassType.NORMAL, 0xFF00FF00);
        square.setRestitution(0.7);
        PxRectangle rectangle = new PxRectangle(600, 150, 80, 40, MassType.NORMAL, 0xFF0000FF);
        rectangle.setRestitution(0.7);
        PxTriangle triangle = new PxTriangle(300, 150, 60, MassType.NORMAL, 0xFFFFFF00);
        triangle.setRestitution(0.7);
        PxRectangle floor = new PxRectangle(400, 500, 1200, 20, MassType.INFINITE, 0xFF888888);
        PxLatexObject latexObj = new PxLatexObject("x^2", 400, 150, MassType.INFINITE, 0.8, 32);
        PxStyledText styletext = new PxStyledText("hello", 400, 200);
        PxBatikSvgObject svgObj = null;
        try {
            svgObj = new PxBatikSvgObject("assets/test.svg", 400, 250);
            svgObj.setScale(0.1f).setRotation(0);
        } catch (Exception e) {
            System.err.println("Failed to load SVG: " + e.getMessage());
            e.printStackTrace();
        }

        scene.add(circle, square, rectangle, triangle, floor, latexObj, styletext);
        if (svgObj != null) {
            scene.add(svgObj);
        }
        System.out.println("Objects in scene: " + scene.getObjects().size());
        scene.getObjects().forEach(obj -> System.err.println("Object: " + obj.getClass().getSimpleName()));

        scene.resetCamera();
        pNode.ensureAnimationLoopStarted();

        Group mainGroup = new Group();
        StackPane stackPane = new StackPane(mainGroup);
        mainGroup.getChildren().add(pNode);

        Button fxButton = new Button("Rotate Processing Content");
        fxButton.setOnAction(e -> {
            javafx.animation.RotateTransition rot = new javafx.animation.RotateTransition(javafx.util.Duration.seconds(3), pNode);
            rot.setByAngle(360);
            rot.play();
        });
        stackPane.getChildren().add(fxButton);

        Scene fxScene = new Scene(stackPane, 800, 600);
        primaryStage.setScene(fxScene);
        primaryStage.setTitle("Geometric Shapes Demo");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}