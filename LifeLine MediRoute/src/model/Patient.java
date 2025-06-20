package model;

public class Patient {
    private String name;
    private String disease;
    private String location;

    public Patient(String name, String disease, String location) {
        this.name = name;
        this.disease = disease;
        this.location = location;
    }

    public String getDisease() { return disease; }
    public String getLocation() { return location; }
    public String getName() { return name; }
}
