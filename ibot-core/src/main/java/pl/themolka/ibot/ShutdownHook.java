package pl.themolka.ibot;

public class ShutdownHook extends Thread {
    private IBot bot;

    public ShutdownHook(IBot bot) {
        super("shutdown");

        this.bot = bot;
    }

    @Override
    public void run() {
        try {
            this.bot.stop();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
