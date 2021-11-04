package entity;

public enum CollisionCategory {
    
    NO_COLLISION(0),
    TILE(1),
    BALL(2),
    BOUNDARY(3),
    PICK_UP(4),
    RACQUET(5),
    ROCKET(6),
    BUBBLE(7);

    public final int BIT;

    CollisionCategory(int bit) {
        this.BIT = bit;
    }
}
