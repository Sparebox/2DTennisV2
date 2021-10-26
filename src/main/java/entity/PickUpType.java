package entity;

public enum PickUpType {
    ROCKET("rocket");

    public final String EFFECT;

    PickUpType(String effect) {
        this.EFFECT = effect;
    }
}
