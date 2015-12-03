package gr.imu.ntua.cruise.db.domain;

import com.existanze.libraries.orm.domain.SimpleBean;

import javax.persistence.*;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: fotis
 * Date: 25/09/13
 * Time: 5:17 PM
 */
@Entity
@Table(name="rating")
public class Rating implements SimpleBean{

    @TableGenerator(
            name="tseq",
            table="sequence",
            pkColumnName="sequence_name",
            valueColumnName="sequence_index",
            pkColumnValue="rating_sequence",
            allocationSize = 1)
    @Id
    @GeneratedValue(strategy= GenerationType.TABLE, generator="tseq")
    private Integer id;

    @Column
    private String session;

    @Column
    private String url;

    @Column
    private String terms;

    @Column
    private String query;

    @Column
    private String title;

    @Column
    private String theme;
    @Column
    private String source;

    @Column
    private String rating;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTerms() {
        return terms;
    }

    public void setTerms(String terms) {
        this.terms = terms;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Rating{" +
                "id=" + id +
                ", session='" + session + '\'' +
                ", url='" + url + '\'' +
                ", terms='" + terms + '\'' +
                ", query='" + query + '\'' +
                ", source='" + source + '\'' +
                ", rating='" + rating + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
