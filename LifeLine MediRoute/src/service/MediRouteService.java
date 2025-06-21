package service;

import algorithm.Dijkstra;
import algorithm.Searching;
import data.DataGenerator;
import model.DiseaseCategory;
import model.GraphNode;
import model.Hospital;
import model.Patient;
import util.Timer;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class MediRouteService {

    private static final String RECORD_FILE = "patient_records.txt";

    public void run() {
        Scanner scanner = new Scanner(System.in);

        // Option to view or manage past records
        System.out.println("Do you want to view/update/delete/search patient records? (yes/no):");
        String manageOption = scanner.nextLine().trim().toLowerCase();
        if (manageOption.equals("yes")) {
            managePatientRecords(scanner);
        }

        // 1. Show valid diseases and hospital types
        System.out.println("\nValid Diseases: " + Arrays.toString(DiseaseCategory.values()));
        System.out.println("Hospital Types: GOVT, PRIVATE (GOVT includes charity hospitals)");

        // 2. Input patient data
        System.out.println("Enter patient name:");
        String name = scanner.nextLine();

        DiseaseCategory diseaseCategory = null;
        while (diseaseCategory == null) {
            System.out.println("Enter patient disease (e.g., CARDIAC, CANCER, LIVER):");
            String diseaseInput = scanner.nextLine().toUpperCase();
            try {
                diseaseCategory = DiseaseCategory.valueOf(diseaseInput);
            } catch (IllegalArgumentException e) {
                System.out.println("❌ Invalid disease. Please select a valid disease from: " + Arrays.toString(DiseaseCategory.values()));
            }
        }

        String hospitalType;
        while (true) {
            System.out.println("Preferred hospital type? (Enter GOVT, PRIVATE or ANY):");
            hospitalType = scanner.nextLine().trim().toUpperCase();

            if (!hospitalType.equals("GOVT") && !hospitalType.equals("PRIVATE") && !hospitalType.equals("ANY")) {
                System.out.println("❌ Invalid hospital type. Only GOVT, PRIVATE, or ANY allowed.");
            } else {
                break;
            }
        }

        Patient patient = new Patient(name, diseaseCategory.name(), "Patient");

        // 3. Generate hospitals and distances
        List<Hospital> hospitals = DataGenerator.generateHospitals();
        Map<String, Map<String, Integer>> rawGraph = DataGenerator.generateDistancesWithRandomDistances();

        // 4. Build graph
        Map<String, GraphNode> nodes = new HashMap<>();
        for (String nameNode : rawGraph.keySet()) {
            nodes.put(nameNode, new GraphNode(nameNode));
        }
        for (var from : rawGraph.entrySet()) {
            GraphNode fromNode = nodes.get(from.getKey());
            for (var to : from.getValue().entrySet()) {
                fromNode.addNeighbor(nodes.get(to.getKey()), to.getValue());
            }
        }

        List<Hospital> matched;
        while (true) {
            matched = Searching.searchHospitalsByDisease(hospitals, diseaseCategory);
            String finalType = hospitalType;
            matched.removeIf(h -> {
                String type = h.getType().toUpperCase();
                if (finalType.equals("PRIVATE")) return !type.equals("PRIVATE");
                if (finalType.equals("GOVT")) return type.equals("PRIVATE");
                return false;
            });

            if (!matched.isEmpty()) break;

            System.out.println("No hospital found for this disease with your preferred hospital type.");
            System.out.println("Do you want to change the hospital type? (yes/no):");
            if (scanner.nextLine().trim().toLowerCase().equals("yes")) {
                System.out.println("Enter new hospital type (GOVT, PRIVATE, ANY):");
                hospitalType = scanner.nextLine().trim().toUpperCase();
            } else {
                return;
            }
        }

        // 6. Display all matching hospitals
        System.out.println("\nMatching Hospitals:");
        for (int i = 0; i < matched.size(); i++) {
            Hospital h = matched.get(i);
            System.out.println((i + 1) + ". " + h.getName() + " (" + h.getType() + ", " + h.getLocation() + ")");
        }

        // 7. Choose option
        System.out.println("\nChoose an option:");
        System.out.println("1. Select hospital manually");
        System.out.println("2. Find nearest hospital automatically");
        String choice = scanner.nextLine();

        Hospital selectedHospital = null;

        if (choice.equals("1")) {
            System.out.print("Enter hospital number from the list: ");
            int selectedIndex;
            try {
                selectedIndex = Integer.parseInt(scanner.nextLine());
                if (selectedIndex < 1 || selectedIndex > matched.size()) {
                    System.out.println("❌ Invalid selection.");
                    return;
                }
                selectedHospital = matched.get(selectedIndex - 1);
            } catch (NumberFormatException e) {
                System.out.println("❌ Please enter a valid number.");
                return;
            }
        } else {
            Timer timer = new Timer();
            timer.start();
            Map<String, Integer> shortestPaths = Dijkstra.findShortestPaths(nodes.get("Patient"));
            long timeTaken = timer.stop();

            int minDist = Integer.MAX_VALUE;
            for (Hospital h : matched) {
                int dist = shortestPaths.getOrDefault(h.getLocation(), Integer.MAX_VALUE);
                if (dist < minDist) {
                    minDist = dist;
                    selectedHospital = h;
                }
            }

            if (selectedHospital != null) {
                System.out.println("\nRecommended Nearest Hospital: " + selectedHospital.getName());
                System.out.println("Location: " + selectedHospital.getLocation());
                System.out.println("Type: " + selectedHospital.getType());
                System.out.println("Distance: " + minDist + " km");
                System.out.println("Route calculated in: " + timeTaken + " ms");
            } else {
                System.out.println("❌ No reachable hospital found.");
            }
        }

        Timer timer = new Timer();
        timer.start();
        Map<String, Integer> shortestPaths = Dijkstra.findShortestPaths(nodes.get("Patient"));
        long timeTaken = timer.stop();
        int finalDistance = shortestPaths.getOrDefault(selectedHospital.getLocation(), new Random().nextInt(5, 50));

        System.out.println("\nSelected Hospital: " + selectedHospital.getName());
        System.out.println("Location: " + selectedHospital.getLocation());
        System.out.println("Type: " + selectedHospital.getType());
        System.out.println("Distance: " + finalDistance + " km");
        simulateAmbulanceMovement("Patient", selectedHospital.getLocation(), finalDistance);
        System.out.println("Route calculated in: " + timeTaken + " ms");

        int total = 0;
        String billBreakdown = "";

        if (selectedHospital.getType().equalsIgnoreCase("PRIVATE")) {
            Random rand = new Random();
            int consultationFee = rand.nextInt(5000, 15000);
            int testCharges = rand.nextInt(10000, 30000);
            int treatmentCharges = rand.nextInt(50000, 150000);
            total = consultationFee + testCharges + treatmentCharges;

            billBreakdown = "Consultation: Rs " + consultationFee + ", Tests: Rs " + testCharges + ", Treatment: Rs " + treatmentCharges;

            System.out.println("\n---- Estimated Bill Breakdown ----");
            System.out.println("Consultation Fee: Rs: " + consultationFee);
            System.out.println("Test Charges: Rs: " + testCharges);
            System.out.println("Treatment Charges: Rs: " + treatmentCharges);
            System.out.println("Total: Rs: " + total + " (" + formatAmountToWords(total) + ")");
        }

        savePatientRecord(name, diseaseCategory.name(), selectedHospital, total, billBreakdown);
    }

    private void simulateAmbulanceMovement(String from, String to, int distance) {
        System.out.println("\n🚑 Ambulance dispatched from " + from + " to " + to);
        System.out.println("📍 Total distance: " + distance + " km");
        System.out.println("🕒 Estimated arrival time: ~" + distance + " minutes (1 km/min speed)\n");
        System.out.println("Tracking ambulance movement...");
        System.out.println("--------------------------------------------------");

        int progress = 0;
        Random random = new Random();

        while (progress < distance) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            int step = random.nextInt(3, 6);
            int trafficDelayChance = random.nextInt(100);

            if (trafficDelayChance < 20) {
                System.out.println("🛑 Traffic delay encountered... waiting...");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            progress = Math.min(progress + step, distance);

            int percent = (progress * 100) / distance;
            int bars = percent / 5;
            String bar = "[🚑" + "-".repeat(bars) + ">" + " ".repeat(20 - bars) + "] ";

            System.out.println(bar + progress + " km completed (ETA: " + (distance - progress) + " min)");
        }

        System.out.println("\n✅ Ambulance has arrived at " + to);
        System.out.println("🏁 Total distance: " + distance + " km\n");
    }

    private void savePatientRecord(String name, String disease, Hospital hospital, int total, String breakdown) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(RECORD_FILE, true))) {
            String timestamp = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
            String record = timestamp + " | Patient: " + name + " | Disease: " + disease + " | Hospital: " + hospital.getName()
                    + " | Type: " + hospital.getType();
            if (hospital.getType().equalsIgnoreCase("PRIVATE")) {
                record += " | Bill: Rs " + total + " (" + formatAmountToWords(total) + ") | " + breakdown;
            }
            writer.write(record);
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error saving patient record: " + e.getMessage());
        }
    }

    private void displayPastRecords() {
        try (BufferedReader reader = new BufferedReader(new FileReader(RECORD_FILE))) {
            String line;
            System.out.println("\n--- Past Patient Records ---");
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.out.println("No past records found or error reading records.");
        }
    }

    private void managePatientRecords(Scanner scanner) {
        System.out.println("Options: 1. View 2. Search 3. Delete 4. Update");
        String option = scanner.nextLine().trim();
        switch (option) {
            case "1": displayPastRecords(); break;
            case "2":
                System.out.println("Enter name or disease to search:");
                String keyword = scanner.nextLine();
                try (BufferedReader reader = new BufferedReader(new FileReader(RECORD_FILE))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.toLowerCase().contains(keyword.toLowerCase())) {
                            System.out.println(line);
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Error: " + e.getMessage());
                }
                break;
            case "3":
                System.out.println("Enter patient name to delete records:");
                String deleteName = scanner.nextLine();
                File inputFile = new File(RECORD_FILE);
                File tempFile = new File("temp_records.txt");
                try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
                     BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (!line.contains(deleteName)) {
                            writer.write(line);
                            writer.newLine();
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Error: " + e.getMessage());
                    return;
                }
                inputFile.delete();
                tempFile.renameTo(inputFile);
                System.out.println("Records deleted.");
                break;
            case "4":
                System.out.println("Enter patient name to update:");
                String updateName = scanner.nextLine();
                File inFile = new File(RECORD_FILE);
                File tempFileUpdate = new File("temp_records_update.txt");
                boolean updated = false;

                try (BufferedReader reader = new BufferedReader(new FileReader(inFile));
                     BufferedWriter writer = new BufferedWriter(new FileWriter(tempFileUpdate))) {

                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.toLowerCase().contains(updateName.toLowerCase())) {
                            System.out.println("Current Record: " + line);
                            System.out.println("Enter updated disease:");
                            String newDisease = scanner.nextLine().toUpperCase();

                            line = line.replaceAll("(\\| Disease: )(.*?)( \\|)", "$1" + newDisease + "$3");
                            System.out.println("Updated Record: " + line);
                            updated = true;
                        }
                        writer.write(line);
                        writer.newLine();
                    }
                } catch (IOException e) {
                    System.out.println("Error updating record: " + e.getMessage());
                    return;
                }

                if (updated) {
                    inFile.delete();
                    tempFileUpdate.renameTo(inFile);
                    System.out.println("✅ Record updated successfully.");
                } else {
                    tempFileUpdate.delete();
                    System.out.println("❌ No matching record found to update.");
                }
                break;
            default: System.out.println("Invalid option.");
        }
    }

    private String formatAmountToWords(int number) {
        StringBuilder result = new StringBuilder();
        if (number >= 100000) {
            result.append(number / 100000).append(" Lakh");
            number %= 100000;
            if (number >= 1000) result.append(", ");
        }
        if (number >= 1000) {
            result.append((number / 1000)).append(" Thousand");
        }
        return result.toString();
    }
}
