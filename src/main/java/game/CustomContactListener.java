package game;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.contacts.Contact;

import entity.CollisionCategory;
import entity.Pickup;
import entity.Rocket;

public class CustomContactListener implements ContactListener{

    @Override
    public void beginContact(Contact contact) {
        int a = contact.getFixtureA().getFilterData().categoryBits;
        int b = contact.getFixtureB().getFilterData().categoryBits;
        if(a == CollisionCategory.PICK_UP.BIT && b == CollisionCategory.RACQUET.BIT) {
            Pickup p = (Pickup) contact.getFixtureA().getBody().getUserData();
            PickupGen.pickedUp(p);
        } else if(a == CollisionCategory.RACQUET.BIT && b == CollisionCategory.PICK_UP.BIT) {
            Pickup p = (Pickup) contact.getFixtureB().getBody().getUserData();
            PickupGen.pickedUp(p);
        }

        if(a == CollisionCategory.ROCKET.BIT && b == CollisionCategory.TILE.BIT) {
            Rocket r = (Rocket) contact.getFixtureA().getBody().getUserData();
            r.explode();
            
        } else if(a == CollisionCategory.TILE.BIT && b == CollisionCategory.ROCKET.BIT) {
            Rocket r = (Rocket) contact.getFixtureB().getBody().getUserData();
            r.explode();
        }
        
            
    }
       

    @Override
    public void endContact(Contact contact) {
        
        
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        
        
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        
        
    }
    
}
