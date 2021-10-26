package game;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.contacts.Contact;

import entity.CollisionCategory;
import entity.Pickup;

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
