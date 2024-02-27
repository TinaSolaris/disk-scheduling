import java.util.ArrayList;
import java.util.List;

public class Helpers {
    public static final int DISK_CAPACITY = 200;

    public static List<DiskRequestData> createRequestDataList(SourceDiskRequestData[] items) {
        if (items == null)
            throw new NullPointerException("The sourceItems should not be null");

        var result = new ArrayList<DiskRequestData>(items.length);
        for (SourceDiskRequestData sourceItem : items) {
            result.add(new DiskRequestData(sourceItem));
        }
        return result;
    }
}
