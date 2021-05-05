package com.jpw.springboot.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.jpw.springboot.model.CommitteeCongressGT;

@Repository
public interface CongressCommitteeRepository extends MongoRepository<CommitteeCongressGT, String>{
    @Query("{'thomas_id':?0}")
	CommitteeCongressGT findByThomasId(String thomasId);

}
