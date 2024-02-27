import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EdfSourceDiskRequestData {
    private final static Pattern valueExtract = Pattern.compile("^(?<Name>\\w+)\\s(?<ArrTime>\\d+)\\s(?<RequestNo>\\d+)\\s(?<DeadlineTime>\\d+)$");

    // Class fields
    private String name; // A request name
    private int arrivalTime; // A time of a disk request arrival
    private int trackNo; // A disk track number
    public int deadlineTime; // A deadline time to complete

    // Constructor
    public EdfSourceDiskRequestData(String line) throws Exception {
        if (line == null || line.isBlank())
            throw new IllegalArgumentException("The line should not be null, empty, or consist of white-space characters only");

        Matcher matcher = valueExtract.matcher(line);
        if (matcher.find()) {
            this.name = matcher.group("Name");
            this.arrivalTime = parseInteger(matcher.group("ArrTime"));
            this.trackNo = parseInteger(matcher.group("RequestNo"));
            this.deadlineTime = parseInteger(matcher.group("DeadlineTime"));
        }
    }

    private int parseInteger(String value) throws Exception {
        try {
            return Integer.parseInt(value);
        }
        catch (Exception ex) {
            throw new Exception("The value: '" + value + "' cannot be parsed as a valid integer number");
        }
    }

    public String getName() {
        return this.name;
    }

    public int getArrivalTime() {
        return this.arrivalTime;
    }

    public int getTrackNo() {
        return this.trackNo;
    }

    public int getDeadlineTime() {
        return this.deadlineTime;
    }

    @Override
    public boolean equals(Object ob) {
        if (this == ob)
            return true;

        if (!(ob instanceof SourceDiskRequestData))
            return false;

        EdfSourceDiskRequestData that = (EdfSourceDiskRequestData)ob;
        if (this.hashCode() != that.hashCode())
            return false;

        return Objects.equals(getName(), that.getName()) &&
                Objects.equals(getArrivalTime(), that.getArrivalTime()) &&
                Objects.equals(getTrackNo(), that.getTrackNo()) &&
                Objects.equals(getDeadlineTime(), that.getDeadlineTime());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getArrivalTime(), getTrackNo(), getDeadlineTime());
    }

    @Override
    public String toString() {
        return "[EdfSourceDiskRequestData]: " +
                "name: " + name +
                ", arrivalTime: " + arrivalTime +
                ", trackNo: " + trackNo +
                ", deadlineTime: " + deadlineTime;
    }
}