package gr.imu.ntua.tweetinspire.services.bean;

/**
 * Created with IntelliJ IDEA.
 * User: fotis
 * Date: 15/02/13
 * Time: 2:23 AM
 */
public class StreamStatus {
    private boolean  running;
    private String terms;


    public StreamStatus(boolean running, String terms) {
        this.running = running;

        this.terms = terms;
    }

    public StreamStatus() {
        this.running=false;
        this.terms = "";
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean state) {
        this.running = state;
    }

    public String getTerms() {
        return terms;
    }

    public void setTerms(String terms) {
        this.terms = terms;
    }
}
