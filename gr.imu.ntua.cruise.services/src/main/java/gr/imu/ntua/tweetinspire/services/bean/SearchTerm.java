package gr.imu.ntua.tweetinspire.services.bean;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: fotis
 * Date: 9/25/12
 * Time: 8:14 PM
 */
public class SearchTerm {


    private String source;
   // private long frequency;
    private float frequency;
    private String term;
    private String link;
    private String imagelink;
    private String videolink;
    private final List<String> data;
    private int id;


  /*  public SearchTerm(String source,int id, long frequency, String term, List<String> data){
        this.id=id;
        this.source = source;
        this.frequency = frequency;
        this.term = term;
        this.data= data;
    }*/
    
     public SearchTerm(String source,int id, float frequency, String term, String link,List<String> data){
        this.id=id;
        this.source = source;
        this.frequency = frequency;
        this.term = term;
        this.link = link;
  
        this.data= data;
    }

    public String getSource() {
        return source;
    }

    public int getId() {
        return id;
    }

   /* public long getFrequency() {
        return frequency;
    }*/
     public float getFrequency() {
        return frequency;
    }

    public String getTerm() {
        return term;
    }
    
    public String getLink() {
        return link;
    }
   
   
    public List<String> getData() {
        return data;
    }

    @Override
    public String toString() {
        return "SearchTerm{" +
                ", id=" + id +
                ", term='" + term + '\'' +
                ", link='" + link +'\'' +
                ", frequency=" + frequency +
                ", source='" + source + '\'' +
                " data=" + data +
                '}';
    }
}
