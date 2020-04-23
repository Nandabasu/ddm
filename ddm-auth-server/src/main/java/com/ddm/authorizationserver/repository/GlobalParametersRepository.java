package com.ddm.authorizationserver.repository;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ddm.authorizationserver.model.GlobalParameters;

@Repository
@Transactional
public interface GlobalParametersRepository extends JpaRepository<GlobalParameters, Long>{

	Optional<GlobalParameters> findByParameter(String parameter);
}
