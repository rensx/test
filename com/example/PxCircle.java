package com.example;

import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Circle;

public class PxCircle extends PxPhysicsObject {
    public PxCircle(double x, double y, double radius, MassType massType, int fillColor) {
        super(new Circle(radius), x, y, massType);
        this.setFillColor(fillColor);
        this.setEnableStroke(true);
        this.setStrokeColor(0xFF000000); // Black outline
        this.setStrokeWeight(0.01f); // Thin stroke
    }
}