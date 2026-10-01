import java.util.Iterator;

public abstract class Person implements IPerson {
    private final int id;
    private String name;
    private String phoneNumber;
    private LinkedList<IRide> rideHistory;

    public Person(int id,String name,String phoneNumber){

        this.id=id;
        this.name=name;
        setPhoneNumber(phoneNumber);
        this.rideHistory=new LinkedList<IRide>();
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
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public void setPhoneNumber(String phoneNumber) {

        if(phoneNumber==null || phoneNumber.length()!=10){
            throw new IllegalArgumentException("Phone Number must be exactly 10 digits");
        }

        for(int i=0;i<phoneNumber.length();i++){
            if(!Character.isDigit(phoneNumber.charAt(i))){
                throw new IllegalArgumentException("Phone Number must be contain digits only");
        }}
        this.phoneNumber=phoneNumber;
    }

    @Override
    public LinkedList<IRide> getRideHistory() {
        return rideHistory;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", rideHistory=" + rideHistory +
                '}';
    }
}
