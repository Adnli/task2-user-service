package kz.userservice.middle.repository;

import jakarta.transaction.Transactional;
import kz.userservice.middle.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public interface UserRepository extends JpaRepository<User, Long> {
}
