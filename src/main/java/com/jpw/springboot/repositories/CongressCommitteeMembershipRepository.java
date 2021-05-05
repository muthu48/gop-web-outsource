package com.jpw.springboot.repositories;

import org.json.JSONObject;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.jpw.springboot.model.CommitteeCongressMembershipGT;


@Repository
public interface CongressCommitteeMembershipRepository extends MongoRepository<JSONObject, String>{
}
