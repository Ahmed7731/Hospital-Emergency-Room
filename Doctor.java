import java.util.concurrent.ThreadLocalRandom;
public class Doctor extends Thread {
    private final int doctorId;
    private final SharedResources resources;
    public Doctor(int doctorId, SharedResources resources) {
        this.doctorId = doctorId;
        this.resources = resources;
    }
    private void say(String msg) {
        Hospital.log("Doctor " + doctorId + " " + msg);
    }
    @Override
    public void run() {
        try {
            while (!isInterrupted()) {
                Patient patient = resources.getPatient();
                String name = "P" + patient.getIdNumber();
                say("is assigned to " + name + ".");
                say("is trying to acquire a treatment room.");
                if (resources.tryAcquireTreatmentRoom(3000)) {
                    try {
                        say("acquired a treatment room.");
                        say("begins treating " + name + ".");
                        Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 3000));
                        say("finished treating " + name + ".");
                    } finally {
                        resources.releaseTreatmentRoom();
                    }
                    say("released the treatment room.");
                    say("is now available.");
                    Hospital.log(name + " left.");
                    resources.patientTreatmentFinished();
                } else {
                    say("could not acquire a treatment room.");
                    say("is sending " + name + " back to the waiting room.");
                    sendBack(patient);
                    say("is now available.");
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    private void sendBack(Patient patient) {
        Thread t = new Thread(() -> {
            try {
                resources.returnPatient(patient);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        t.setDaemon(true);
        t.start();
    }
}
