import java.util.Comparator;

public class SeekTimeComparator implements Comparator<DiskRequestData> {
    @Override
    public int compare(DiskRequestData o1, DiskRequestData o2) {
        return Integer.compare(o1.getSeekTime(), o2.getSeekTime());
    }
}