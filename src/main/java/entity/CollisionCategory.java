package entity;

public enum CollisionCategory {
    BALL(2),
    TILE(1),
    BOUNDARY(3);

    public final int BIT;

    CollisionCategory(int bit) {
        this.BIT = bit;
    }
}
