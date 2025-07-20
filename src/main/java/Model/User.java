package Model;



public class User {
    private String name;
    private int age;
    private String fishingDay;
    private String address;
    private String contact;

    public User(String name, int age, String fishingDay, String address, String contact) {
        this.name = name;
        this.age = age;
        this.fishingDay = fishingDay;
        this.address = address;
        this.contact = contact;
    }

    // Getters & Setters

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getFishingDay() { return fishingDay; }
    public void setFishingDay(String fishingDay) { this.fishingDay = fishingDay; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
}

