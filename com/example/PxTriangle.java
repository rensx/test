package com.example;

import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Polygon;
import org.dyn4j.geometry.Vector2;

public class PxTriangle extends PxPhysicsObject {
    public PxTriangle(double x, double y, double sideLength, MassType massType, int fillColor) {
        super(createTriangleShape(sideLength), x, y, massType);
        this.setFillColor(fillColor);
        this.setEnableStroke(true);
        this.setStrokeColor(0xFF000000);
        this.setStrokeWeight(0.01f);
    }

    private static Polygon createTriangleShape(double sideLength) {
        double h = sideLength * Math.sqrt(3) / 2; // Height
        Vector2[] vertices = {
            new Vector2(0, h * 2 / 3), // Top
            new Vector2(-sideLength / 2, -h / 3), // Bottom-left
            new Vector2(sideLength / 2, -h / 3) // Bottom-right
        };
        return new Polygon(vertices);
    }
}