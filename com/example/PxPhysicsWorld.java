package com.example;

import org.dyn4j.world.World;
import org.dyn4j.dynamics.Body;

/**
 * 物理世界管理类，只负责物理模拟，不涉及绘制。
 */
public class PxPhysicsWorld {
    private final World world = new World();

    public void step(double dt) {
        world.update(dt);
    }

    public void addBody(Body body) {
        world.addBody(body);
    }
    public void add(PxPhysicsObject... objs) {
        for(PxPhysicsObject obj:objs){
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