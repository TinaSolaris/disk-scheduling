import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class EdfDiskRequestDataGenerator {
    private final int maxRegularItems;
    private final int maxPriorityItems;

    private final int minArrivalTime = 0;
    private final int maxArrivalTime;

    private final int minTrackNo = 0;

    private final int minDeadline = 10;
    private final int maxDeadline = 100;

    public EdfDiskRequestDataGenerator(int maxRegularItems, int maxPriorityItems, int maxArrivalTime) {
        if (maxRegularItems <= 0)
            throw new IllegalArgumentException("The maxRegularItems should be positive");
        if (maxPriorityItems <= 0)
            throw new IllegalArgumentException("The maxPriorityItems should be positive");
        if (maxArrivalTime <= 0)
            throw new IllegalArgumentException("The maxArrivalTime should be positive");
        if (maxRegularItems + maxPriorityItems > Helpers.DISK_CAPACITY)
            throw new IllegalArgumentException("The maxRegularItems + maxPriorityItems cannot be larger than the disk capacity: " + Helpers.DISK_CAPACITY);

        this.maxRegularItems = maxRegularItems;
        this.maxPriorityItems = maxRegularItems;
        this.maxArrivalTime = maxArrivalTime;
    }

    public void writeToFile(String filePath) throws IOException {
        if (filePath == null || filePath.isBlank())
            throw new IllegalArgumentException("The filePath should not be null, empty, or consist of white-space characters only");

        FileWriter writer = new FileWriter(filePath);
        writer.write("request_no arrival_time track_pos deadline_time");

        ArrayList<Integer> uniqueTrackNumbers = new ArrayList<>(maxRegularItems);
        int trackNumber = generateRandom(minTrackNo, Helpers.DISK_CAPACITY - 1);

        for (int i = 1; i <= maxRegularItems; i++) {
            while (uniqueTrackNumbers.contains(trackNumber)) {
                trackNumber = generateRandom(minTrackNo, Helpers.DISK_CAPACITY - 1);
            }
            uniqueTrackNumbers.add(trackNumber);
            writer.write("\nr" + i + " 0 " + trackNumber + " 0");
        }

        for (int i = maxRegularItems + 1; i <= maxRegularItems + maxPriorityItems; i++) {
            while (uniqueTrackNumbers.contains(trackNumber)) {
                trackNumber = generateRandom(minTrackNo, Helpers.DISK_CAPACITY - 1);
            }
            uniqueTrackNumbers.add(trackNumber);

            writer.write("\nr" + i + " " +
                    generateRandom(minArrivalTime, maxArrivalTime) + " " +
                    generateRandom(minTrackNo, Helpers.DISK_CAPACITY - 1) + " " +
                    generateRandom(minDeadline, maxDeadline));
        }

        writer.close();
    }

    private int generateRandom(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }

    private static final String ACTUAL_FILE = "F:\\Path\\EDF_Actual_data_100_100.txt";

    public static void main(String[] args) {
        try {
            var generator = new EdfDiskRequestDataGenerator(100, 100, 1000);
            generator.writeToFile(ACTUAL_FILE);
        }
        catch (Exception ex) {
            System.out.println(ex);
        }
    }
}
