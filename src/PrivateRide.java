public class PrivateRide extends Ride implements IPrivateRide {

    private final IRider rider;

    public PrivateRide(int rideId, String pickupLocation,
                       String dropoffLocation, ITimeSlot timeSlot,
                       IDriver driver, IRider rider) {

        super(rideId, pickupLocation, dropoffLocation, timeSlot, driver);

        if (rider == null) {
            throw new IllegalArgumentException(
                "A private ride must have exactly one rider."
            );
        }

        this.rider = rider;
    }

    @Override
    public IRider getRider() {
        return rider;
    }

    @Override
    public boolean hasRider(int riderId) {
        return rider.getId() == riderId;
    }

    @Override
    public String toString() {
        return "Private," + super.toString() + "," + getRider().getId();
    }
}