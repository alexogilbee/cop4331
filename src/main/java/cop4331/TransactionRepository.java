package cop4331;

import org.springframework.data.repository.CrudRepository;

import cop4331.Transaction;

// Will be auto-implemented
public interface TransactionRepository extends CrudRepository<Transaction, Integer> {

}
