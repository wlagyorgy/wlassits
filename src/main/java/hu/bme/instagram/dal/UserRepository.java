package hu.bme.instagram.dal;


import hu.bme.instagram.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<User, String> {

    List<User> findByName(String name);

    @Override
    User save(User entity);

    @Override
    boolean exists(String s);
}
