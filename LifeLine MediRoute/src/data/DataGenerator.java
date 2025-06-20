package data;

import model.DiseaseCategory;
import model.Hospital;

import java.util.*;

public class DataGenerator {

    public static List<Hospital> generateHospitals() {
        List<Hospital> list = new ArrayList<>();

        list.add(new Hospital("National Institute of Cardiovascular Diseases (NICVD)", "Karachi", "Govt", Set.of(DiseaseCategory.CARDIAC, DiseaseCategory.POLIO)));
        list.add(new Hospital("Punjab Institute of Cardiology (PIC)", "Lahore", "Govt", Set.of(DiseaseCategory.CARDIAC)));
        list.add(new Hospital("Rawalpindi Institute of Cardiology (RIC)", "Rawalpindi", "Govt", Set.of(DiseaseCategory.CARDIAC)));
        list.add(new Hospital("Aga Khan University Hospital (AKUH)", "Karachi", "Private", Set.of(DiseaseCategory.CARDIAC, DiseaseCategory.CANCER, DiseaseCategory.DIABETES, DiseaseCategory.NEUROLOGY)));

        list.add(new Hospital("Shaukat Khanum Memorial Cancer Hospital (SKMCH)", "Lahore", "Charity", Set.of(DiseaseCategory.CANCER)));
        list.add(new Hospital("Shaukat Khanum Memorial Cancer Hospital (SKMCH)", "Karachi", "Charity", Set.of(DiseaseCategory.CANCER)));
        list.add(new Hospital("INMOL Hospital (Institute of Nuclear Medicine & Oncology)", "Lahore", "Govt", Set.of(DiseaseCategory.CANCER)));
        list.add(new Hospital("NORI Hospital (Nuclear Medicine, Oncology & Radiotherapy Institute)", "Islamabad", "Govt", Set.of(DiseaseCategory.CANCER)));

        list.add(new Hospital("Pakistan Kidney & Liver Institute (PKLI)", "Lahore", "Semi-Govt", Set.of(DiseaseCategory.LIVER, DiseaseCategory.KIDNEY)));
        list.add(new Hospital("Sindh Institute of Urology & Transplantation (SIUT)", "Karachi", "Charity", Set.of(DiseaseCategory.LIVER, DiseaseCategory.KIDNEY)));
        list.add(new Hospital("Sheikh Zayed Hospital (Renal Unit)", "Lahore", "Govt", Set.of(DiseaseCategory.KIDNEY)));

        list.add(new Hospital("Baqai Institute of Diabetology & Endocrinology (BIDE)", "Karachi", "Private", Set.of(DiseaseCategory.DIABETES)));
        list.add(new Hospital("Jinnah Hospital Diabetes Centre", "Lahore", "Govt", Set.of(DiseaseCategory.DIABETES)));

        list.add(new Hospital("Jinnah Postgraduate Medical Centre (JPMC) Chest Ward", "Karachi", "Govt", Set.of(DiseaseCategory.LUNG)));
        list.add(new Hospital("Mayo Hospital TB Clinic", "Lahore", "Govt", Set.of(DiseaseCategory.LUNG)));
        list.add(new Hospital("Jinnah Hospital Pulmonology Unit", "Lahore", "Govt", Set.of(DiseaseCategory.LUNG)));

        list.add(new Hospital("National Institute of Health (NIH)", "Islamabad", "Govt", Set.of(DiseaseCategory.DENGUE, DiseaseCategory.POLIO)));
        list.add(new Hospital("Children’s Hospital Lahore", "Lahore", "Govt", Set.of(DiseaseCategory.POLIO)));

        list.add(new Hospital("Liaquat National Hospital (Neurology Dept.)", "Karachi", "Private", Set.of(DiseaseCategory.NEUROLOGY)));
        list.add(new Hospital("Pakistan Institute of Medical Sciences (PIMS)", "Islamabad", "Govt", Set.of(DiseaseCategory.NEUROLOGY)));

        list.add(new Hospital("Fountain House", "Lahore", "Charity", Set.of(DiseaseCategory.PSYCHIATRY)));
        list.add(new Hospital("Karwan-e-Hayat", "Karachi", "Charity", Set.of(DiseaseCategory.PSYCHIATRY)));
        list.add(new Hospital("Punjab Institute of Mental Health (PIMH)", "Lahore", "Govt", Set.of(DiseaseCategory.PSYCHIATRY)));

        return list;
    }

    public static Map<String, Map<String, Integer>> generateDistances() {
        Map<String, Map<String, Integer>> map = new HashMap<>();
        map.put("Patient", Map.of("Karachi", 5, "Lahore", 8, "Islamabad", 10, "Rawalpindi", 12));
        map.put("Karachi", Map.of("Patient", 5));
        map.put("Lahore", Map.of("Patient", 8));
        map.put("Islamabad", Map.of("Patient", 10));
        map.put("Rawalpindi", Map.of("Patient", 12));
        return map;
    }
    public static Map<String, Map<String, Integer>> generateDistancesWithRandomDistances() {
        Random random = new Random();
        Map<String, Map<String, Integer>> distances = new HashMap<>();

        List<String> locations = Arrays.asList(
            "Patient", "Karachi", "Lahore", "Rawalpindi", "Islamabad", "Peshawar", "Faisalabad", "Quetta"
        );

        for (String from : locations) {
            Map<String, Integer> map = new HashMap<>();
            for (String to : locations) {
                if (!from.equals(to)) {
                    map.put(to, random.nextInt(5, 60));  // random distance between 5 to 60 km
                }
            }
            distances.put(from, map);
        }

        return distances;
    }
}