package kz.userservice.middle.repository;

import kz.userservice.middle.model.UserProfilePhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProfilePhotoRepository extends JpaRepository<UserProfilePhoto, Long> {
}
