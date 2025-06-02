package com.example;

import org.dyn4j.geometry.MassType;
import processing.core.PVector;
import org.dyn4j.geometry.Vector2;

public class PxSequenceAnim extends PxAnim {
    private static final float DEGREES = (float) (Math.PI / 180.0);
    private final java.util.List<Action> actions = new java.util.ArrayList<>();
    private double elapsedTime = 0.0;
    private int currentActionIndex = 0;

    private interface Action {
        void execute(PxScene scene);
        double getDelay();
    }

    private static class AddAction implements Action {
        private final PxObject shape;
        private final double delay;
        private final AnimationWithEffects effect;

        AddAction(PxObject shape, double delay, AnimationWithEffects effect) {
            this.shape = shape;
            this.delay = delay;
            this.effect = effect;
        }

        @Override
        public void execute(PxScene scene) {
            if (effect != null) {
                effect.setScene(scene);
                scene.playAnimation(effect);
            } else {
                scene.add(shape);
            }
            // Start sprite animation if applicable
            if (shape instanceof PxSpriteObject) {
                ((PxSpriteObject) shape).play(true);
            }
        }

        @Override
        public double getDelay() {
            return delay;
        }
    }

    private static class RotateAction implements Action {
        private final PxPhysicsObject shape;
        private final float angle;
        private final double delay;
        private final AnimationWithEffects effect;

        RotateAction(PxPhysicsObject shape, float angle, double delay, AnimationWithEffects effect) {
            this.shape = shape;
            this.angle = angle;
            this.delay = delay;
            this.effect = effect;
        }

        @Override
        public void execute(PxScene scene) {
            if (effect != null) {
                effect.setScene(scene);
                scene.playAnimation(effect);
            } else if (shape != null && shape.getBody() != null) {
                shape.getBody().rotate(angle);
            }
        }

        @Override
        public double getDelay() {
            return delay;
        }
    }

    private static class SetPositionAction implements Action {
        private final PxObject shape;
        private final double x;
        private final double y;
        private final double delay;
        private final AnimationWithEffects effect;

        SetPositionAction(PxObject shape, double x, double y, double delay, AnimationWithEffects effect) {
            this.shape = shape;
            this.x = x;
            this.y = y;
            this.delay = delay;
            this.effect = effect;
        }

        @Override
        public void execute(PxScene scene) {
            if (effect != null) {
                effect.setScene(scene);
                scene.playAnimation(effect);
            } else if (shape != null) {
                if (shape instanceof PxPhysicsObject && ((PxPhysicsObject) shape).getBody() != null) {
                    ((PxPhysicsObject) shape).getBody().getTransform().setTranslation(x, y);
                    ((PxPhysicsObject) shape).getBody().setLinearVelocity(0, 0);
                } else if (shape instanceof PxSpriteObject) {
                    ((PxSpriteObject) shape).setPosition((float) x, (float) y);
                }
            }
        }

        @Override
        public double getDelay() {
            return delay;
        }
    }

    public PxSequenceAnim add(PxObject shape) {
        return add(shape, 0.0, null);
    }

    public PxSequenceAnim add(PxObject shape, double delaySeconds) {
        return add(shape, delaySeconds, null);
    }

    public PxSequenceAnim addWithEffect(PxObject shape, double delaySeconds, AnimationWithEffects effect) {
        actions.add(new AddAction(shape, delaySeconds, effect));
        return this;
    }

    private PxSequenceAnim add(PxObject shape, double delaySeconds, AnimationWithEffects effect) {
        actions.add(new AddAction(shape, delaySeconds, effect));
        return this;
    }

    public PxSequenceAnim rotate(float angleDegrees, PxPhysicsObject shape) {
        return rotate(angleDegrees, shape, 0.0, null);
    }

    public PxSequenceAnim rotate(float angleDegrees, PxPhysicsObject shape, double delaySeconds) {
        return rotate(angleDegrees, shape, delaySeconds, null);
    }

    public PxSequenceAnim rotateWithEffect(float angleDegrees, PxPhysicsObject shape, double delaySeconds, AnimationWithEffects effect) {
        actions.add(new RotateAction(shape, angleDegrees * DEGREES, delaySeconds, effect));
        return this;
    }

    private PxSequenceAnim rotate(float angleDegrees, PxPhysicsObject shape, double delaySeconds, AnimationWithEffects effect) {
        actions.add(new RotateAction(shape, angleDegrees * DEGREES, delaySeconds, effect));
        return this;
    }

    public PxSequenceAnim setPosition(PxObject shape, double x, double y) {
        return setPosition(shape, x, y, 0.0, null);
    }

    public PxSequenceAnim setPosition(PxObject shape, double x, double y, double delaySeconds) {
        return setPosition(shape, x, y, delaySeconds, null);
    }

    public PxSequenceAnim setPositionWithEffect(PxObject shape, double x, double y, double delaySeconds, AnimationWithEffects effect) {
        actions.add(new SetPositionAction(shape, x, y, delaySeconds, effect));
        return this;
    }

    private PxSequenceAnim setPosition(PxObject shape, double x, double y, double delaySeconds, AnimationWithEffects effect) {
        actions.add(new SetPositionAction(shape, x, y, delaySeconds, effect));
        return this;
    }

    public PxSequenceAnim waitSeconds(double seconds) {
        if (!actions.isEmpty()) {
            Action lastAction = actions.get(actions.size() - 1);
            if (lastAction instanceof AddAction || lastAction instanceof RotateAction || lastAction instanceof SetPositionAction) {
                actions.set(actions.size() - 1, new Action() {
                    @Override
                    public void execute(PxScene scene) {
                        lastAction.execute(scene);
                    }

                    @Override
                    public double getDelay() {
                        return lastAction.getDelay() + seconds;
                    }
                });
            }
        }
        return this;
    }

    @Override
    public void update(double dt) {
        if (currentActionIndex >= actions.size()) {
            markAsFinished();
            return;
        }

        Action currentAction = actions.get(currentActionIndex);
        elapsedTime += dt;

        if (elapsedTime >= currentAction.getDelay()) {
            currentAction.execute(scene);
            currentActionIndex++;
            elapsedTime = 0.0;
        }
    }

    public static class Shape {
        public static PxRectangle square() {
            return new PxRectangle(0, 5, 0.6, 0.6, MassType.NORMAL, 0xFF00FF00);
        }

        public static PxCircle circle() {
            return new PxCircle(-1, 3, 0.3, MassType.NORMAL, 0xFFFF0000);
        }
    }
}