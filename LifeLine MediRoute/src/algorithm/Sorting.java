package algorithm;

import model.Hospital;

import java.util.Comparator;
import java.util.List;

public class Sorting {

    public static void sortHospitalsByDistance(List<Hospital> hospitals, Comparator<Hospital> comparator) {
        hospitals.sort(comparator);
    }
}
