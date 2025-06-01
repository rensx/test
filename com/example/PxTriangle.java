package com.example;

import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Polygon;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.geometry.Geometry;

public class PxTriangle extends PxPhysicsObject {
    public PxTriangle(double x, double y, double size, MassType massType, int color) {
        super(Geometry.createEquilateralTriangle(size), x, y, massType);
        this.fillColor = color;
    }
}