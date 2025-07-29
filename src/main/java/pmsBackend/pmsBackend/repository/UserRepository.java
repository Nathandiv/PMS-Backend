package pmsBackend.pmsBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pmsBackend.pmsBackend.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByCellphone(String cellphone);
    boolean existsByEmail(String email);
    boolean existsByCellphone(String cellphone);
}
