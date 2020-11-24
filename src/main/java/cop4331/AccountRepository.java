package cop4331;

import org.springframework.data.repository.CrudRepository;
import java.util.List;

import cop4331.Account;

// Will be auto-implemented
public interface AccountRepository extends CrudRepository<Account, Integer> {

    List<Account> findByuID(Integer uID);
}
