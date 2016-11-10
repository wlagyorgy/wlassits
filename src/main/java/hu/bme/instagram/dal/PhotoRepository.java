package hu.bme.instagram.dal;


import hu.bme.instagram.entity.Photo;
import hu.bme.instagram.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public interface PhotoRepository  extends JpaRepository<Photo, String> {

    Iterable<Photo> findByUserName(String name);
    Iterable<Photo> findByTheUserName(String username);
}
