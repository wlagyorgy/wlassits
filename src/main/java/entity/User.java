package entity;

import javax.persistence.*;
import java.util.Set;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int userId;
    private String token;
    private String name;
    private String googlePictureUrl;
    @OneToMany(mappedBy = "user")
    private Set<Photo> photos;

    public String getGooglePictureUrl() {
        return googlePictureUrl;
    }

    public void setGooglePictureUrl(String googlePictureUrl) {
        this.googlePictureUrl = googlePictureUrl;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Set<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(Set<Photo> photos) {
        this.photos = photos;
    }
}
