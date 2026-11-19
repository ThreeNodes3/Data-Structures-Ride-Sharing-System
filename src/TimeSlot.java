public class TimeSlot implements ITimeSlot {

    private final IDateTime start;
    private final IDateTime end;

    public TimeSlot(IDateTime start, IDateTime end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException(
                    "Start and end must not be null."
            );
        }

        if (start.compareTo(end) >= 0) {
            throw new IllegalArgumentException(
                    "Start must be before end."
            );
        }

        this.start = start;
        this.end = end;
    }

    @Override
    public IDateTime getStart() {
        return start;
    }

    @Override
    public IDateTime getEnd() {
        return end;
    }

    @Override
    public int compareTo(ITimeSlot other) {
        // This slot ends before or exactly when the other starts.
        if (end.compareTo(other.getStart()) <= 0) {
            return -1;
        }

        // This slot starts after or exactly when the other ends.
        if (start.compareTo(other.getEnd()) >= 0) {
            return 1;
        }

        // The two slots overlap.
        return 0;
    }
}