package automan;

import automan.handlers.MessageHandler;
import automan.handlers.GarageHandler;
import automan.stateful.Garage;
import automan.stateful.State;
import org.tinylog.Logger;

import java.io.*;
import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Core implements Runnable {

    private static final long LOOP_TIME_MS = Duration.ofMillis(100L).toMillis();
    private static final Duration STEP_DURATION = Duration.of(1L, ChronoUnit.HOURS);

    private static final LocalTime DAY_START = LocalTime.MIDNIGHT;
    private static final LocalTime DAY_END = LocalTime.MIDNIGHT;


    private static final Set<String> VALID_INPUT = Set.of("1", "0");

    private final PrintStream out;
    private final InputStream in;
    private final GarageHandler garageHandler;
    private final MessageHandler messageHandler;

    private boolean keepRunning;

    public Core(PrintStream out, InputStream in, GarageHandler garageHandler, MessageHandler messageHandler) {
        this.out = out;
        this.in = in;
        this.garageHandler = garageHandler;
        this.messageHandler = messageHandler;

        this.keepRunning = true;
    }

    @Override
    public void run() {
        State state = new State(1, DAY_START);
        try (Scanner inputScanner = new Scanner(in)) {

            out.println("Choose a name for your garage. Leave empty for random name:");
            String userInput = inputScanner.nextLine();

            String name;
            if (userInput == null || userInput.isBlank()) {
                name = "Super Garage S.A.";
            } else {
                name = userInput;
            }

            Garage garage = new Garage(name, LocalTime.of(8, 0), LocalTime.of(18, 0),
                    1, 1, 1);

            while (keepRunning) {
                runGameLoop(inputScanner, state, garage);
            }
        }
    }

    private void runGameLoop(Scanner in, State state, Garage garage) {
        // open time check
        if (state.getCurrentTime() == garage.getOpenTime()) {
            messageHandler.printEventMessage("%s is open for the day!".formatted(garage.getGarageName()));
        }

        // currently open check
        if (garage.isOpenAtTime(state.getCurrentTime())) {
            messageHandler.printCurrentState(state, garage);

            garageHandler.handleWorkProgress(garage, STEP_DURATION);
        }

        // time progress
        state.advanceTime(STEP_DURATION);

        // end day and user input
        if (state.getCurrentTime() == DAY_END) {
            out.println("Proceed to next day (1), Exit (0):\n");

            String userInput = in.nextLine();

            if (userMenuChoiceValid(userInput)) {
                switch (userInput) {
                    case "0" -> {
                        stop();
                    }
                    case "1" -> {
                        out.println("EoD Status: "); // TODO garage print
                    }
                    default -> throw new IllegalStateException("Unexpected value: " + userInput);
                }
            }
        }

        try {
            Thread.sleep(LOOP_TIME_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


    private boolean userMenuChoiceValid(String input) {
        Logger.debug("User input: {}", input);

        if (input == null || input.isBlank()) {
            return false;
        }

        return VALID_INPUT.contains(input);
    }

    private void stop() {
        this.keepRunning = false;
    }
}
