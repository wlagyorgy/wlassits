package hu.bme.instagram.dal;


import hu.bme.instagram.entity.Photo;
import hu.bme.instagram.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhotoRepository  extends CrudRepository<Photo, String> {
    Iterable<Photo> findByUserName(String name);
}
