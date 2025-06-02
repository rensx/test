package com.example;

import org.dyn4j.geometry.Vector2;

public class TransformEffect extends AnimationWithEffects {
    private final double startX, startY, startAngle;
    private final double endX, endY, endAngle;

    public TransformEffect(PxObject target, double endX, double endY, float endAngleDegrees, double duration) {
        super(target, duration);
        Vector2 startPos = getPosition(target);
        this.startX = startPos.x;
        this.startY = startPos.y;
        this.startAngle = (target instanceof PxPhysicsObject) ? ((PxPhysicsObject) target).getBody().getTransform().getRotationAngle() : 0;
        this.endX = endX;
        this.endY = endY;
        this.endAngle = endAngleDegrees * Math.PI / 180.0;
    }

    @Override
    protected void initialize() {
        if (!scene.getObjects().contains(target)) {
            scene.add(target);
        }
    }

    @Override
    protected void applyEffect(float t) {
        double x = lerp((float) startX, (float) endX, t);
        double y = lerp((float) startY, (float) endY, t);
        double angle = lerp((float) startAngle, (float) endAngle, t);
        setPosition(target, x, y);
        if (target instanceof PxPhysicsObject) {
            ((PxPhysicsObject) target).getBody().getTransform().setRotation(angle);
        }
    }

    @Override
    protected void finalizeEffect() {
        setPosition(target, endX, endY);
        if (target instanceof PxPhysicsObject) {
            ((PxPhysicsObject) target).getBody().getTransform().setRotation(endAngle);
        }
    }
}