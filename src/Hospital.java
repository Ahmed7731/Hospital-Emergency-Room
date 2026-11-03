import java.util.Scanner;

public class Hospital {

    // All classes print through this method instead of System.out.println
    public static synchronized void log(String message) {
        System.out.println(message);
    }

    // Keeps asking until the user enters a number greater than 0
    private static int readPositive(Scanner input, String message) {
        int value = 0;
        while (value <= 0) {
            System.out.print(message);
            if (input.hasNextInt()) {
                value = input.nextInt();
            } else {
                input.next();
            }
        }
        return value;
    }

    public static void main(String[] args) throws InterruptedException {
        Scanner input = new Scanner(System.in);

        int waitingRoomCapacity = readPositive(input, "Waiting room capacity: ");
        int numberOfDoctors = readPositive(input, "Number of doctors: ");
        int numberOfPatients = readPositive(input, "Number of patients: ");

        log("Patients arriving:");
        String names = "";
        for (int i = 1; i <= numberOfPatients; i++) {
            names += "Patient " + i + " ";
        }
        log(names.trim());

        // Person 4: needs constructor SharedResources(int waitingRoomCapacity, int treatmentRooms)
        SharedResources resources = new SharedResources(waitingRoomCapacity, 2);

        // Person 3: needs constructor Doctor(int id, SharedResources resources)
        Doctor[] doctors = new Doctor[numberOfDoctors];
        for (int i = 0; i < numberOfDoctors; i++) {
            doctors[i] = new Doctor(i + 1, resources);
            doctors[i].start();
        }

        Patient[] patients = new Patient[numberOfPatients];
        for (int i = 0; i < numberOfPatients; i++) {
            patients[i] = new Patient(i + 1, resources);
            patients[i].start();
        }

        for (Patient p : patients) {
            p.join();
        }

        // Person 4: needs method waitUntilAllTreated(int total) throws InterruptedException
        resources.waitUntilAllTreated(numberOfPatients);

        // Person 3: Doctor must catch InterruptedException and exit run()
        for (Doctor d : doctors) {
            d.interrupt();
        }
        for (Doctor d : doctors) {
            d.join();
        }

        log("All patients have been treated.");
        log("Simulation completed.");

        input.close();
    }
}