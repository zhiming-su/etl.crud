package com.xiyu.etl.repository;

import org.springframework.data.repository.CrudRepository;

import com.xiyu.etl.model.EtlJob;
import com.xiyu.etl.model.PrimaryKeyEtlJob;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface EtlJobRepository extends CrudRepository<EtlJob, PrimaryKeyEtlJob> {
	
}
