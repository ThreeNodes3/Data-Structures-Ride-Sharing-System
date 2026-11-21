public class Driver extends Person implements IDriver {

    private final String vehiclePlate;
    private final VehicleType vehicleType;

    public Driver(int driverId, String name, String phoneNumber,
                  String vehiclePlate, VehicleType vehicleType) {

        super(driverId, name, phoneNumber);

        if (vehiclePlate == null || vehiclePlate.length() != 7) {
            throw new IllegalArgumentException(
                    "Vehicle plate must contain 7 characters."
            );
        }

        for (int i = 0; i < 3; i++) {
            char ch = vehiclePlate.charAt(i);

            if (ch < 'A' || ch > 'Z') {
                throw new IllegalArgumentException(
                        "The first 3 characters must be uppercase letters."
                );
            }
        }

        for (int i = 3; i < 7; i++) {
            char ch = vehiclePlate.charAt(i);

            if (ch < '0' || ch > '9') {
                throw new IllegalArgumentException(
                        "The last 4 characters must be digits."
                );
            }
        }

        if (vehicleType == null) {
            throw new IllegalArgumentException(
                    "Vehicle type must not be null."
            );
        }

        this.vehiclePlate = vehiclePlate;
        this.vehicleType = vehicleType;
    }

    @Override
    public String getVehiclePlate() {
        return vehiclePlate;
    }

    @Override
    public VehicleType getVehicleType() {
        return vehicleType;
    }

    @Override
    public int compareTo(IDriver other) {
        if (getId() < other.getId()) {
            return -1;
        } else if (getId() > other.getId()) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public String toString() {
        return "Driver ID: " + getId()
                + ", Name: " + getName()
                + ", Phone Number: " + getPhoneNumber()
                + ", Vehicle Plate: " + vehiclePlate
                + ", Vehicle Type: " + vehicleType;
    }
}