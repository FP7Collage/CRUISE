package gr.imu.ntua.tweetinspire.ui.controller;

/**
 * Created with IntelliJ IDEA.
 * User: fotis
 * Date: 20/08/13
 * Time: 4:33 PM
 */
public class DefaultCruiseController {


    protected String getPartial(String partial){
        return getView("_"+partial);
    }


    protected String getView(String view){

        if(view.indexOf("/") == 0){
            view = view.substring(1);
        }
        return returnPrefix()+"/"+view;

    }

    public String returnPrefix(){
        return "";
    }


}
