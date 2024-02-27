import java.util.*;

public class CSCAN implements DiskSchedulingAlgorithm {
    private List<DiskRequestData> sourceItems;
    private List<DiskRequestData> processedItems;
    private int totalSeekTime;
    private int diskCapacity;

    public CSCAN(SourceDiskRequestData[] sourceItems, int diskCapacity) {
        if (sourceItems == null)
            throw new NullPointerException("The sourceItems should not be null");
        if (diskCapacity <= 0)
            throw new IllegalArgumentException("The diskCapacity should be positive");

        this.sourceItems = Helpers.createRequestDataList(sourceItems);
        this.diskCapacity = diskCapacity;
        init(this.sourceItems.size());
    }

    private void init(int size) {
        this.totalSeekTime = 0;
        this.processedItems = new ArrayList<>(size);
    }

    public void process() {
        init(sourceItems.size());

        if (sourceItems.isEmpty())
            return;

        ArrayList<DiskRequestData> left = new ArrayList<>();
        ArrayList<DiskRequestData> right = new ArrayList<>();

        DiskRequestData prev = sourceItems.get(0);
        prev.setSeekTime(0);
        processedItems.add(prev);

        TrackNoComparator comparator = new TrackNoComparator();
        for (DiskRequestData item : sourceItems) {
            if (comparator.compare(item, prev) < 0)
                left.add(item);
            else if (comparator.compare(item, prev) > 0)
                right.add(item);
        }

        Collections.sort(left, comparator);
        Collections.sort(right, comparator);

        // Run the loop to the right from the right list
        for (DiskRequestData item : right) {
            processHeadMoving(item, prev, false);
            prev = item;
        }

        // Run the loop to the right from the left list
        boolean isFirst = true;
        for (DiskRequestData item : left) {
            processHeadMoving(item, prev, isFirst);
            isFirst = false;
            prev = item;
        }
    }

    private void processHeadMoving(DiskRequestData current, DiskRequestData prev, boolean isDirectionSwitch) {
        processedItems.add(current);

        int seekTime;
        if (isDirectionSwitch) {
            // capacity = 200 means track numbers from 0 to 199
            int maxTrackNo = diskCapacity - 1;
            seekTime = Math.abs(maxTrackNo - prev.getTrackNo());
            seekTime += diskCapacity - 1;
            seekTime += current.getTrackNo();
        } else {
            seekTime = Math.abs(current.getTrackNo() - prev.getTrackNo());
        }
        current.setSeekTime(seekTime);
        totalSeekTime += seekTime;
    }

    public int getTotalSeekTime() {
        return totalSeekTime;
    }

    public String getName() {
        return "CSCAN";
    }

    public List<DiskRequestData> getProcessedItems() {
        return processedItems;
    }
}