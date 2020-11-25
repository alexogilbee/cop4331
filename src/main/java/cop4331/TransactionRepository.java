package cop4331;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import cop4331.Transaction;

// Will be auto-implemented
public interface TransactionRepository extends CrudRepository<Transaction, Integer> {

    List<Transaction> findBysUName(String sUName);

    List<Transaction> findByrUName(String rUName);
}
