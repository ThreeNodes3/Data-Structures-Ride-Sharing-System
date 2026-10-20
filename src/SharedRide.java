public class SharedRide extends Ride implements ISharedRide {

    private LinkedList<IRider> participants;

    public SharedRide(int rideId, String pickupLocation, IDateTime pickupTime,
                      IDateTime dropoffTime, String dropoffLocation,
                      IDriver driver) {

        super(rideId, pickupLocation, pickupTime, dropoffTime, dropoffLocation, driver);
        participants = new LinkedList<IRider>();
    }

    @Override
    public LinkedList<IRider> getParticipants() {
        return participants;
    }

    @Override
    public boolean addParticipant(IRider rider) {

        if (rider == null) {
            return false;
        }

        if (hasRider(rider.getId())) {
            return false;
        }

        if (participants.empty()) {
            participants.insert(rider);
        }
        else {
            participants.findFirst();

            while (!participants.last()) {
                participants.findNext();
            }

            participants.insert(rider);
        }

        return true;
    }

    @Override
    public boolean hasRider(int riderId) {

        if (participants.empty()) {
            return false;
        }

        participants.findFirst();

        while (true) {

            if (participants.retrieve().getId() == riderId) {
                return true;
            }

            if (participants.last()) {
                break;
            }

            participants.findNext();
        }

        return false;
    }

    @Override
    public boolean removeParticipantById(int riderId) {

        if (participants.empty()) {
            return false;
        }

        participants.findFirst();

        while (true) {

            if (participants.retrieve().getId() == riderId) {
                participants.remove();
                return true;
            }

            if (participants.last()) {
                break;
            }

            participants.findNext();
        }

        return false;
    }

    @Override
    public boolean isEmpty() {
        return participants.empty();
    }
  @Override
public String toString() {
    String riderIDs = "";

    if (!Participants.empty()) {
        Participants.findFirst();

        while (!Participants.last()) {
            riderIDs += Participants.retrieve().getId() + ";";
            Participants.findNext();
        }

        riderIDs += Participants.retrieve().getId();
    }

    return "SHARED," + super.toString() + "," + riderIDs;
}  
}