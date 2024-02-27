import java.util.Comparator;

public class DeadlineTimeComparator implements Comparator<EdfDiskRequestData> {
    @Override
    public int compare(EdfDiskRequestData o1, EdfDiskRequestData o2) {
        return Integer.compare(o1.getDeadlineTime(), o2.getDeadlineTime());
    }
}