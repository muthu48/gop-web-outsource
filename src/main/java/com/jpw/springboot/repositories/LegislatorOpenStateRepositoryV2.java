package com.jpw.springboot.repositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.jpw.springboot.model.LegislatorOpenState;
import com.jpw.springboot.model.LegislatorOpenStateV2;

@Repository
public interface LegislatorOpenStateRepositoryV2 extends MongoRepository<LegislatorOpenStateV2, String>{
	//Optional<LegislatorOpenState> findById(String legId);

	//LegislatorOpenStateV2 findByLegId(String legId);
	LegislatorOpenStateV2 findByName(String name);
}
