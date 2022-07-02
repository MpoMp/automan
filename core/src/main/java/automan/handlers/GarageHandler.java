package automan.handlers;

import automan.model.JobStatus;
import automan.stateful.CarJob;
import automan.stateful.Garage;
import automan.stateful.NameRegistry;
import org.tinylog.Logger;

import java.time.Duration;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class GarageHandler {

    private final Random rng;
    private final MessageHandler messageHandler;
    private final NameRegistry nameRegistry;

    public GarageHandler(Random rng, MessageHandler messageHandler, NameRegistry nameRegistry) {
        this.rng = rng;
        this.messageHandler = messageHandler;
        this.nameRegistry = nameRegistry;
    }


    public void handleWorkProgress(Garage garage, Duration stepDuration) {
        deliverCompletedJobs(garage);

        CarJob incomingJob = generateIncomingJob();
        if (incomingJob != null) {
            acceptOrDeclineNewJob(garage, incomingJob);

        }

        progressExistingJobs(garage, stepDuration);
    }

    private void acceptOrDeclineNewJob(Garage garage, CarJob job) {
        if (!garage.canAcceptNewJob()) {
            messageHandler.printEventMessage("Declined a job because of no availability.");
            return;
        }

        garage.getAcceptedJobs().add(job);
        messageHandler.printEventMessage("Accepted new job from %s! We'll service their %s."
                .formatted(job.getCustomer(), job.getCar())); //TODO print time estimation
    }

    private void progressExistingJobs(Garage garage, Duration stepDuration) {
        List<CarJob> jobsInProgress = garage.getJobsInProgress();
        List<CarJob> completedJobs = garage.getCompletedJobs();

        // update WIP jobs
        for (Iterator<CarJob> iterator = jobsInProgress.iterator(); iterator.hasNext(); ) {
            CarJob wipJob = iterator.next();

            boolean done;
            if (wipJob.getCompletionPercentage() == 100f) {
                done = true;
            } else {
                done = wipJob.updateProgress(stepDuration);
            }

            if (done) {
                wipJob.setCurrentStatus(JobStatus.DONE);

                if (garage.getAvailableExitParkingSlots() > 0) {
                    iterator.remove();
                    completedJobs.add(wipJob);

                    messageHandler.printEventMessage("Finished with %s. Parking it for %s to receive it."
                            .formatted(wipJob.getCar(), wipJob.getCustomer()));
                } else {
                    messageHandler.printEventMessage("Finished with %s but exit area is full. Keeping in service slot."
                            .formatted(wipJob.getCar()));
                }
            } else {
                messageHandler.printEventMessage("Continuing with %s. Progress at %.1f%%." // TODO add name
                        .formatted(wipJob.getCar(), wipJob.getCompletionPercentage()));
            }
        }

        // start more, if any
        while (!garage.getAcceptedJobs().isEmpty() && garage.getAvailableServiceSlots() > 0) {
            Iterator<CarJob> iterator = garage.getAcceptedJobs().iterator();

            CarJob nextJob = iterator.next();
            nextJob.setCurrentStatus(JobStatus.IN_PROGRESS);
            garage.getJobsInProgress().add(nextJob);
            iterator.remove();

            messageHandler.printEventMessage("Starting work on %s.".formatted(nextJob.getCar()));
        }
    }

    private void deliverCompletedJobs(Garage garage) {
        List<CarJob> completedJobs = garage.getCompletedJobs();

        for (Iterator<CarJob> iterator = completedJobs.iterator(); iterator.hasNext(); ) {
            CarJob completed = iterator.next();
            iterator.remove();

            messageHandler.printEventMessage("Delivered %s to %s.".formatted(completed.getCar(), completed.getCustomer()));
        }
    }


    //TODO move to job provider
    private CarJob generateIncomingJob() {
        int rand = rng.nextInt(1, 10);
        CarJob incomingJob;
        String custName = nameRegistry.getRandomFullName();
        String car = nameRegistry.getRandomCarName();
        if (rand <= 2) {
            incomingJob = null;
        } else if (rand <= 7) {
            incomingJob = new CarJob(custName, car, Duration.ofMinutes(rng.nextInt(1, 59)));
        } else if (rand <= 9) {
            incomingJob = new CarJob(custName, car, Duration.ofHours(rng.nextInt(1, 8)));
        } else {
            incomingJob = new CarJob(custName, car, Duration.ofHours(rng.nextInt(8, 40)));
        }

        Logger.debug("Generated job {}", incomingJob);
        return incomingJob;
    }
}
