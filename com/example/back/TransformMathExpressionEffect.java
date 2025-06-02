package com.example;

public class TransformMathExpressionEffect extends AnimationWithEffects {
    private final float startScale, endScale;
    private final float startOpacity, endOpacity;

    public TransformMathExpressionEffect(PxLatexObject target, float endScale, float endOpacity, double duration) {
        super(target, duration);
        this.startScale = target.getScale(); // Assuming PxLatexObject has getScale
        this.endScale = endScale;
        this.startOpacity = 1.0f; // Assuming opacity is not stored; adjust if needed
        this.endOpacity = endOpacity;
    }

    @Override
    protected void initialize() {
        if (!scene.getObjects().contains(target)) {
            scene.add(target);
        }
    }

    @Override
    protected void applyEffect(float t) {
        float scale = lerp(startScale, endScale, t);
        float opacity = lerp(startOpacity, endOpacity, t);
        if (target instanceof PxLatexObject) {
            ((PxLatexObject) target).setScale(scale);
            // Set opacity if PxLatexObject supports it
        }
    }

    @Override
    protected void finalizeEffect() {
        if (target instanceof PxLatexObject) {
            ((PxLatexObject) target).setScale(endScale);
        }
    }
}