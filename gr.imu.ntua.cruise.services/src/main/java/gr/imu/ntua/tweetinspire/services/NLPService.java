/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.imu.ntua.tweetinspire.services;

import edu.stanford.nlp.pipeline.Annotation;

/**
 *
 * @author imu-user
 */
public interface NLPService {
    
  void pipeAnnotate (Annotation document);  
    
}
