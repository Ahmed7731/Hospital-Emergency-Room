import java.util.Queue;
import java.util.ArrayDeque;

public class SharedResources {

    // Waiting room
    private Queue<Patient> waitingRoom;
    private int waitingRoomCapacity;

    // Waiting room semaphores
    private Semaphore emptySlots;
    private Semaphore fullSlots;
    private Semaphore mutex;

    // Treatment rooms
    private Semaphore treatmentRooms;

    // Number of patients who finished treatment
    private int completedPatients = 0;


    // Constructor
    public SharedResources(int waitingRoomCapacity, int treatmentRooms) {

        this.waitingRoomCapacity = waitingRoomCapacity;

        waitingRoom = new ArrayDeque<>();

        emptySlots = new Semaphore(waitingRoomCapacity);
        fullSlots = new Semaphore(0);
        mutex = new Semaphore(1);

        this.treatmentRooms = new Semaphore(treatmentRooms);
    }


    // Patient enters the waiting room
    public void enterWaitingRoom(Patient patient) throws InterruptedException {

        emptySlots.acquire();

        try {
            mutex.acquire();
        } catch (InterruptedException e) {
            emptySlots.release();
            throw e;
        }

        try {
            waitingRoom.add(patient);
            Hospital.log("P" + patient.getIdNumber() + " entered the waiting room.");
        } finally {
            mutex.release();
        }

        fullSlots.release();
    }


    // Doctor takes a patient from the waiting room
    public Patient getPatient() throws InterruptedException {

        fullSlots.acquire();

        try {
            mutex.acquire();
        } catch (InterruptedException e) {
            fullSlots.release();
            throw e;
        }

        Patient patient;

        try {
            patient = waitingRoom.remove();
            Hospital.log("P" + patient.getIdNumber() + " left the waiting room.");
        } finally {
            mutex.release();
        }

        emptySlots.release();

        return patient;
    }


    // Doctor tries to acquire a treatment room
    public boolean tryAcquireTreatmentRoom(long maxWaitTime) throws InterruptedException {

        return treatmentRooms.acquire(maxWaitTime);
    }


    // Doctor releases a treatment room
    public void releaseTreatmentRoom() {

        treatmentRooms.release();
    }


    // Patient returns to the waiting room if no treatment room is available
    public void returnPatient(Patient patient) throws InterruptedException {

        enterWaitingRoom(patient);

        Hospital.log("P" + patient.getIdNumber() + " returned to the waiting room.");
    }


    // Record that a patient finished treatment
    public synchronized void patientTreatmentFinished() {

        completedPatients++;

        notifyAll();
    }


    // Hospital waits until all patients have been treated
    public synchronized void waitUntilAllTreated(int total) throws InterruptedException {

        while (completedPatients < total) {
            wait();
        }
    }
}