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
    private int width;
    private int height;
    private Long bytes;
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

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Long getBytes() {
        return bytes;
    }

    public void setBytes(Long bytes) {
        this.bytes = bytes;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
