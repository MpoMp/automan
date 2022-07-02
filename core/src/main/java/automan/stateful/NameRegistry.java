package automan.stateful;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Stream;

public class NameRegistry {

    private final List<String> femaleNames;
    private final List<String> maleNames;
    private final List<String> lastNames;
    private final List<String> carNames;
    private final Random rng;

    public NameRegistry() throws URISyntaxException, IOException {
        this.rng = new Random();

        this.femaleNames = loadFromFile("femnames");
        this.maleNames = loadFromFile("malnames");
        this.lastNames = loadFromFile("lastnames");
        this.carNames = loadFromFile("carnames");
    }

    public String getRandomFullName() {
        int chosenIdx;
        String firstName;

        if (rng.nextInt() % 2 == 0) {
            chosenIdx = rng.nextInt(0, femaleNames.size());
            firstName = femaleNames.get(chosenIdx);
        } else {
            chosenIdx = rng.nextInt(0, maleNames.size());
            firstName = maleNames.get(chosenIdx);
        }

        chosenIdx = rng.nextInt(0, lastNames.size());
        return firstName + " " + lastNames.get(chosenIdx);
    }

    public String getRandomCarName() {
        return carNames.get(rng.nextInt(0, carNames.size()));
    }

    private List<String> loadFromFile(String sourceFile) throws URISyntaxException, IOException {
        URL fileResource = Objects.requireNonNull(getClass().getClassLoader().getResource(sourceFile),
                "Resource could not be found for path " + sourceFile);

        Path path = Paths.get(fileResource.toURI());
        try (Stream<String> lineStream = Files.lines(path);) {
            return lineStream.toList();
        }
    }
}
