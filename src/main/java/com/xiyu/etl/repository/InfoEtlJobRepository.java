package com.xiyu.etl.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.xiyu.etl.model.InfoEtlJob;
import com.xiyu.etl.model.PrimaryKeyEtlJob;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface InfoEtlJobRepository extends CrudRepository<InfoEtlJob, PrimaryKeyEtlJob> {
	
	//Optional<InfoEtlJob> findByname(String pk);
}
