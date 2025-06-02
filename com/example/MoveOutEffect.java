package com.example;

public class MoveOutEffect extends AnimationWithEffects {
    private final double startX, startY, endX, endY;
    private float startOpacity, endOpacity;

    public MoveOutEffect(PxObject target, double duration) {
        super(target, duration);
        this.startX = getPosition(target).x;
        this.startY = getPosition(target).y;
        this.endX = startX;
        this.endY = startY - 5; // Move 5 units below
        this.startOpacity = 1.0f;
        this.endOpacity = 0.0f;
    }

    @Override
    protected void initialize() {
        // Ensure target is in the scene
        if (!scene.getObjects().contains(target)) {
            scene.add(target);
        }
    }

    @Override
    protected void applyEffect(float t) {
        double x = lerp((float) startX, (float) endX, t);
        double y = lerp((float) startY, (float) endY, t);
        float opacity = lerp(startOpacity, endOpacity, t);
        setPosition(target, x, y);
        setOpacity(target, opacity);
    }

    @Override
    protected void finalizeEffect() {
        scene.remove(target); // Remove after moving out
    }

    @Override
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