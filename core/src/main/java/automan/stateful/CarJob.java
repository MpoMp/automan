package automan.stateful;

import automan.model.JobStatus;

import java.time.Duration;

public class CarJob {
    private final String customer;
    private final String car;
    private final Duration totalTimeToComplete;

    private Duration currentProgress;
    private JobStatus currentStatus;

    public CarJob(String customer, String car, Duration totalTimeToComplete) {
        this.customer = customer;
        this.car = car;
        this.totalTimeToComplete = totalTimeToComplete;

        this.currentProgress = Duration.ZERO;
        this.currentStatus = JobStatus.WAITING;
    }

    /**
     * @return true if completed
     */
    public boolean updateProgress(Duration timeWorked) {
        currentProgress = currentProgress.plus(timeWorked);

        return currentProgress.compareTo(totalTimeToComplete) >= 0;
    }

    public float getCompletionPercentage() {
        if (currentProgress.compareTo(totalTimeToComplete) >= 0) {
            return 100f;
        }

        return ((float) currentProgress.toMinutes() / totalTimeToComplete.toMinutes()) * 100f;
    }

    public String getCustomer() {
        return customer;
    }

    public String getCar() {
        return car;
    }

    public JobStatus getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(JobStatus currentStatus) {
        this.currentStatus = currentStatus;
    }

    @Override
    public String toString() {
        return "CarJob{" +
                "customer='" + customer + '\'' +
                ", car='" + car + '\'' +
                ", totalTimeToComplete=" + totalTimeToComplete +
                ", currentProgress=" + currentProgress +
                ", currentStatus=" + currentStatus +
                '}';
    }
}
