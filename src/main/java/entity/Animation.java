package entity;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import utils.Timer;
/**
 * Responsible for sprite animation
 */
public final class Animation {
    
    public static final int SPRITE_PIXEL_WIDTH = 64;
    public static final int SPRITE_PIXEL_HEIGTH = 64;
    public static final int SPRITE_SHEET_SIZE = SPRITE_PIXEL_WIDTH * 10;

    private Timer animationTimer;
    private int frames;
    private int frameIndex;
    private BufferedImage[] images;
    private BufferedImage currentImg;

    /**
     * Creates a new sprite animation
     * @param frameMs how long one frame is in milliseconds
     * @param images the sprite images to be used in the animation
     */
    public Animation(int frameMs, BufferedImage[] images) {
        this.animationTimer = new Timer(frameMs);
        this.images = images;
        this.frames = images.length;
    }

    public void draw(Graphics2D g, int x, int y, int width, int height) {
        g.drawImage(currentImg, x, y, width, height, null);
        if(animationTimer.tick()) {
            nextFrame();
        }
    }

    /**
     * Crop the animation sprites from a sprite sheet
     * @param image the sprite sheet to be used
     * @param count the number of individual sprites to be cropped
     * @return an array of the cropped sprites
     */
    public static BufferedImage[] cropSprites(BufferedImage image, int count) {
        BufferedImage[] croppedSprites = new BufferedImage[count];
        int currentX = 0;
        int currentY = 0;
        for(int i = 0; i < count; i++) {
            if(currentX > SPRITE_PIXEL_WIDTH) {
                currentY += SPRITE_PIXEL_HEIGTH;
                currentX = 0;
            }
            if(currentY > SPRITE_PIXEL_HEIGTH)
                break;
            croppedSprites[i] = image.getSubimage(currentX, currentY, SPRITE_PIXEL_WIDTH, SPRITE_PIXEL_HEIGTH);
            currentX += SPRITE_PIXEL_WIDTH;
        }
        return croppedSprites;
    }

    /**
     * Advance the animation forward by one frame
    */
    private void nextFrame() {
        for(int i = 0; i < frames; i++) {
            if(frameIndex == i)
                currentImg = images[i];
        }
        frameIndex++;
        if(frameIndex > frames)
            frameIndex = 0;
    }
}
