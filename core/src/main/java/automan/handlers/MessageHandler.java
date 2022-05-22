package automan.handlers;

import automan.stateful.Garage;
import automan.stateful.State;

import java.io.PrintStream;
import java.time.format.DateTimeFormatter;

public class MessageHandler {

    private static final DateTimeFormatter TIME_FORM = DateTimeFormatter.ofPattern("HH:mm");

    private final PrintStream out;

    public MessageHandler(PrintStream out) {
        this.out = out;
    }

    public void printEventMessage(String msg) {
        out.printf("    %s%n", msg);
    }

    public void printCurrentState(State state, Garage garage) {
        String garageStatus;
        if (!garage.isOpenAtTime(state.getCurrentTime())) {
            garageStatus = "Closed.";
        } else {
            garageStatus = "  Parking slots: %d/%d, Service slots: %d/%d, Exit slots: %d/%d"
                    .formatted(garage.getAvailableEntryParkingSlots(), garage.getTotalEntryParkingSlots(),
                            garage.getAvailableServiceSlots(), garage.getTotalServiceSlots(),
                            garage.getAvailableExitParkingSlots(), garage.getTotalExitParkingSlots());
        }

        out.printf("%nD%d %s %n%s%n",
                state.getCurrentDay(),
                state.getCurrentTime().format(TIME_FORM),
                garageStatus);
    }
}
