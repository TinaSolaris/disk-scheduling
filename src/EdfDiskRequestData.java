import java.util.Objects;

public class EdfDiskRequestData extends DiskRequestData {
    private int arrivalTime; // A time of a disk request arrival
    private int deadlineTime; // A deadline time to complete
    protected int doneTimeAfterArrival; // A seek time from the previous track number to the current one or after arrival
    private String notes; // Additional notes

    public EdfDiskRequestData(EdfSourceDiskRequestData source) {
        super(source.getName(), source.getTrackNo());

        this.arrivalTime = source.getArrivalTime();
        this.deadlineTime = source.getDeadlineTime();
        this.doneTimeAfterArrival = Integer.MIN_VALUE;
    }

    public int getArrivalTime() {
        return this.arrivalTime;
    }

    public int getDeadlineTime() {
        return this.deadlineTime;
    }

    public int getDoneTimeAfterArrival() {
        return this.doneTimeAfterArrival;
    }

    public void setDoneTimeAfterArrival(int value) {
        if (value < 0)
            throw new IllegalArgumentException("The value cannot be negative.");

        this.doneTimeAfterArrival = value;
    }

    public String getNotes() {
        return this.notes;
    }

    public void setNotes(String notes) {
        if (this.notes == null)
            this.notes = "[" + notes + "]";
        else
            this.notes += " [" + notes + "]";
    }

    public boolean isPriority() {
        return this.deadlineTime > 0;
    }

    public boolean isDeadlineMet()
    {
        return isPriority() ? this.doneTimeAfterArrival <= this.deadlineTime : true;
    }

    @Override
    public boolean equals(Object ob) {
        if (this == ob)
            return true;

        if (!(ob instanceof DiskRequestData))
            return false;

        EdfDiskRequestData that = (EdfDiskRequestData)ob;
        if (this.hashCode() != that.hashCode())
            return false;

        return Objects.equals(getName(), that.getName()) &&
                Objects.equals(getTrackNo(), that.getTrackNo()) &&
                Objects.equals(getArrivalTime(), that.getArrivalTime()) &&
                Objects.equals(getDeadlineTime(), that.getDeadlineTime()) &&
                Objects.equals(getSeekTime(), that.getSeekTime()) &&
                Objects.equals(getDoneTimeAfterArrival(), that.getDoneTimeAfterArrival()) &&
                Objects.equals(getNotes(), that.getNotes());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getTrackNo(), getArrivalTime(), getDeadlineTime(), getSeekTime(), getDoneTimeAfterArrival(), getNotes());
    }

    @Override
    public String toString() {
        return "[EdfDiskRequestData]: " +
                "name: " + name +
                ", trackNo: " + trackNo +
                ", type: " + (isPriority() ? "Priority" : "Regular") +
                ", arrivalTime: " + arrivalTime +
                ", deadlineTime: " + deadlineTime +
                ", seekTime: " + seekTime +
                ", isDeadlineMet: " + isDeadlineMet() +
                ", doneTimeAfterArrival: " + doneTimeAfterArrival +
                ", notes: " + (notes != null ? notes : "");
    }
}