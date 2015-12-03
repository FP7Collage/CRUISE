/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.imu.ntua.tweetinspire.services;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import javax.annotation.PostConstruct;
import java.util.Properties;

/**
 *
 * @author imu-user
 */
public class NLPServiceImpl implements NLPService {
    
    public static StanfordCoreNLP pipeline;
     @PostConstruct
     public  void init() 
    {
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos");
        pipeline = new StanfordCoreNLP(props);
        //System.out.println(pipeline);
        //return pipeline;
       
    }
    
    
    public void pipeAnnotate(Annotation doc){
        pipeline.annotate(doc);
    }
            
    
}
