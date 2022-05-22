package automan.stateful;

import java.time.Duration;
import java.time.LocalTime;

public class State {
    private long currentDay;
    private LocalTime currentTime;

    public State(long startDay, LocalTime startTime) {
        this.currentDay = startDay;
        this.currentTime = startTime;
    }

    public void advanceTime(Duration timeSpan) {
        currentTime = currentTime.plus(timeSpan);

        if (currentTime.equals(LocalTime.MIDNIGHT)) {
            currentDay++;
        }
    }

    public LocalTime getCurrentTime() {
        return currentTime;
    }

    public long getCurrentDay() {
        return currentDay;
    }
}
