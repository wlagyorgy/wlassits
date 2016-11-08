package hu.bme.instagram.entity;

import org.springframework.context.annotation.Scope;

import javax.persistence.*;
import java.util.Set;

@Scope("session")
@Entity
public class User {

    @Id
    private String userId;
    private String name;
    private String googlePictureUrl;
    @OneToMany(mappedBy = "user")
    private Set<Photo> photos ;

    public String getGooglePictureUrl() {
        return googlePictureUrl;
    }

    public void setGooglePictureUrl(String googlePictureUrl) {
        this.googlePictureUrl = googlePictureUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(Set<Photo> photos) {
        this.photos = photos;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
