public class Rider extends Person implements IRider {

    private final String email;
    private final String homeCity;

    public Rider(
            int id,
            String name,
            String email,
            String phoneNumber,
            String homeCity) {

        super(id, name, phoneNumber);

        this.email = email;
        this.homeCity = homeCity;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getHomeCity() {
        return homeCity;
    }

    @Override
    public int compareTo(IRider other) {

        if (getId() < other.getId()) {
            return -1;
        }

        if (getId() > other.getId()) {
            return 1;
        }

        return 0;
    }

    @Override
    public String toString() {

        return getId()
                + ","
                + getName()
                + ","
                + email
                + ","
                + getPhoneNumber()
                + ","
                + homeCity;
    }
}