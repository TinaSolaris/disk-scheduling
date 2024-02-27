import java.util.*;
import java.util.stream.Collectors;

public class EDF {
    private List<EdfDiskRequestData> sourceItems;
    private List<EdfDiskRequestData> processedItems;
    private int totalSeekTime;
    private int currentTime;

    public EDF(EdfSourceDiskRequestData[] sourceItems) {
        if (sourceItems == null)
            throw new NullPointerException("The sourceItems should not be null");

        this.sourceItems = createRequestDataList(sourceItems);
        init(this.sourceItems.size());
    }

    private void init(int size) {
        processedItems = new ArrayList<>(size);
        totalSeekTime = 0;
        currentTime = 0;
    }

    private static List<EdfDiskRequestData> createRequestDataList(EdfSourceDiskRequestData[] items) {
        var result = new ArrayList<EdfDiskRequestData>(items.length);
        for (EdfSourceDiskRequestData sourceItem : items) {
            result.add(new EdfDiskRequestData(sourceItem));
        }
        return result;
    }

    public void process() {
        init(sourceItems.size());

        if (sourceItems.isEmpty())
            return;

        EdfDiskRequestData prev = sourceItems.get(0);
        sourceItems.remove(prev);
        prev.setSeekTime(0);
        prev.setDoneTimeAfterArrival(0);
        prev.setNotes("Initial, seekTime: " + prev.getSeekTime() + ", currentTime: " + currentTime);
        processedItems.add(prev);
        int prevTrackNo = prev.getTrackNo();
        int prevTime = 0;
        Optional<EdfDiskRequestData> next;

        while (!sourceItems.isEmpty()) {
            next = getNext(prevTrackNo);

            if (next.isPresent()) {
                processHeadMoving(next.get(), prevTrackNo, prevTime);
                prev = next.get();
                prevTrackNo = prev.getTrackNo();
                prevTime = currentTime;
            }
            else
                currentTime++;
        }
    }

    private Optional<EdfDiskRequestData> getNext(int prevTrackNo) {
        if (sourceItems.isEmpty())
            return Optional.empty();

        List<EdfDiskRequestData> arrived = sourceItems.stream().filter(i -> i.getArrivalTime() <= currentTime).collect(Collectors.toList());
        if (arrived.isEmpty())
            return Optional.empty();

        EdfDiskRequestData closestItem = getClosest(arrived, prevTrackNo);

        int seekTimeToClosest = Math.abs(closestItem.trackNo - prevTrackNo);
        int maxTime = currentTime + seekTimeToClosest;
        while (currentTime <= maxTime) {
            Optional<EdfDiskRequestData> priorityItem = getPriorityItem(arrived);
            if (priorityItem.isPresent()) {
                priorityItem.get().setNotes("seekTimeToClosest: " + seekTimeToClosest + ", closest: " + closestItem.getName());
                return priorityItem;
            }

            currentTime++;
            arrived = sourceItems.stream().filter(i -> i.getArrivalTime() <= currentTime).collect(Collectors.toList());
        }

        currentTime--; // Reduce as the currentTime was increased more than needed in the cycle above
        return Optional.of(closestItem);
    }

    private EdfDiskRequestData getClosest(List<EdfDiskRequestData> arrived, int prevTrackNo) {
        EdfDiskRequestData closest = arrived.get(0);

        int minDistance = Math.abs(closest.getTrackNo() - prevTrackNo);

        for (EdfDiskRequestData item : arrived) {
            int distance = Math.abs(item.getTrackNo() - prevTrackNo);
            if (distance < minDistance) {
                minDistance = distance;
                closest = item;
            }
        }

        return closest;
    }

    private Optional<EdfDiskRequestData> getClosestRegular(int prevTrackNo) {
        if (sourceItems.isEmpty())
            return Optional.empty();

        List<EdfDiskRequestData> regular = sourceItems.stream().filter(i -> !i.isPriority()).collect(Collectors.toList());
        if (regular.isEmpty())
            return Optional.empty();

        EdfDiskRequestData closest = regular.get(0);

        int minDistance = Math.abs(closest.getTrackNo() - prevTrackNo);

        for (EdfDiskRequestData item : regular) {
            int distance = Math.abs(item.getTrackNo() - prevTrackNo);
            if (distance < minDistance) {
                minDistance = distance;
                closest = item;
            }
        }

        return Optional.of(closest);
    }

    private Optional<EdfDiskRequestData> getPriorityItem(List<EdfDiskRequestData> arrived) {
        var comparator = new DeadlineTimeComparator();
        var arrivedPriorityItem = arrived.stream().filter(i -> i.isPriority()).min(comparator);

        return arrivedPriorityItem;
    }

