public class Patient extends Thread {

    private int id;
    private SharedResources resources;

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
            System.out.println("P" + id + " arrived.");

            resources.enterWaitingRoom(this);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
