import java.util.Comparator;

public class ArrivalTimeComparator implements Comparator<EdfDiskRequestData> {
    @Override
    public int compare(EdfDiskRequestData o1, EdfDiskRequestData o2) {
        return Integer.compare(o1.getArrivalTime(), o2.getArrivalTime());
    }
}