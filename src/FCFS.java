import java.util.*;

public class FCFS implements DiskSchedulingAlgorithm {
    private List<DiskRequestData> items;
    private int totalSeekTime;

    public FCFS(SourceDiskRequestData[] sourceItems) {
        if (sourceItems == null)
            throw new NullPointerException("The sourceItems should not be null");

        items = Helpers.createRequestDataList(sourceItems);
        init(items.size());
    }

    private void init(int size) {
        totalSeekTime = 0;
    }

    public void process() {
        init(items.size());

        if (items.isEmpty())
            return;

        items.get(0).setSeekTime(0);

        for (int i = 1; i < items.size(); i++) {
            processHeadMoving(items.get(i - 1), items.get(i));
        }
    }

    private void processHeadMoving(DiskRequestData prev, DiskRequestData current) {
        int seekTime = Math.abs(current.getTrackNo() - prev.getTrackNo());
        current.setSeekTime(seekTime);
        totalSeekTime += seekTime;
    }

    public int getTotalSeekTime() {
        return totalSeekTime;
    }

    public String getName() {
        return "FCFS";
    }

    public List<DiskRequestData> getProcessedItems() {
        return items;
    }
}
