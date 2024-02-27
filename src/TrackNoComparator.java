import java.util.Comparator;

public class TrackNoComparator implements Comparator<DiskRequestData> {
    @Override
    public int compare(DiskRequestData o1, DiskRequestData o2) {
        return Integer.compare(o1.getTrackNo(), o2.getTrackNo());
    }
}