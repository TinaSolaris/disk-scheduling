import java.util.*;

public class DiskRequestData {
    protected String name; // A request name
    protected int trackNo; // A disk track number
    protected int seekTime; // A seek time from the previous track number to the current one; could include the waiting time to arrive from the previous one processing

    public DiskRequestData(String name, int trackNo) {
        this.name = name;
        this.trackNo = trackNo;
        this.seekTime = Integer.MIN_VALUE;
    }

    public DiskRequestData(SourceDiskRequestData source) {
        this(source.getName(), source.getTrackNo());
    }

    public String getName() {
        return this.name;
    }

    public int getTrackNo() {
        return this.trackNo;
    }

    public int getSeekTime() {
        return this.seekTime;
    }

    public void setSeekTime(int value) {
        if (value < 0)
            throw new IllegalArgumentException("The value cannot be negative.");

        this.seekTime = value;
    }

    @Override
    public boolean equals(Object ob) {
        if (this == ob)
            return true;

        if (!(ob instanceof DiskRequestData))
            return false;

        DiskRequestData that = (DiskRequestData)ob;
        if (this.hashCode() != that.hashCode())
            return false;

        return Objects.equals(getName(), that.getName()) &&
                Objects.equals(getTrackNo(), that.getTrackNo()) &&
                Objects.equals(getSeekTime(), that.getSeekTime());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getTrackNo(), getSeekTime());
    }

    @Override
    public String toString() {
        return "[DiskRequestData]: " +
                "name: " + name +
                ", trackNo: " + trackNo +
                ", seekTime: " + seekTime;
    }
}
