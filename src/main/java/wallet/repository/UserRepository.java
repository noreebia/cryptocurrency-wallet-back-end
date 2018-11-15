package wallet.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import wallet.entity.User;

@Repository
public interface UserRepository extends MongoRepository<User, String>{
	boolean existsByUsername(String username);
	boolean existsByPassword(String password);
	User findByUsername(String username);
}
