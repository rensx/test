package com.example;

import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Rectangle;

public class PxRectangle extends PxPhysicsObject {
    public PxRectangle(double x, double y, double width, double height, MassType massType, int fillColor) {
        super(new Rectangle(width, height), x, y, massType);
        this.setFillColor(fillColor);
        this.setEnableStroke(true);
        this.setStrokeColor(0xFF000000);
        this.setStrokeWeight(0.01f);
    }

    // Square as a special case
    public static PxRectangle createSquare(double x, double y, double size, MassType massType, int fillColor) {
        return new PxRectangle(x, y, size, size, massType, fillColor);
    }
}