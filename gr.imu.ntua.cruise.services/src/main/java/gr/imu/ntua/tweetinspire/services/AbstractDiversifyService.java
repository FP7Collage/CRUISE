package gr.imu.ntua.tweetinspire.services;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: fotis
 * Date: 17/04/13
 * Time: 4:38 PM
 */
public abstract  class AbstractDiversifyService implements DiversifyService {
    private Logger logger = LoggerFactory.getLogger(AbstractDiversifyService.class);

    @Inject
    Properties systemProperties;
//    Environment environment;

    @Override
    public List<Result> diversify(List<Result> resultList) throws IOException {

        OutputStream o = new ByteArrayOutputStream();


        Map<String,List<Result>> results = new HashMap<String, List<Result>>();
        results.put("items",resultList);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(o,results);

        String json = o.toString();
        logger.trace("List<Result> diversify([resultList]) {} ",json);



        HttpPost post = new HttpPost(systemProperties.getProperty("diversify.url"));
        post.setEntity(
                new StringEntity(json, ContentType.APPLICATION_JSON)
        );

        int connectionTimeout =
                Integer.valueOf(systemProperties.getProperty("http.connectionTimeout"));

        HttpParams connectionParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(connectionParameters,connectionTimeout);
        HttpConnectionParams.setSoTimeout(connectionParameters,connectionTimeout);

        HttpClient c = new DefaultHttpClient(connectionParameters);

        long start = System.currentTimeMillis();
        HttpResponse execute = c.execute(post);
        String resultIds = IOUtils.toString(execute.getEntity().getContent());

        long time = System.currentTimeMillis() - start;
        logger.debug("List<Result> diversify([resultList]) Diversification took {} ms \n\t{}",time,resultIds);
        List idsList = objectMapper.readValue(resultIds, List.class);
        List<Result> diversified = new ArrayList<Result>();

        for(Object object: idsList){

            Integer id1 = (Integer) ((Map) object).get("id");
            Result id = resultList.get(id1);
            diversified.add(id);
        }

        return diversified;

    }

//    public void setEnvironment(Environment environment) {
//        this.systemProperties = environment;
//    }
    public void setSystemProperties(Properties systemProperties) {
        this.systemProperties = systemProperties;
    }


    public static class Image{

        // [{"id","title","thumbnail","link"}]

        private String id;
        private String title;
        private String thumbnail;
        private String link;

        public Image() {
        }

        public Image(String id, String title, String thumbnail, String link) {
            this.id = id;
            this.title = title;
            this.thumbnail = thumbnail;
            this.link = link;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getThumbnail() {
            return thumbnail;
        }

        public void setThumbnail(String thumbnail) {
            this.thumbnail = thumbnail;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }



        @Override
        public String toString() {
            return "Image{" +
                    "id=" + id +
                    ", title='" + title + '\'' +
                    ", thumbnail='" + thumbnail + '\'' +
                    ", link='" + link + '\'' +
                    '}';
        }
    }


    public static class Result{

        private Integer id;
        private String link;
        private String title;
        private String snippet;
        private String uid;

        public Result() {
        }

        public Result(Integer id, String link, String title, String snippet, String uid) {
            this.id = id;
            this.link = link;
            this.title = title;
            this.snippet = snippet;
            this.uid = uid;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getSnippet() {
            return snippet;
        }

        public void setSnippet(String snippet) {
            this.snippet = snippet;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        @Override
        public String toString() {
            return "Result{" +
                    "id=" + id +
                    ", link='" + link + '\'' +
                    ", title='" + title + '\'' +
                    ", snippet='" + snippet + '\'' +
                    ", uid='" + uid + '\'' +
                    '}';
        }
    }
    
     public static class Video{

        private String id;
        private String link;
        private String thumbnail;
        private String title;
        private String snippet;
        private String uid;

        public Video() {
        }

        public Video(String id, String link, String thumbnail, String title, String snippet, String uid) {
            this.id = id;
            this.link = link;
            this.thumbnail = thumbnail;
            this.title = title;
            this.snippet = snippet;
            this.uid = uid;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }
        
        public void setThumbnail(String thumbnail){
            this.thumbnail = thumbnail;
        }
        
        public String getThumbnail(){
            return thumbnail;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getSnippet() {
            return snippet;
        }

        public void setSnippet(String snippet) {
            this.snippet = snippet;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        @Override
        public String toString() {
            return "Result{" +
                    "id=" + id +
                    ", link='" + link + '\'' +
                    ", thumbnail='" + thumbnail + '\'' +
                    ", title='" + title + '\'' +
                    ", snippet='" + snippet + '\'' +
                    ", uid='" + uid + '\'' +
                    '}';
        }
    }

}
