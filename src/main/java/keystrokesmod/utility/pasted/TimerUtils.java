package keystrokesmod.utility.pasted;

import java.util.function.BooleanSupplier;
import java.util.function.LongSupplier;

public class TimerUtils {
    private long lastMs;

    private long time;

    private boolean checkedFinish;

    public TimerUtils(long lasts) {
        this.lastMs = lasts;
    }

    public TimerUtils() {
        lastMs = System.currentTimeMillis();
    }

    public void start() {
        reset();
        checkedFinish = false;
    }

    public boolean firstFinish() {
        return checkAndSetFinish(() -> System.currentTimeMillis() >= (time + lastMs));
    }

    public void setCooldown(long time) {
        this.lastMs = time;
    }

    public boolean hasFinished() {
        return isElapsed(time + lastMs, System::currentTimeMillis);
    }

    public boolean finished(long delay) {
        return isElapsed(time, () -> System.currentTimeMillis() - delay);
    }

    public boolean isDelayComplete(long l) {
        return isElapsed(lastMs, () -> System.currentTimeMillis() - l);
    }

    public boolean reached(long currentTime) {
        return isElapsed(time, () -> Math.max(0L, System.currentTimeMillis() - currentTime));
    }

    public void reset() {
        this.time = System.currentTimeMillis();
    }

    public long getTime() {
        return Math.max(0L, System.currentTimeMillis() - time);
    }

    public boolean getFinish(long ms) {
        return getTime() - lastMs >= ms;
    }


    public boolean hasTimeElapsed(long ms, boolean reset) {
        if (getTime() >= ms) {
            if (reset) {
                reset();
            }
            return true;
        }
        return false;
    }

    private boolean checkAndSetFinish(BooleanSupplier condition) {
        if (condition.getAsBoolean() && !checkedFinish) {
            checkedFinish = true;
            return true;
        }
        return false;
    }

    private boolean isElapsed(long targetTime, LongSupplier currentTimeSupplier) {
        return currentTimeSupplier.getAsLong() >= targetTime;
    }

    public void setTime(long ms) {
        this.time = ms;
    }
}
