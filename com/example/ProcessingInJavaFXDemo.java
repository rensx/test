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

//import javafx.scene.shape.Sphere;
//import javafx.scene.paint.PhongMaterial;
public class ProcessingInJavaFXDemo extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        ProcessingNode pNode = new ProcessingNode(800, 600);
        PxScene scene = pNode.getPxScene();

        // Create shapes
        PxCircle circle = new PxCircle(-2, 5, 0.3, MassType.NORMAL, 0xFFFF0000);
        circle.setRestitution(0.9);
        PxRectangle square = PxRectangle.createSquare(0, 5, 0.6, MassType.NORMAL, 0xFF00FF00);
        square.setRestitution(0.7);
        PxRectangle rectangle = new PxRectangle(2, 5, 0.8, 0.4, MassType.NORMAL, 0xFF0000FF);
        rectangle.setRestitution(0.7);
        PxTriangle triangle = new PxTriangle(4, 5, 0.6, MassType.NORMAL, 0xFFFFFF00);
        triangle.setRestitution(0.7);
        PxRectangle floor = new PxRectangle(0, -2, 12, 0.2, MassType.INFINITE, 0xFF888888);
        PxLatexObject latexObj = new PxLatexObject(
            "E=mc^2, \\text{中文测试🧁}", // 公式内容
            0, 1.5,                      // 物理坐标 (x, y)，单位米
            MassType.NORMAL,             // 可移动
            0.8,                         // 弹性系数
            32,                          // 字体像素大小
            100.0                        // 物理缩放（1米=100像素）
        );
        //PhongMaterial material = new PhongMaterial();
        //material.setDiffuseColor(Color.ORANGE);
        //material.setSpecularColor(Color.BLACK);

        javafx.scene.shape.Circle circlefx = new javafx.scene.shape.Circle();
        circlefx.setCenterX(100.0f);
        circlefx.setCenterY(100.0f);
        circlefx.setRadius(50.0f);
        circlefx.setFill(Color.ORANGE);


      Text text = new Text();
      text.setFont(new Font(20));
      text.setWrappingWidth(300);
      text.setTextAlignment(TextAlignment.JUSTIFY);
      text.setText("Variety is the spice of life\nEvery cloud has a silver lining");
      
      //setting the position of the text
      text.setX(50); 
      text.setY(130);

      PxStyledText styletext=new PxStyledText("hello world",100,40);

      PxBatikSvgObject svgObj = null;
        try {
            svgObj = new PxBatikSvgObject("assets/test.svg", 400, 300)
            .setScale(2.0f)
            .setRotation(0);
        } catch (Exception e) {
            System.err.println("加载SVG失败：");
            e.printStackTrace();
        }
        if (svgObj != null) {
            scene.add(svgObj);
        }
        // Add to scene
        scene.add(circle, square, rectangle, triangle, floor, latexObj, styletext);

        // Camera setup
        scene.resetCamera();
        scene.viewScale = 100.0f;
        scene.viewCenter.set(0, 1); // Center at y=1

        // Export initial frame
        //   scene.exportFrame("initial_frame.png");

        // JavaFX controls
        Group mainGroup = new Group();
        StackPane stack = new StackPane(mainGroup);
        mainGroup.getChildren().add(pNode);
        mainGroup.getChildren().add(circlefx);
        mainGroup.getChildren().add(text);

        Button fxButton = new Button("Rotate Processing Content");
        fxButton.setOnAction(e -> {
            RotateTransition rot = new RotateTransition(Duration.seconds(1), pNode);
            rot.setByAngle(360);
            rot.play();
        });
        stack.getChildren().add(fxButton);

        Scene fxScene = new Scene(stack, 800, 600);
        primaryStage.setScene(fxScene);
        primaryStage.setTitle("Geometric Shapes Demo");
        primaryStage.show();
    }

    public static void main(String[] args) { launch(args); }
}