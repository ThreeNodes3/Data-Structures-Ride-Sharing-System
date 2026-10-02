public class Rider extends Person implements IRider{

    private String email;
    private  String homeCity;

    public Rider(int riderid, String name, String phoneNumber, String email, String homeCity) {
        super(riderid, name, phoneNumber);
        this.email = email;
        this.homeCity = homeCity;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getHomeCity() {
        return homeCity;
    }

    @Override
    public void setHomeCity(String homeCity) {
        this.homeCity = homeCity;
    }

    public int compareTo(IRider other){
        if(this.getId()<other.getId() )
            return -1;
        else
            if(this.getId()>other.getId())
                return 1;
            else
                return 0;
    }

    public String toString() {
        return "Rider{" +
                "email='" + email + '\'' +
                ", homeCity='" + homeCity + '\'' +
                '}';
    }
}
