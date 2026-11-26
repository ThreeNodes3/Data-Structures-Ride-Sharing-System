public class SharedRide extends Ride implements ISharedRide {

    private final IBST<Integer, IRider> participants;

    public SharedRide(int rideId, String pickupLocation,
                      String dropoffLocation, ITimeSlot timeSlot,
                      IDriver driver) {

        super(rideId, pickupLocation, dropoffLocation, timeSlot, driver);

        participants = new BST<Integer, IRider>();
    }

    @Override
    public LinkedList<IRider> getParticipants() {
        return participants.inOrder();
    }

    @Override
    public boolean addParticipant(IRider rider) {

        if (rider == null) {
            return false;
        }

        return participants.insert(rider.getId(), rider);
    }

    @Override
    public boolean hasRider(int riderId) {
        return participants.findKey(riderId);
    }

    @Override
    public boolean removeParticipantById(int riderId) {
        return participants.removeKey(riderId);
    }

    @Override
    public boolean isEmpty() {
        return participants.empty();
    }

    @Override
    public String toString() {

        LinkedList<IRider> riders = getParticipants();
        String riderIDs = "";

        if (!riders.empty()) {
            riders.findFirst();

            while (!riders.last()) {
                riderIDs += riders.retrieve().getId() + ";";
                riders.findNext();
            }

            riderIDs += riders.retrieve().getId();
        }

        return "SHARED," + super.toString() + "," + riderIDs;
    }
}