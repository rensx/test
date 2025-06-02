package com.example;

import org.dyn4j.geometry.Vector2;

public class ShiftEffect extends AnimationWithEffects {
    private final double startX, startY, endX, endY;

    public ShiftEffect(PxObject target, double endX, double endY, double duration) {
        super(target, duration);
        Vector2 startPos = getPosition(target);
        this.startX = startPos.x;
        this.startY = startPos.y;
        this.endX = endX;
        this.endY = endY;
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
        setPosition(target, x, y);
    }

    @Override
    protected void finalizeEffect() {
        setPosition(target, endX, endY);
    }
}