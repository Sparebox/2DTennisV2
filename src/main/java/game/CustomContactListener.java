package game;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.contacts.Contact;

import entity.CollisionCategory;
import entity.Pickup;
import entity.Rocket;
/**
 * Implementation for the JBox2D ContactListener which handles pickup collisions
 */
public class CustomContactListener implements ContactListener{

    private Game currentGame;
    private AudioManager audioManager;

    /**
     * Creates the contact listener for the specified game class instance
     * @param currentGame the instance of the current game 
     */
    public CustomContactListener(Game currentGame) {
        this.currentGame = currentGame;
        this.audioManager = currentGame.getAudioManager();
    }

    @Override
    public void beginContact(Contact contact) {

        int a = contact.getFixtureA().getFilterData().categoryBits;
        int b = contact.getFixtureB().getFilterData().categoryBits;
        
        if(a == CollisionCategory.PICK_UP.BIT && b == CollisionCategory.RACQUET.BIT) {
            Pickup p = (Pickup) contact.getFixtureA().getBody().getUserData();
            currentGame.getPickUpGen().setPickupToBeApplied(p);
        } else if(a == CollisionCategory.RACQUET.BIT && b == CollisionCategory.PICK_UP.BIT) {
            Pickup p = (Pickup) contact.getFixtureB().getBody().getUserData();
            currentGame.getPickUpGen().setPickupToBeApplied(p);
        }

        if(a == CollisionCategory.ROCKET.BIT && b == CollisionCategory.TILE.BIT) {
            Rocket r = (Rocket) contact.getFixtureA().getBody().getUserData();
            r.explode();
            audioManager.playSound(AudioManager.EXPLOSION);
        } else if(a == CollisionCategory.TILE.BIT && b == CollisionCategory.ROCKET.BIT) {
            Rocket r = (Rocket) contact.getFixtureB().getBody().getUserData();
            r.explode();
            audioManager.playSound(AudioManager.EXPLOSION);
        }

        if(a == CollisionCategory.BALL.BIT || b == CollisionCategory.BALL.BIT) {
            audioManager.playSound(AudioManager.BALL_HIT);
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
