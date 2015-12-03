package gr.imu.ntua.cruise.db.domain;

import com.existanze.libraries.orm.domain.SimpleBean;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: fotis
 * Date: 22/08/13
 * Time: 1:22 PM
 */
@Entity
@Table(name="bookmarks")
public class Bookmarks implements SimpleBean{


    @TableGenerator(
            name="tseq",
            table="sequence",
            pkColumnName="sequence_name",
            valueColumnName="sequence_index",
            pkColumnValue="bookmarks_sequence",
            allocationSize = 1)
    @Id
    @GeneratedValue(strategy= GenerationType.TABLE, generator="tseq")
    private Integer id;

    @Column
    private String url;

    @Column
    private String terms;

    @Column
    private String query;

    @Column
    private String source;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();


    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer integer) {
        this.id = id;
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createAt) {
        this.createdAt = createAt;
    }
}
