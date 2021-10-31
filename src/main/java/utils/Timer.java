package utils;

public class Timer {
    
    private long now;
    private long delta;
    private long timer;
    private long lastTime;
    private int intervalMs;

    /**
     *  A timer whose tick() method returns true every intervalMs milliseconds
     * @param intervalMs the interval time in milliseconds 
     */
    public Timer(int intervalMs) {
        lastTime = System.currentTimeMillis();
        this.intervalMs = intervalMs;
    }

    /**
     * Takes one timestep per call and checks if total timesteps exceed interval period
     * @return true if accumulated timesteps exceed specified millisecond interval
     * @return false if accumulated timesteps do not exceed specified interval
     */
    public boolean tick() {
        now = System.currentTimeMillis();
        delta = now - lastTime;
        timer += delta;
        lastTime = now;
        if(timer >= intervalMs) {
            timer = 0;
            return true;
        } else {
            return false;
        }
    }

    public int getIntervalMs() {
        return intervalMs;
    }

    public void setIntervalMs(int intervalMs) {
        this.intervalMs = intervalMs;
    }

    /**
     * Sets the timer to start counting timesteps from zero again
     */
    public void reset() {
        timer = 0;
    }
    
}
