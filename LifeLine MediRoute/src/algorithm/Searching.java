package algorithm;

import model.Hospital;
import model.DiseaseCategory;

import java.util.ArrayList;
import java.util.List;

public class Searching {
    public static List<Hospital> searchHospitalsByDisease(List<Hospital> hospitals, DiseaseCategory disease) {
        List<Hospital> result = new ArrayList<>();
        for (Hospital hospital : hospitals) {
            if (hospital.treats(disease)) {
                result.add(hospital);
            }
        }
        return result;
    }
}