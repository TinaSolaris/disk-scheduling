import java.util.*;

public class SSTF implements DiskSchedulingAlgorithm {
    private List<DiskRequestData> sourceItems;
    private List<DiskRequestData> processedItems;
    private int totalSeekTime;

    public SSTF(SourceDiskRequestData[] sourceItems) {
        if (sourceItems == null)
            throw new NullPointerException("The sourceItems should not be null");

        this.sourceItems = Helpers.createRequestDataList(sourceItems);
        init(this.sourceItems.size());
    }

    private void init(int size) {
        processedItems = new ArrayList<>(size);
        totalSeekTime = 0;
    }

    public void process() {
        init(sourceItems.size());

        if (sourceItems.isEmpty())
            return;

        DiskRequestData prev = sourceItems.get(0);
        sourceItems.remove(prev);
        prev.setSeekTime(0);
        processedItems.add(prev);
        int prevTrackNo = prev.getTrackNo();

        DiskRequestData next;
        while (!sourceItems.isEmpty()) {
            next = getClosest(prevTrackNo);
            processHeadMoving(next, prevTrackNo);
            prevTrackNo = next.getTrackNo();
        }
    }

    private DiskRequestData getClosest(int prevTrackNo) {
        DiskRequestData closest = sourceItems.get(0);
        int minDistance = Math.abs(closest.getTrackNo() - prevTrackNo);

        for (DiskRequestData item : sourceItems) {
            int distance = Math.abs(item.getTrackNo() - prevTrackNo);
            if (distance < minDistance) {
                minDistance = distance;
                closest = item;
            }
        }

        return closest;
    }

    private void processHeadMoving(DiskRequestData item, int prevTrackNo) {
        sourceItems.remove(item);

        int seekTime = Math.abs(item.getTrackNo() - prevTrackNo);
        item.setSeekTime(seekTime);
        totalSeekTime += seekTime;
        processedItems.add(item);
    }

    public int getTotalSeekTime() {
        return totalSeekTime;
    }

    public String getName() {
        return "SSTF";
    }

    public List<DiskRequestData> getProcessedItems() {
        return processedItems;
    }
}
