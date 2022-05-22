package automan.stateful;

import automan.model.JobStatus;

import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;

public class Garage {

    private String garageName;
    private LocalTime openTime;
    private LocalTime closeTime;
    private int totalEntryParkingSlots;
    private int totalExitParkingSlots;
    private int totalServiceSlots;

    private final LinkedList<CarJob> acceptedJobs;
    private final LinkedList<CarJob> jobsInProgress;
    private final LinkedList<CarJob> completedJobs;

    public Garage(String garageName, LocalTime openTime, LocalTime closeTime, int totalEntryParkingSlots,
                  int totalExitParkingSlots, int totalServiceSlots) {
        this.garageName = garageName;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.totalEntryParkingSlots = totalEntryParkingSlots;
        this.totalExitParkingSlots = totalExitParkingSlots;
        this.totalServiceSlots = totalServiceSlots;

        this.acceptedJobs = new LinkedList<>();
        this.jobsInProgress = new LinkedList<>();
        this.completedJobs = new LinkedList<>();
    }

    public String getGarageName() {
        return garageName;
    }

    public int getTotalEntryParkingSlots() {
        return totalEntryParkingSlots;
    }

    public int getTotalExitParkingSlots() {
        return totalExitParkingSlots;
    }

    public int getTotalServiceSlots() {
        return totalServiceSlots;
    }

    public boolean canAcceptNewJob() {
        return acceptedJobs.size() < totalEntryParkingSlots;
    }

    public int getAvailableEntryParkingSlots() {
        return totalEntryParkingSlots - acceptedJobs.size();
    }

    public int getAvailableExitParkingSlots() {
        return totalExitParkingSlots - completedJobs.size();
    }

    public int getAvailableServiceSlots() {
        return totalServiceSlots - jobsInProgress.size();
    }

    public List<CarJob> getJobsInProgress() {
        return jobsInProgress;
    }

    public List<CarJob> getAcceptedJobs() {
        return acceptedJobs;
    }

    public List<CarJob> getCompletedJobs() {
        return completedJobs;
    }

    public LocalTime getOpenTime() {
        return openTime;
    }

    public LocalTime getCloseTime() {
        return closeTime;
    }

    public boolean isOpenAtTime(LocalTime time) {
        return !time.isBefore(getOpenTime()) && !time.isAfter(getCloseTime());
    }
}
