package com.example;

import org.dyn4j.geometry.Vector2;
import processing.core.PVector;

public abstract class AnimationWithEffects extends PxAnim {
    protected final PxObject target; // Target object (e.g., PxPhysicsObject or PxSpriteObject)
    protected final double duration; // Duration of the effect in seconds
    protected double elapsedTime = 0.0;
    protected boolean started = false;

    public AnimationWithEffects(PxObject target, double duration) {
        this.target = target;
        this.duration = duration;
    }

    @Override
    public void update(double dt) {
        if (!started) {
            initialize();
            started = true;
        }
        elapsedTime += dt;
        float t = (float) Math.min(elapsedTime / duration, 1.0); // Interpolation factor [0,1]
        applyEffect(t);
        if (t >= 1.0) {
            finalizeEffect();
            markAsFinished();
        }
    }

    protected abstract void initialize(); // Setup initial state
    protected abstract void applyEffect(float t); // Apply effect based on interpolation factor
    protected abstract void finalizeEffect(); // Finalize effect (set final state)

    // Linear interpolation helper
    protected float lerp(float start, float end, float t) {
        return start + (end - start) * t;
    }

    // Helper to get position
    protected Vector2 getPosition(PxObject obj) {
        if (obj instanceof PxPhysicsObject) {
            return ((PxPhysicsObject) obj).getBody().getTransform().getTranslation();
        } else if (obj instanceof PxSpriteObject) {
            PVector pos = ((PxSpriteObject) obj).getPosition();
            return new Vector2(pos.x, pos.y);
        }
        return new Vector2(0, 0);
    }

    // Helper to set position
    protected void setPosition(PxObject obj, double x, double y) {
        if (obj instanceof PxPhysicsObject) {
            PxPhysicsObject physObj = (PxPhysicsObject) obj;
            physObj.getBody().getTransform().setTranslation(x, y);
            physObj.getBody().setLinearVelocity(0, 0); // Reset velocity
        } else if (obj instanceof PxSpriteObject) {
            ((PxSpriteObject) obj).setPosition((float) x, (float) y);
        }
    }

    // Helper to set opacity
    protected void setOpacity(PxObject obj, float opacity) {
        if (obj instanceof PxPhysicsObject) {
            int alpha = (int) (opacity * 255);
            PxPhysicsObject physObj = (PxPhysicsObject) obj;
            int fillColor = physObj.getFillColor();
            int newFillColor = (fillColor & 0x00FFFFFF) | (alpha << 24);
            physObj.setFillColor(newFillColor);
        } else if (obj instanceof PxSpriteObject) {
            ((PxSpriteObject) obj).setOpacity(opacity);
        }
    }
}