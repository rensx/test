package com.example;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.layout.Region;


//import javafx.scene.shape.Sphere;
//import javafx.scene.paint.PhongMaterial;
public class FxScene  {
    private StackPane stack;
    private Group mainGroup;
    private Stage primaryStage;
    private ProcessingNode pNode;
    private int mediaW,mediaH;
    private PxScene pxScene;
    public FxScene(Stage primaryStage, int width, int height) {
        // JavaFX controls
        this.mediaW=width;
        this.mediaH=height;
        this.pNode=new ProcessingNode(width,height);
        this.pxScene=pNode.getPxScene();
        this.mainGroup = new Group();
        this.stack = new StackPane(mainGroup);
        this.primaryStage = primaryStage;
    }
    public FxScene addNode(Object obj) {
        if (obj instanceof javafx.scene.shape.Shape3D) {
            System.err.println(">>>>");
            stack.getChildren().add((javafx.scene.shape.Shape3D)obj);
        } else if (obj instanceof javafx.scene.Node) {
            stack.getChildren().add((javafx.scene.Node)obj);
        } else if(obj instanceof PxObject){
            pxScene.add((PxObject)obj);

        }
        return this;

    }
    public FxScene add(Object... objs) {
        for (Object obj : objs) {
            addNode(obj);
        }
        return this;
    }
    public StackPane getStack(){return stack;}
    /*{
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(Color.ORANGE);
        material.setSpecularColor(Color.BLACK);

        Sphere sphere = new Sphere(100);
        sphere.setMaterial(material);
        stack.getChildren().add(sphere);*/
    public void show() {
        stack.getChildren().add(pNode);

        Scene fxScene = new Scene(stack, this.mediaW, this.mediaH);
        primaryStage.setScene(fxScene);
        primaryStage.setTitle("Geometric Shapes Demo");
        primaryStage.show();
    }
}