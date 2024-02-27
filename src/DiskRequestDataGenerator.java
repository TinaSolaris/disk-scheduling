import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class DiskRequestDataGenerator {
    private final int maxItems;
    private final int minTrackNo = 0;

    public DiskRequestDataGenerator(int maxItems) {
        if (maxItems <= 0)
            throw new IllegalArgumentException("The maxItems should be positive");
        if (maxItems > Helpers.DISK_CAPACITY)
            throw new IllegalArgumentException("The maxItems cannot be larger than the disk capacity: " + Helpers.DISK_CAPACITY);

        this.maxItems = maxItems;
    }

    public void writeToFile(String filePath) throws IOException {
        if (filePath == null || filePath.isBlank())
            throw new IllegalArgumentException("The filePath should not be null, empty, or consist of white-space characters only");

        FileWriter writer = new FileWriter(filePath);
        writer.write("request_no track_pos");

        ArrayList<Integer> uniqueTrackNumbers = new ArrayList<>(maxItems);
        int trackNumber = generateRandom(minTrackNo, Helpers.DISK_CAPACITY - 1);

        for (int i = 1; i <= maxItems; i++) {
            while (uniqueTrackNumbers.contains(trackNumber)) {
                trackNumber = generateRandom(minTrackNo, Helpers.DISK_CAPACITY - 1);
            }
            uniqueTrackNumbers.add(trackNumber);
            writer.write("\nr" + i + " " + trackNumber);
        }

        writer.close();
    }

    private int generateRandom(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }

    private static final String ACTUAL_FILE = "D:\\Valentina\\PWr\\Second Semester\\OS\\Laboratories\\OSLab2\\TestData\\Actual_data_100.txt";

    public static void main(String[] args) {
        try {
            var generator = new DiskRequestDataGenerator(100);
            generator.writeToFile(ACTUAL_FILE);
        }
        catch (Exception ex) {
            System.out.println(ex);
        }
    }
}
