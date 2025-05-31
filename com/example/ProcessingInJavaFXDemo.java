package com.example;

import javafx.animation.RotateTransition;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.dyn4j.geometry.MassType;


public class ProcessingInJavaFXDemo extends Application {
    @Override
    public void start(Stage primaryStage) {
        ProcessingNode pNode = new ProcessingNode(800, 600);
        pNode.getPxScene().addObject(new PxAnimCircle(100, 100, 50).setColor(0xff00cc99));

        Group mainGroup = new Group();
        StackPane stack = new StackPane(mainGroup);
        mainGroup.getChildren().add(pNode);
PxLatexObject latexObj = new PxLatexObject(
    "E=mc^2, \\\\text{中文测试}", // 公式内容
    0, 1.5,                      // 物理坐标 (x, y)，单位米
    MassType.NORMAL,             // 可移动
    0.8,                         // 弹性系数
    32,                          // 字体像素大小
    100.0                        // 物理缩放（1米=100像素）
);
pNode.getPxScene().addObject(latexObj);
pNode.getPxScene().addObject(new PxStyledText("hello world",100,50));
        // 加入原生JavaFX控件
        Button fxButton = new Button("旋转Processing内容");
        fxButton.setOnAction(e -> {
            RotateTransition rot = new RotateTransition(Duration.seconds(1), pNode);
            rot.setByAngle(360);
            rot.play();
        });
        stack.getChildren().add(fxButton);

        // 你还可以加更多JavaFX节点

        Scene scene = new Scene(stack, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Processing + JavaFX 混合Demo");
        primaryStage.show();
    }

    public static void main(String[] args) { 
        launch(args); 
    }
}