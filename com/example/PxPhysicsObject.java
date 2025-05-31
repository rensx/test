package com.example;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Convex;
// import org.dyn4j.geometry.Geometry; // Utility, can be kept if other parts of your project use it
import org.dyn4j.geometry.Vector2;
import org.dyn4j.geometry.AABB;
import org.dyn4j.geometry.Circle;    // Specific shape import for instanceof
import org.dyn4j.geometry.Rectangle; // Specific shape import for instanceof
import org.dyn4j.geometry.Polygon;   // Specific shape import for instanceof
import org.dyn4j.geometry.Wound;     // Specific shape import for instanceof

import processing.core.PConstants; // For PGraphics constants like TRIANGLE_FAN, CENTER
import processing.core.PVector;
import processing.core.PGraphics;
import javafx.geometry.Point2D;

/**
 * Abstract base class for physics-enabled objects in a Processing sketch,
 * using the dyn4j physics engine.
 * It handles the creation of a dyn4j Body, its fixtures, and rendering with customizable styles.
 * Assumes a coordinate system where the physics world origin (0,0) is mapped to the center of the
 * Processing canvas, and 1 physics unit = 'scale' pixels.
 */
public abstract class PxPhysicsObject extends PxObject {

    protected final Body body;
    protected double scale = 100.0; // Default scale: 1 physics unit (e.g., meter) = 100 pixels

    // Drawing properties
    protected int fillColor = 0xFFCCCCCC;     // Default: light gray
    protected int strokeColor = 0xFF000000;   // Default: black
    protected float strokeWeight = 1.0f;
    protected boolean enableFill = true;
    protected boolean enableStroke = true;

    // Default physics material properties
    protected static final double DEFAULT_DENSITY = 1.0;
    protected static final double DEFAULT_FRICTION = 0.5;
    protected static final double DEFAULT_RESTITUTION = 0.2; // Slight bounciness

    /**
     * Simplified constructor using default physics material properties.
     *
     * @param shape The convex shape of the physics body.
     * @param x The initial x-coordinate in physics world units.
     * @param y The initial y-coordinate in physics world units.
     * @param massType The MassType of the body (e.g., NORMAL, INFINITE, FIXED).
     */
    public PxPhysicsObject(Convex shape, double x, double y, MassType massType) {
        this(shape, x, y, massType, DEFAULT_DENSITY, DEFAULT_FRICTION, DEFAULT_RESTITUTION);
    }

    /**
     * Constructor with specified restitution, using default density and friction.
     *
     * @param shape The convex shape of the physics body.
     * @param x The initial x-coordinate in physics world units.
     * @param y The initial y-coordinate in physics world units.
     * @param massType The MassType of the body.
     * @param restitution The restitution (bounciness) of the body's fixture.
     */
    public PxPhysicsObject(Convex shape, double x, double y, MassType massType, double restitution) {
        this(shape, x, y, massType, DEFAULT_DENSITY, DEFAULT_FRICTION, restitution);
    }

    /**
     * Full constructor allowing specification of common physics material properties for the body's fixture.
     *
     * @param shape The convex shape of the physics body.
     * @param x The initial x-coordinate in physics world units.
     * @param y The initial y-coordinate in physics world units.
     * @param massType The MassType of the body.
     * @param density The density of the body's fixture. Used for mass calculation if MassType is NORMAL.
     * @param friction The friction coefficient of the body's fixture.
     * @param restitution The restitution (bounciness) of the body's fixture.
     */
    public PxPhysicsObject(Convex shape, double x, double y, MassType massType,
                           double density, double friction, double restitution) {
        body = new Body();
        BodyFixture fixture = new BodyFixture(shape);
        fixture.setDensity(density);
        fixture.setFriction(friction);
        fixture.setRestitution(restitution);
        // fixture.setFilter(...); // For advanced collision filtering if needed

        body.addFixture(fixture);
        body.setMass(massType); // If MassType.NORMAL, mass is computed from density and shape.
                                // For other types, it sets the type (e.g. INFINITE for static).
        body.getTransform().setTranslation(x, y);
        // To set initial rotation: body.getTransform().setRotation(angleInRadians);
    }

    /**
     * @return The underlying dyn4j {@link Body} instance.
     */
    public Body getBody() {
        return body;
    }

    /**
     * Returns this instance of PxPhysicsObject.
     * May be used in a fluent API or for consistency within the PxObject hierarchy.
     * @return this PxPhysicsObject instance.
     */
    public PxPhysicsObject get() {
        return this;
    }

    /**
     * Abstract update method from PxObject.
     * Physics updates for the {@link Body} are typically handled by {@code World.update()} in dyn4j.
     * This method can be overridden in subclasses for game logic specific to this object that
     * needs to occur each frame (e.g., reacting to state changes, animations not tied to physics).
     */
    @Override
    public void update() {
        // Object-specific non-physics update logic can go here.
    }

    /**
     * Draws the physics object on the given PGraphics canvas.
     * It maps physics world coordinates (dyn4j: Y-up) to Processing screen coordinates (Y-down).
     * The physics world origin (0,0) is mapped to the center of the canvas.
     *
     * @param g The PGraphics context to draw on.
     */
    @Override
    public void draw(PGraphics g) {
        // Get body's world transform
        Vector2 worldTranslation = body.getTransform().getTranslation();
        double worldRotation = body.getTransform().getRotationAngle(); // Radians, CCW is positive

        // Map physics world coordinates to Processing screen coordinates
        // Canvas center is (g.width / 2, g.height / 2)
        // Physics Y positive is up, Processing Y positive is down
        float screenX = (float)(g.width / 2.0 + worldTranslation.x * scale);   // CORRECTED
        float screenY = (float)(g.height / 2.0 - worldTranslation.y * scale);  // CORRECTED

        // Apply transformations
        g.pushMatrix();
        g.translate(screenX, screenY);
        g.rotate((float)(-worldRotation)); // Processing rotation: CW positive, so negate dyn4j's CCW angle

        // Apply drawing styles
        if (enableFill) {
            g.fill(fillColor);
        } else {
            g.noFill();
        }

        if (enableStroke) {
            g.stroke(strokeColor);
            g.strokeWeight(strokeWeight);
        } else {
            g.noStroke();
        }

        // Draw each fixture's shape
        for (BodyFixture fixture : body.getFixtures()) {
            Convex convexShape = fixture.getShape();
            drawShape(g, convexShape);
        }

        g.popMatrix();
    }

