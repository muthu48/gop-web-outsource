package com.jpw.springboot.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.jpw.springboot.model.LegislatorCongressGT;
import com.jpw.springboot.model.User;
import com.mongodb.BasicDBObject;

@Repository
public interface LegislatorCongressGTRepository extends MongoRepository<LegislatorCongressGT, String> {
	//LegislatorCongressGT findByUsername(String username);

	@Query(value = "{ 'id.govtrack' : ?0 }", fields = "{ 'id.govtrack' : 0 }")
    List<LegislatorCongressGT> findByIdGovtrack(Integer govtrack);
	
	@Query(value = "{ 'id.bioguide' : ?0 }", fields = "{ 'id.bioguide' : 0 }")
    List<LegislatorCongressGT> findByIdBioguide(String bioguide);
	
    List<LegislatorCongressGT> findById(BasicDBObject id);
}
