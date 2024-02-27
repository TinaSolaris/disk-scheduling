import java.util.List;

public interface DiskSchedulingAlgorithm {
    void process();

    int getTotalSeekTime();

    String getName();

    List<DiskRequestData> getProcessedItems();
}
