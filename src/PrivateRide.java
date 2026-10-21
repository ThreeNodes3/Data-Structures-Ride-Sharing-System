public class PrivateRide extends Ride implements IPrivateRide {

    private IRider rider;

    public PrivateRide(int rideId, String pickupLocation, IDateTime pickupTime,
                       IDateTime dropoffTime, String dropoffLocation,
                       IDriver driver, IRider rider) {

        super(rideId, pickupLocation, pickupTime, dropoffTime, dropoffLocation, driver);
        if (rider == null) {
    throw new IllegalArgumentException("A private ride must have exactly one rider.");
}
this.rider = rider;
    }

    @Override
    public IRider getRider() {
        return rider;
    }

    @Override
public void setRider(IRider rider) {
    if (rider == null) {
        throw new IllegalArgumentException("A private ride must have exactly one rider.");
    }
    this.rider = rider;
}

    @Override
    public boolean hasRider(int riderId) {
        return rider != null && rider.getId() == riderId;
    }
     @Override
     public String toString(){
return "Private,"+super.toString()+","+getRider().getId();

     }
}