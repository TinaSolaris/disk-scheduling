import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SourceDiskRequestData {
    private final static Pattern valueExtract = Pattern.compile("^(?<Name>\\w+)\\s(?<RequestNo>\\d+)$");

    // Class fields
    private String name; // A request name
    private int trackNo; // A disk track number

    // Constructor
    public SourceDiskRequestData(String line) throws Exception {
        if (line == null || line.isBlank())
            throw new IllegalArgumentException("The line should not be null, empty, or consist of white-space characters only");

        Matcher matcher = valueExtract.matcher(line);
        if (matcher.find()) {
            this.name = matcher.group("Name");
            this.trackNo = parseInteger(matcher.group("RequestNo"));
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

    public int getTrackNo() {
        return this.trackNo;
    }

    @Override
    public boolean equals(Object ob) {
        if (this == ob)
            return true;

        if (!(ob instanceof SourceDiskRequestData))
            return false;

        SourceDiskRequestData that = (SourceDiskRequestData)ob;
        if (this.hashCode() != that.hashCode())
            return false;

        return Objects.equals(getName(), that.getName()) &&
                Objects.equals(getTrackNo(), that.getTrackNo());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getTrackNo());
    }

    @Override
    public String toString() {
        return "[SourceDiskRequestData]: " +
                "name: " + name +
                ", trackNo: " + trackNo;
    }
}