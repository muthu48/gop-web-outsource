package com.jpw.springboot.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.jpw.springboot.model.LegislatorOpenState;

@Repository
public interface LegislatorOpenStateRepository extends MongoRepository<LegislatorOpenState, String>{
	LegislatorOpenState findById(String legId);

	LegislatorOpenState findByLegId(String legId);
}
