package cop4331;

import org.springframework.data.repository.CrudRepository;

import cop4331.User;

// Will be auto-implemented
public interface UserRepository extends CrudRepository<User, Integer> {

}
