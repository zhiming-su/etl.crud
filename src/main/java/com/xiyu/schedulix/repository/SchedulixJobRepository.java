package com.xiyu.schedulix.repository;

import org.springframework.data.repository.CrudRepository;
import com.xiyu.schedulix.model.SchedulixJob;

	// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
	// CRUD refers Create, Read, Update, Delete

	public interface SchedulixJobRepository extends CrudRepository<SchedulixJob, Long> {

	}
