package gr.imu.ntua.tweetinspire.services;

import gr.imu.ntua.tweetinspire.services.bean.SearchTerm;
import org.apache.commons.exec.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: fotis
 * Date: 13/09/13
 * Time: 1:29 PM
 */
public class ScholarDiversifySerivce extends  AbstractDiversifyService implements DiversifyService {

    private Logger logger = LoggerFactory.getLogger(ScholarDiversifySerivce.class);

    private File scholarPy;

    @PostConstruct
    public void init() throws IOException {

        scholarPy = new File(System.getProperty("java.io.tmpdir"),"scholar.py");

        //if the python script is not in temporary storage move it there
        InputStream resourceAsStream = ScholarDiversifySerivce.class.getResourceAsStream("/scholar.py");
        IOUtils.copy(resourceAsStream,new FileOutputStream(scholarPy));
        logger.trace("Map<String,List<Result>> getResultsFor([filter]) Python file at {} ",scholarPy.getAbsolutePath());

    }

    @Override
    public Map<String, List<Result>> getResultsFor(String[] filter) {

        int max = Integer.valueOf(systemProperties.getProperty("diversify.maxResults"));
        Map<String,List<Result>> res = new HashMap<String, List<Result>>();
        res.put("original", new ArrayList<Result>());
        res.put("diversified", new ArrayList<Result>());

        try{

            //python /tmp/scholar.py -c 100 --txt cloud computing
            CommandLine cmdLine = new CommandLine("/usr/bin/python");
            cmdLine.addArgument(scholarPy.getAbsolutePath());
            cmdLine.addArgument("-c");
            cmdLine.addArgument("100");
            cmdLine.addArgument("--csv-header");

            for(String s: filter){
                cmdLine.addArgument(s);

            }


            logger.trace("Map<String,List<Result>> getResultsFor([filter]) {}", cmdLine.toString());

            DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();


            // some time later the result handler callback was invoked so we
            // can safely request the exit value
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PumpStreamHandler psh = new PumpStreamHandler( out );


            ExecuteWatchdog watchdog = new ExecuteWatchdog(60*1000);
            Executor executor = new DefaultExecutor();
            executor.setExitValue(1);
            executor.setWatchdog(watchdog);
            executor.setStreamHandler(psh);

            executor.execute(cmdLine, resultHandler);
            resultHandler.waitFor();

            String s = out.toString();


            logger.trace("Map<String,List<Result>> getResultsFor([filter]) {}",s);


            String[] split = s.split("\n");


            List<Result> results= new ArrayList<>();


            /**
             *
                 0 Title Cloud computing: Distributed Internet computing for IT and scientific research
                 1 URL http://ieeexplore.ieee.org/xpls/abs_all.jsp?arnumber=5233607
                 2 Citations 294
                 3 Versions 23
                 4 Citations list http://scholar.google.com/scholar?cites=16271757900746272421&as_sdt=2005&sciodt=0,5&hl=en&num=20
                 5 Versions list http://scholar.google.com/scholar?cluster=16271757900746272421&hl=en&num=20&as_sdt=0,5
                 6 Year 2009
             */

            int i=0;

            String description ="%s\nYear: %s";//<br/>" +
//                    "Citations: %s<br/>" +
//                    "Versions: %s<br/>" +
//                    "Citations List: <a href=\"%s\">%s</a><br/>" +
//                    "Versions List: <a href=\"%s\">%s</a>\n";

            boolean first = true;
            for(String sp: split){

                if(first){
                    first=false;
                    continue;
                }


                if(sp.trim().length() <=0){
                    continue;
                }
                logger.trace("Map<String,List<Result>> getResultsFor([filter]) {}\n",sp);

                String[] split1 = sp.split("\\|");

                if(split1.length >= 7){
                    results.add(new Result(
                        i,
                        split1[1],
                        split1[0],
                        String.format(
                                description,
                                StringEscapeUtils.escapeHtml(split1[7]),
                                split1[6]
                                ),
                        split1[1]
                    ));
                }

            }




            //crop the array

            if(results.size() > max){
                Collections.addAll(res.get("original"),results.subList(0,max).toArray(new Result[max]));
            }else{
                Collections.addAll(res.get("original"),results.toArray(new Result[results.size()]));
            }

            if(results.size() >0 ){

                List<Result> resultList = diversify(results);

                if(resultList.size() < results.size()){
                    Collections.shuffle(results);
                    resultList = results;
                }

                if(resultList.size() > max){
                    Collections.addAll(res.get("diversified"),resultList.subList(0,max).toArray(new Result[max]));
                }else{
                    Collections.addAll(res.get("diversified"),resultList.toArray(new Result[resultList.size()]));
                }

            }

        }catch (IOException e){
            logger.warn("IO Exception",e.getLocalizedMessage());
        } catch (InterruptedException e) {
            logger.warn("Couldn't complete the python operatio",e.getLocalizedMessage());
        }

        return res;

    }

    @Override
    public List<Image> getImagesFor(String[] filter) {
        throw new NotImplementedException();
    }

    @Override
    public String getSearchEngineName() {
        return "Scholar";  //To change body of implemented methods use File | Settings | File Templates.
    }
    
     @Override
    public List<AbstractDiversifyService.Result> getBingResultsForCloud(String[] filter) {
        throw new NotImplementedException();
    }
     @Override
     public List<SearchTerm> getLinksAsTerms(float maxtermfreq, String terms, int resultsSize){
        throw new NotImplementedException();
    }


}
