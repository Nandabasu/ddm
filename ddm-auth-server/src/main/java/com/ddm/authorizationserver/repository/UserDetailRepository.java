package com.ddm.authorizationserver.repository;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ddm.authorizationserver.model.User;

@Repository
@Transactional
public interface UserDetailRepository extends JpaRepository<User, Long> {

	Optional<User> findByUsername(String name);

	Optional<User> findByEmail(String email);

	Boolean existsByUsername(String username);

	Boolean existsByEmail(String email);

	Boolean existsByPan(String pan);

	Boolean existsByMobile(String mobile);

	Optional<User> findByMobile(String mobile);

	Optional<User> findByPan(String pan);

	List<User> findAll();

	Optional<User> findById(long id);

	@Query("select u from User u inner join u.roles r where r.id=2")
	List<User> getAllGroupAdmins();

}
