package cop4331;

import org.springframework.data.repository.CrudRepository;

import cop4331.Account;

// Will be auto-implemented
public interface AccountRepository extends CrudRepository<Account, Integer> {

}
