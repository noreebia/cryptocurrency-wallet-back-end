package wallet.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import wallet.pojo.User;

@Repository
public interface UserRepository extends MongoRepository<User, String>{
	boolean existsByUsername(String username);
	boolean existsByPassword(String password);
	boolean existsByAddress(String address);
	Optional<User> findByAddress(String address);
	Optional<User> findByUsername(String username);
}