    private void processHeadMoving(EdfDiskRequestData item, int prevTrackNo, int prevTime) {
        sourceItems.remove(item);

        int seekTime = getSeekTime(item, prevTrackNo, prevTime);
        item.setSeekTime(seekTime);
        totalSeekTime += seekTime;
        currentTime = totalSeekTime;
        processedItems.add(item);

        // needed for debug only
        // System.out.println((processedItems.size() - 1) + " " + item + ", totalSeekSize = " + totalSeekTime);
    }

    private int getClosestSeekTime(EdfDiskRequestData item, int prevTrackNo) {
        return Math.abs(item.getTrackNo() - prevTrackNo);
    }

    private boolean isRegularExist() {
        return sourceItems.stream().filter(i -> !i.isPriority()).count() > 0;
    }

    private int getSeekTime(EdfDiskRequestData item, int prevTrackNo, int prevTime) {
        if (!item.isPriority()) {
            item.setNotes("Regular, seekTime: abs(" + prevTrackNo + " - " + item.getTrackNo() +
                    "), prevTime: " + prevTime + ", currentTime: " + currentTime);
            item.setDoneTimeAfterArrival(currentTime);
            return getClosestSeekTime(item, prevTrackNo);
        }

        int timeDelta = item.getArrivalTime() - prevTime; // A time needed for the current item to arrive after the previous item processing is completed

        if (timeDelta <= 0) {
            item.setNotes("Priority, arrived before previous priority item completed, seekTime: abs(" + prevTrackNo + " - " + item.getTrackNo() +
                    "), prevTime: " + prevTime + ", currentTime: " + currentTime);
            var seekTime = getClosestSeekTime(item, prevTrackNo);
            item.setDoneTimeAfterArrival(seekTime - timeDelta);
            return seekTime;
        }

        if (item.getArrivalTime() > totalSeekTime && !isRegularExist()) {
            item.setNotes("Priority, waited before its arrival, way: Direct; seekTime: abs(" + prevTrackNo +
                    " - " + item.getTrackNo() + ") + " + timeDelta +
                    ", prevTime: " + prevTime + ", currentTime: " + currentTime);
            var seekTime = getClosestSeekTime(item, prevTrackNo);
            item.setDoneTimeAfterArrival(seekTime);
            return seekTime + timeDelta;
        }

        var closestRegular = getClosestRegular(prevTrackNo);
        boolean isLeftToRight = closestRegular.isPresent() ? prevTrackNo < closestRegular.get().getTrackNo() : prevTrackNo < item.getTrackNo();

        int switchTrackNo;
        boolean isDirectWay;
        if (isLeftToRight) {
            switchTrackNo = prevTrackNo + timeDelta;
            isDirectWay = switchTrackNo < item.getTrackNo();
        } else {
            switchTrackNo = prevTrackNo - timeDelta;
            isDirectWay = switchTrackNo > item.getTrackNo();
        }

        String notes = "isLeftToRight: " + isLeftToRight + ", prevTrackNo: " + prevTrackNo + ", timeDelta: " + timeDelta +
                ", switchTrackNo: " + switchTrackNo +
                ", prevTime: " + prevTime + ", currentTime: " + currentTime;

        if (switchTrackNo >= Helpers.DISK_CAPACITY) {
            item.setNotes("Priority item; " + notes);
            throw new IndexOutOfBoundsException("The switchTrackNo: " + switchTrackNo +
                    " cannot be greater than the disk capacity: " + Helpers.DISK_CAPACITY +
                    " where prevTrackNo: " + prevTrackNo +
                    ", prevTime: " + prevTime +
                    ", timeDelta: " + timeDelta +
                    ", item: " + item);
        }

        if (switchTrackNo < 0) {
            item.setNotes("Priority item; " + notes);
            throw new IndexOutOfBoundsException("The switchTrackNo: " + switchTrackNo +
                    " cannot be negative where prevTrackNo: " + prevTrackNo +
                    ", prevTime: " + prevTime +
                    ", timeDelta: " + timeDelta +
                    ", item: " + item);
        }

        if (isDirectWay) { // Direct, the same way
            var seekTime = getClosestSeekTime(item, prevTrackNo);
            item.setNotes("Priority, way: Direct, " + notes);
            item.setDoneTimeAfterArrival(seekTime - timeDelta);
            return seekTime;
        } else { // Opposite way
            item.setNotes("Priority, way: Opposite, " + notes);
            item.setDoneTimeAfterArrival(Math.abs(switchTrackNo - item.getTrackNo()));
            return Math.abs(switchTrackNo - prevTrackNo) + Math.abs(switchTrackNo - item.getTrackNo());
        }
    }

    public int getTotalSeekTime() {
        return totalSeekTime;
    }

    public String getName() {
        return "EDF (SSTF inside)";
    }

    public List<EdfDiskRequestData> getProcessedItems() {
        return processedItems;
    }
}
