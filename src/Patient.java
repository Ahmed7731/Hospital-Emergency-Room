public class Patient extends Thread {

    private final int id;
    private final SharedResources resources;

    public Patient(int id, SharedResources resources) {
        this.id = id;
        this.resources = resources;
    }

    public int getIdNumber() {
        return id;
    }

    @Override
    public void run() {
        try {
            Hospital.log("P" + id + " arrived.");

            resources.enterWaitingRoom(this);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
