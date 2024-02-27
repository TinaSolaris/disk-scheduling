import java.util.*;

public class SCAN implements DiskSchedulingAlgorithm {
    private List<DiskRequestData> sourceItems;
    private List<DiskRequestData> processedItems;
    private int totalSeekTime;
    private int diskCapacity;

    public SCAN(SourceDiskRequestData[] sourceItems, int diskCapacity) {
        if (sourceItems == null)
            throw new NullPointerException("The sourceItems should not be null");
        if (diskCapacity <= 0)
            throw new IllegalArgumentException("The diskCapacity should be positive");

        this.sourceItems = Helpers.createRequestDataList(sourceItems);
        this.diskCapacity = diskCapacity;
        init(this.sourceItems.size());
    }

    private void init(int size) {
        this.processedItems = new ArrayList<>(this.sourceItems.size());
        this.totalSeekTime = 0;
    }

    public void process() {
        init(sourceItems.size());

        if (sourceItems.isEmpty())
            return;

        this.processedItems = new ArrayList<>(sourceItems.size());

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

        // Run the while loop two times: one by one scanning to the right and then to the left
        boolean rightDirection = true;
        int run = 2;
        DiskRequestData current;
        while (run-- > 0) {
            if (rightDirection) {
                for (int i = 0; i < right.size(); i++) {
                    current = right.get(i);
                    processHeadMoving(current, prev, false);
                    prev = current;
                }
                rightDirection = false;
            } else {
                for (int i = left.size() - 1; i >= 0; i--) {
                    current = left.get(i);
                    processHeadMoving(current, prev, i == left.size() - 1);
                    prev = current;
                }
                rightDirection = true;
            }
        }
    }

    private void processHeadMoving(DiskRequestData current, DiskRequestData prev, boolean isDirectionSwitch) {
        processedItems.add(current);

        int seekTime;
        if (isDirectionSwitch) {
            // capacity = 200 means track numbers from 0 to 199
            int maxTrackNo = diskCapacity - 1;
            seekTime = Math.abs(maxTrackNo - prev.getTrackNo());
            seekTime += Math.abs(maxTrackNo - current.getTrackNo());
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
        return "SCAN";
    }

    public List<DiskRequestData> getProcessedItems() {
        return processedItems;
    }
}