    /**
     * Helper method to draw a specific {@link Convex} shape.
     * Handles scaling and Y-axis inversion for local shape coordinates.
     *
     * @param g The PGraphics context.
     * @param shape The Convex shape to draw (e.g., Circle, Rectangle, Polygon).
     */
    protected void drawShape(PGraphics g, Convex shape) {
        // The PGraphics context is already translated to the body's center and rotated.
        // Shape coordinates are local to the body.
        // We need to apply 'scale' and invert Y for Processing's coordinate system.

        if (shape instanceof Circle) {
            Circle c = (Circle) shape;
            Vector2 localCenter = c.getCenter(); // Center of the circle relative to body origin
            float r = (float)(c.getRadius() * scale);
            g.ellipse(
                (float)(localCenter.x * scale),
                (float)(localCenter.y * scale * -1.0), // Invert local Y
                2 * r, 2 * r
            );
        } else if (shape instanceof Rectangle) {
            Rectangle rect = (Rectangle) shape;
            // dyn4j Rectangle is defined by width/height and centered at its local (0,0)
            // which corresponds to the body's origin if not offset.
            float w = (float)(rect.getWidth() * scale);
            float h = (float)(rect.getHeight() * scale);
            g.rectMode(PConstants.CENTER); // Draw rectangle from its center
            g.rect(0, 0, w, h); // Centered at the already transformed origin
        } else if (shape instanceof Polygon) { // Also handles Wound as Wound extends Polygon
            Polygon poly = (Polygon) shape;
            Vector2[] vertices = poly.getVertices(); // Local coordinates (Y-up)
            g.beginShape();
            for (Vector2 v : vertices) {
                g.vertex(
                    (float)(v.x * scale),
                    (float)(v.y * scale * -1.0) // Invert local Y
                );
            }
            g.endShape(PConstants.CLOSE);
        }
        // Add more 'else if' blocks here to support other shapes like Segment, Capsule, etc.
    }

    /**
     * @return The position of the body's world center as a Processing PVector,
     * scaled by the `scale` factor (pixels).
     * Note: dyn4j Y-axis is up. PVector Y usually interpreted as down in Processing.
     * This returns (worldX * scale, worldY * scale).
     */
    public PVector getPosition() {
        Vector2 v = body.getWorldCenter(); // In physics units
        return new PVector((float)(v.x * scale), (float)(v.y * scale));
    }

    /**
     * @return The position of the body's world center as a JavaFX Point2D, in physics units.
     */
    public Point2D getPositionFX() {
        Vector2 v = body.getWorldCenter(); // In physics units
        return new Point2D(v.x, v.y);
    }

    /**
     * Calculates the maximum dimension (width or height) of the body's
     * Axis-Aligned Bounding Box (AABB) in scaled screen units (pixels).
     *
     * @return The maximum dimension of the AABB in pixels.
     */
    public float getBoundingSize() {
        AABB aabb = body.createAABB(); // AABB is in world coordinates (physics units)
        double worldWidth = aabb.getWidth();
        double worldHeight = aabb.getHeight();
        return (float)(Math.max(worldWidth, worldHeight) * scale);
    }

    // --- Getters and Setters for Scale ---
    public double getScale() { return scale; }
    public void setScale(double scale) { this.scale = scale; }

    // --- Getters and Setters for Drawing Properties ---
    public int getFillColor() { return fillColor; }
    public void setFillColor(int fillColor) { this.fillColor = fillColor; }

    public int getStrokeColor() { return strokeColor; }
    public void setStrokeColor(int strokeColor) { this.strokeColor = strokeColor; }

    public float getStrokeWeight() { return strokeWeight; }
    public void setStrokeWeight(float strokeWeight) { this.strokeWeight = strokeWeight; }

    public boolean isFillEnabled() { return enableFill; } // Renamed from isEnableFill for standard boolean getter
    public void setEnableFill(boolean enableFill) { this.enableFill = enableFill; }

    public boolean isStrokeEnabled() { return enableStroke; } // Renamed from isEnableStroke for standard boolean getter
    public void setEnableStroke(boolean enableStroke) { this.enableStroke = enableStroke; }

    // --- Getters and Setters for Physics Properties (applied to all fixtures) ---
    public double getRestitution() {
        if (!body.getFixtures().isEmpty()) {
            return body.getFixtures().get(0).getRestitution(); // Assuming same for all
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
            return body.getFixtures().get(0).getFriction(); // Assuming same for all
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
            return body.getFixtures().get(0).getDensity(); // Assuming same for all
        }
        return 0;
    }

    public void setDensity(double density) {
        for (BodyFixture fixture : body.getFixtures()) {
            fixture.setDensity(density);
        }
        // If mass type is NORMAL, changing density requires mass recalculation
        if (body.getMass().getType() == MassType.NORMAL) {
            body.setMass(MassType.NORMAL); // This recomputes mass from fixtures
        }
    }
}