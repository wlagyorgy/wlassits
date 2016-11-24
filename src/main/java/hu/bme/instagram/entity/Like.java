package hu.bme.instagram.entity;

import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Like {

    private int likeCount=0;
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> likes;

    public Like(){
        likeCount=0;
        likes = new ArrayList<>();
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public List<String> getLikes() {
        return likes;
    }

    public void setLikes(List<String> likes) {
        this.likes = likes;
    }

    public void addOne()
    {
        likeCount+= 1;
    }
}
