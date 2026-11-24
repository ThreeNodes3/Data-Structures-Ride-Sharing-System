public abstract class Person implements IPerson {

    private final int id;
    private final String name;
    private String phoneNumber;

    private final IBST<ITimeSlot, IRide> schedule;

    public Person(
            int id,
            String name,
            String phoneNumber) {

        this.id = id;
        this.name = name;

        setPhoneNumber(phoneNumber);

        schedule =
                new BST<ITimeSlot, IRide>();
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public void setPhoneNumber(
            String phoneNumber) {

        if (phoneNumber == null
                || phoneNumber.length() != 10) {

            throw new IllegalArgumentException(
                    "Phone number must be exactly 10 digits."
            );
        }

        for (int i = 0;
             i < phoneNumber.length();
             i++) {

            if (!Character.isDigit(
                    phoneNumber.charAt(i))) {

                throw new IllegalArgumentException(
                        "Phone number must contain digits only."
                );
            }
        }

        this.phoneNumber = phoneNumber;
    }

    @Override
    public IBST<ITimeSlot, IRide>
    getSchedule() {

        return schedule;
    }

    @Override
    public String toString() {

        return "ID: " + id
                + ", Name: " + name
                + ", Phone Number: "
                + phoneNumber;
    }
}