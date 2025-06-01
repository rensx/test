package com.example;

import org.dyn4j.world.World;
import org.dyn4j.dynamics.Body;

public class PxPhysicsWorld {
    private final World world = new World();

    public PxPhysicsWorld() {
        world.setGravity(0, -981); // Pixels/s^2
    }

    public void step(double dt) {
        int bodyCount = world.getBodyCount();
        world.update(dt);
    }

    public synchronized void addBody(Body body) {
        world.addBody(body);
    }

    public synchronized void add(PxPhysicsObject... objs) {
        for (PxPhysicsObject obj : objs) {
            world.addBody(obj.getBody());
        }
    }

    public synchronized void removeBody(Body body) {
        world.removeBody(body);
    }

    public World getWorld() {
        return world;
    }
}