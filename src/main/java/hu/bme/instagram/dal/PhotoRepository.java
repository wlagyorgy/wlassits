package hu.bme.instagram.dal;


import hu.bme.instagram.entity.Photo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotoRepository  extends CrudRepository<Photo, String> {


}
