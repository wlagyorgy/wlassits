package hu.bme.instagram.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Photo {

    @Id
    private String public_id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date created_at;
    private String url;
    @ManyToOne
    @JoinColumn(name = "user")
    private User user;

    public String getPublic_id() {
        return public_id;
    }

    public void setPublic_id(String public_id) {
        this.public_id = public_id;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
