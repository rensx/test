package com.example;

import org.dyn4j.world.World;
import org.dyn4j.dynamics.Body;

public class PxPhysicsWorld {
    private final World world = new World();

    public PxPhysicsWorld() {
        world.setGravity(0, -9.81); // 默认重力，Y向下
    }

    public void step(double dt) {
        int bodyCount = world.getBodyCount();
        //System.out.printf("PhysicsWorld step: dt=%.4f, bodies=%d%n", dt, bodyCount);
        world.update(dt);
    }

    public void addBody(Body body) {
        world.addBody(body);
    }

    public void add(PxPhysicsObject... objs) {
        for (PxPhysicsObject obj : objs) {
            world.addBody(obj.getBody());
        }
    }

    public void removeBody(Body body) {
        world.removeBody(body);
    }

    public World getWorld() {
        return world;
    }
}