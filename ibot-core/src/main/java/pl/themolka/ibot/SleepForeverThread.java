package pl.themolka.ibot;

public class SleepForeverThread extends Thread {
    public SleepForeverThread() {
        super("sleep forever");
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(Long.MAX_VALUE);
            } catch (InterruptedException ex) {
            }
        }
    }
}
