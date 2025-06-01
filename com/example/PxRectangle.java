package com.example;

import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Geometry;

public class PxRectangle extends PxPhysicsObject {
    public PxRectangle(double x, double y, double width, double height, MassType massType, int color) {
        super(Geometry.createRectangle(width, height), x, y, massType);
        this.fillColor = color;
    }

    public static PxRectangle createSquare(double x, double y, double size, MassType massType, int color) {
        return new PxRectangle(x, y, size, size, massType, color);
    }
}