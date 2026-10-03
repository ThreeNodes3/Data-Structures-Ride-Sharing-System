public abstract class Ride implements IRide {

    private int rideId;
    private String pickupLocation;
    private IDateTime pickupTime;
    private IDateTime dropoffTime;
    private String dropoffLocation;
    private IDriver driver;

    public Ride(int rideId, String pickupLocation, IDateTime pickupTime,
                IDateTime dropoffTime, String dropoffLocation, IDriver driver) {

        this.rideId = rideId;
        this.pickupLocation = pickupLocation;
        this.pickupTime = pickupTime;
        this.dropoffTime = dropoffTime;
        this.dropoffLocation = dropoffLocation;
        this.driver = driver;
    }

    @Override
    public int getRideId() {
        return rideId;
    }

    @Override
    public String getPickupLocation() {
        return pickupLocation;
    }

    @Override
    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    @Override
    public IDateTime getPickupTime() {
        return pickupTime;
    }

    @Override
    public IDateTime getDropoffTime() {
        return dropoffTime;
    }

    @Override
    public String getDropoffLocation() {
        return dropoffLocation;
    }

    @Override
    public void setDropoffLocation(String dropoffLocation) {
        this.dropoffLocation = dropoffLocation;
    }

    @Override
    public IDriver getDriver() {
        return driver;
    }

    @Override
    public void setDriver(IDriver driver) {
        this.driver = driver;
    }

    @Override
    public abstract boolean hasRider(int riderId);

    @Override
    public int compareTo(IRide other) {
        return this.pickupLocation.compareTo(other.getPickupLocation());
    }
    @Override
public String toString() {
    return "Ride ID: " + rideId
            + ", Pickup Location: " + pickupLocation
            + ", Pickup Time: " + pickupTime
            + ", Drop-off Location: " + dropoffLocation
            + ", Drop-off Time: " + dropoffTime
            + ", Driver: " + driver;
}
}