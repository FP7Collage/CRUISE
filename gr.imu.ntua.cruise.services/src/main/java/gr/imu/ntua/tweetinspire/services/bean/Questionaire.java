package gr.imu.ntua.tweetinspire.services.bean;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: fotis
 * Date: 23/05/13
 * Time: 10:12 AM
 */
public class Questionaire implements Serializable{

    boolean inspired = false;

    int task = 5;
    int variety = 5;
    int unexpected =5;
    int divergence = 5;
    int reformulate= 5 ;
    int visualization = 5;
    String other;
    String ui;



    public boolean isInspired() {
        return inspired;
    }

    public void setInspired(boolean inspired) {
        this.inspired = inspired;
    }

    public int getTask() {
        return task;
    }

    public void setTask(int task) {
        this.task = task;
    }

    public int getVariety() {
        return variety;
    }

    public void setVariety(int variety) {
        this.variety = variety;
    }

    public int getUnexpected() {
        return unexpected;
    }

    public void setUnexpected(int unexpected) {
        this.unexpected = unexpected;
    }

    public int getDivergence() {
        return divergence;
    }

    public void setDivergence(int divergence) {
        this.divergence = divergence;
    }

    public int getReformulate() {
        return reformulate;
    }

    public void setReformulate(int reformulate) {
        this.reformulate = reformulate;
    }

    public int getVisualization() {
        return visualization;
    }

    public void setVisualization(int visualization) {
        this.visualization = visualization;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    public String getUi() {
        return ui;
    }

    public void setUi(String ui) {
        this.ui = ui;
    }

    @Override
    public String toString() {
        return "Questionaire{" +
                "inspired=" + inspired +
                ", task=" + task +
                ", variety=" + variety +
                ", unexpected=" + unexpected +
                ", divergence=" + divergence +
                ", reformulate=" + reformulate +
                ", visualization=" + visualization +
                ", other='" + other + '\'' +
                ", ui='" + ui + '\'' +
                '}';
    }
}
