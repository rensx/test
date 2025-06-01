package com.example;

import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.Geometry;

public class PxCircle extends PxPhysicsObject {
    public PxCircle(double x, double y, double radius, MassType massType, int color) {
        super(Geometry.createCircle(radius), x, y, massType);
        this.fillColor = color;
    }
}