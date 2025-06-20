package model;

import java.util.Set;

public class Hospital {
    private String name;
    private String location;
    private String type; // Govt, Private, etc.
    private Set<DiseaseCategory> treatments;

    public Hospital(String name, String location, String type, Set<DiseaseCategory> treatments) {
        this.name = name;
        this.location = location;
        this.type = type;
        this.treatments = treatments;
    }

    public String getName() { return name; }
    public String getLocation() { return location; }
    public String getType() { return type; }
    public Set<DiseaseCategory> getTreatments() { return treatments; }

    public boolean treats(DiseaseCategory disease) {
        return treatments.contains(disease);
    }
}