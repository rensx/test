package com.example;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Polygon;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.geometry.AABB;
import processing.core.PConstants;
import processing.core.PVector;
import processing.core.PGraphics;
import javafx.geometry.Point2D;

public abstract class PxPhysicsObject extends PxObject {
    protected final Body body;
    protected int fillColor = 0xFFCCCCCC;
    protected int strokeColor = 0xFF000000;
    protected float strokeWeight = 1.0f;
    protected boolean enableFill = true;
    protected boolean enableStroke = true;
    protected static final double DEFAULT_DENSITY = 1.0;
    protected static final double DEFAULT_FRICTION = 0.5;
    protected static final double DEFAULT_RESTITUTION = 0.2;

    public PxPhysicsObject(Convex shape, double x, double y, MassType massType) {
        this(shape, x, y, massType, DEFAULT_DENSITY, DEFAULT_FRICTION, DEFAULT_RESTITUTION);
    }

    public PxPhysicsObject(Convex shape, double x, double y, MassType massType, double restitution) {
        this(shape, x, y, massType, DEFAULT_DENSITY, DEFAULT_FRICTION, restitution);
    }

    public PxPhysicsObject(Convex shape, double x, double y, MassType massType,
                           double density, double friction, double restitution) {
        body = new Body();
        BodyFixture fixture = new BodyFixture(shape);
        fixture.setDensity(density);
        fixture.setFriction(friction);
        fixture.setRestitution(restitution);
        body.addFixture(fixture);
        body.setMass(massType);
        body.getTransform().setTranslation(x, y); // x, y in pixels
    }

    @Override
    public void draw(PGraphics g) {
        Vector2 worldTranslation = body.getTransform().getTranslation();
        double worldRotation = body.getTransform().getRotationAngle();
        float screenX = (float) worldTranslation.x; // Pixels
        float screenY = (float) worldTranslation.y; // Pixels
        g.pushMatrix();
        g.translate(screenX, screenY);
        g.rotate((float) worldRotation);
        if (enableFill) g.fill(fillColor);
        if (enableStroke) {
            g.stroke(strokeColor);
            g.strokeWeight(strokeWeight);
        } else {
            g.noStroke();
        }
        for (BodyFixture fixture : body.getFixtures()) {
            drawShape(g, fixture.getShape());
        }
        g.popMatrix();
    }

    protected void drawShape(PGraphics g, Convex shape) {
        if (shape instanceof Circle) {
            Circle c = (Circle) shape;
            Vector2 localCenter = c.getCenter();
            float r = (float) c.getRadius();
            g.ellipse((float) localCenter.x, (float) localCenter.y, 2 * r, 2 * r);
        } else if (shape instanceof Rectangle) {
            Rectangle rect = (Rectangle) shape;
            float w = (float) rect.getWidth();
            float h = (float) rect.getHeight();
            g.rectMode(PConstants.CENTER);
            g.rect(0, 0, w, h);
        } else if (shape instanceof Polygon) {
            Polygon poly = (Polygon) shape;
            Vector2[] vertices = poly.getVertices();
            g.beginShape();
            for (Vector2 v : vertices) {
                g.vertex((float) v.x, (float) v.y);
            }
            g.endShape(PConstants.CLOSE);
        }
    }

    public PVector getPosition() {
        Vector2 v = body.getWorldCenter();
        return new PVector((float) v.x, (float) v.y); // Pixels
    }

    public Point2D getPositionFX() {
        Vector2 v = body.getWorldCenter();
        return new Point2D(v.x, v.y);
    }

    public float getBoundingSize() {
        AABB aabb = body.createAABB();
        double worldWidth = aabb.getWidth();
        double worldHeight = aabb.getHeight();
        return (float) Math.max(worldWidth, worldHeight);
    }
  //  public double getScale() { return scale; }
   // public void setScale(double scale) { this.scale = scale; }

    public int getFillColor() { return fillColor; }
    public void setFillColor(int fillColor) { this.fillColor = fillColor; }

    public int getStrokeColor() { return strokeColor; }
    public void setStrokeColor(int strokeColor) { this.strokeColor = strokeColor; }

    public float getStrokeWeight() { return strokeWeight; }
    public void setStrokeWeight(float strokeWeight) { this.strokeWeight = strokeWeight; }

    public boolean isFillEnabled() { return enableFill; }
    public void setEnableFill(boolean enableFill) { this.enableFill = enableFill; }

    public boolean isStrokeEnabled() { return enableStroke; }
    public void setEnableStroke(boolean enableStroke) { this.enableStroke = enableStroke; }

    public double getRestitution() {
        if (!body.getFixtures().isEmpty()) {
            return body.getFixtures().get(0).getRestitution();
        }
        return 0;
    }

    public void setRestitution(double restitution) {
        for (BodyFixture fixture : body.getFixtures()) {
            fixture.setRestitution(restitution);
        }
    }

    public double getFriction() {
        if (!body.getFixtures().isEmpty()) {
            return body.getFixtures().get(0).getFriction();
        }
        return 0;
    }

    public void setFriction(double friction) {
        for (BodyFixture fixture : body.getFixtures()) {
            fixture.setFriction(friction);
        }
    }

    public double getDensity() {
        if (!body.getFixtures().isEmpty()) {
            return body.getFixtures().get(0).getDensity();
        }
        return 0;
    }

    public void setDensity(double density) {
        for (BodyFixture fixture : body.getFixtures()) {
            fixture.setDensity(density);
        }
        if (body.getMass().getType() == MassType.NORMAL) {
            body.setMass(MassType.NORMAL);
        }
    }
    public Body getBody() {
        return body;
    }
    
}