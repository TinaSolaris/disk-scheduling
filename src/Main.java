import java.util.ArrayList;

public class Main {
    private static final String ACTUAL_FILE_100 = "F:\\Path\\Actual_data_100.txt";

    // EDF - Large Test File
    private static final String EDF_ACTUAL_FILE_100_100 = "F:\\Path\\EDF_Actual_data_100_100.txt";

    public static void main(String[] args) {
        try {
            ScanFile scanner = new ScanFile(ACTUAL_FILE_100);
            scanner.scan();
            SourceDiskRequestData[] items = scanner.getItems().toArray(new SourceDiskRequestData[0]);

            EdfScanFile edfScanner = new EdfScanFile(EDF_ACTUAL_FILE_100_100);
            edfScanner.scan();
            EdfSourceDiskRequestData[] edfItems = edfScanner.getItems().toArray(new EdfSourceDiskRequestData[0]);

            ArrayList<AlgorithmExecutionSummary> summaries = new ArrayList();
            summaries.add(executeAlgorithm(new FCFS(items)));
            summaries.add(executeAlgorithm(new SSTF(items)));
            summaries.add(executeAlgorithm(new SCAN(items, Helpers.DISK_CAPACITY)));
            summaries.add(executeAlgorithm(new CSCAN(items, Helpers.DISK_CAPACITY)));
            summaries.add(executeAlgorithm(new EDF(edfItems)));

            System.out.println();
            System.out.println("Summary");

            for (AlgorithmExecutionSummary summary : summaries) {
                System.out.println("DiskSchedulingAlgorithm: " + summary.name +
                        "; Total Seek Time (Distance + Waiting Time): " + summary.totalSeekTime);
            }
        }
        catch (Exception ex) {
            System.out.println(ex);
        }
    }

    private static AlgorithmExecutionSummary executeAlgorithm(DiskSchedulingAlgorithm algorithm) {
        System.out.println();
        System.out.println(algorithm.getName());
        algorithm.process();

        AlgorithmExecutionSummary result = new AlgorithmExecutionSummary();
        result.name = algorithm.getName();
        result.totalSeekTime = algorithm.getTotalSeekTime();

        System.out.println("Total Seek Time (Distance + Waiting Time): " + result.totalSeekTime);
        int index = 1;
        int totalSeekTime = 0;
        for (DiskRequestData item : algorithm.getProcessedItems()) {
            totalSeekTime += item.getSeekTime();
            System.out.println(index + ", totalSeekTime: " + totalSeekTime + ", " + item);
            index++;
        }

        return result;
    }

    private static AlgorithmExecutionSummary executeAlgorithm(EDF algorithm) {
        System.out.println();
        System.out.println(algorithm.getName());
        algorithm.process();

        AlgorithmExecutionSummary result = new AlgorithmExecutionSummary();
        result.name = algorithm.getName();
        result.totalSeekTime = algorithm.getTotalSeekTime();

        System.out.println("Total Seek Time (Distance + Waiting Time): " + result.totalSeekTime);
        int index = 1;
        int totalSeekTime = 0;
        for (DiskRequestData item : algorithm.getProcessedItems()) {
            totalSeekTime += item.getSeekTime();
            System.out.println(index + ", totalSeekTime: " + totalSeekTime + ", " + item);
            index++;
        }

        return result;
    }
}