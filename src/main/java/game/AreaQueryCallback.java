package game;

import java.util.HashSet;

import org.jbox2d.callbacks.QueryCallback;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;

import entity.Ball;

public class AreaQueryCallback implements QueryCallback {

    private HashSet<Body> foundBodies = new HashSet<>();

    @Override
    public boolean reportFixture(Fixture fixture) {
        foundBodies.add(fixture.getBody());
        return true;
    }

    public HashSet<Body> getFoundBodies() {
        return foundBodies;
    }
    
}
