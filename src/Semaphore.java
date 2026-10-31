public class Semaphore {
    private int value;

    public Semaphore(int value) {
        this.value = value;
    }

    public synchronized void acquire() throws InterruptedException {
        while (value <= 0) {
            wait();
        }
        value--;
    }

    public synchronized boolean acquire(long maxWaitTime) throws InterruptedException {
        long deadline = System.currentTimeMillis() + maxWaitTime; // maxWaitTime is in ms

        while (value <= 0) {
            long timeLeft = deadline - System.currentTimeMillis();
            if (timeLeft <= 0) {
                return false;
            }
            wait(timeLeft);
        }
        value--;
        return true;
    }

    public synchronized void release() {
        value++;
        notify();
    }
}