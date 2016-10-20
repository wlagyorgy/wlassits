package hu.bme.instagram.egyes.Entities;

import org.springframework.data.annotation.Id;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Picture
{
    @ManyToOne
    @JoinColumn(name="userid")
    private User user;
    @Id
    private String Id;
    private String tag;


    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public String getId()
    {
        return Id;
    }

    public void setId(String id)
    {
        Id = id;
    }

    public String getTag()
    {
        return tag;
    }

    public void setTag(String tag)
    {
        this.tag = tag;
    }
}
