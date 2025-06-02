package com.example;

public class MoveInEffect extends AnimationWithEffects {
    private final double startX, startY, endX, endY;
    private float startOpacity, endOpacity;

    public MoveInEffect(PxObject target, double endX, double endY, double duration) {
        super(target, duration);
        this.startX = endX;
        this.startY = endY + 5; // Start 5 units above
        this.endX = endX;
        this.endY = endY;
        this.startOpacity = 0.0f;
        this.endOpacity = 1.0f;
    }

    @Override
    protected void initialize() {
        setPosition(target, startX, startY);
        setOpacity(target, startOpacity);
        if (!scene.getObjects().contains(target)) {
            scene.add(target);
            System.out.println("Added target to scene: " + target);
        }
        System.out.println("MoveInEffect initialized: startPos=(" + startX + ", " + startY + "), opacity=" + startOpacity);
    }

    @Override
    protected void applyEffect(float t) {
        double x = lerp((float) startX, (float) endX, t);
        double y = lerp((float) startY, (float) endY, t);
        float opacity = lerp(startOpacity, endOpacity, t);
        setPosition(target, x, y);
        setOpacity(target, opacity);
        System.out.println("MoveInEffect: t=" + t + ", pos=(" + x + ", " + y + "), opacity=" + opacity);
    }

    @Override
    protected void finalizeEffect() {
        setPosition(target, endX, endY);
        setOpacity(target, endOpacity);
        System.out.println("MoveInEffect finalized at pos=(" + endX + ", " + endY + "), opacity=" + endOpacity);
    }
}