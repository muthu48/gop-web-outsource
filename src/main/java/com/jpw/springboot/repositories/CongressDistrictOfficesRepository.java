package com.jpw.springboot.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.mongodb.DBObject;

@Repository
public interface CongressDistrictOfficesRepository extends MongoRepository<DBObject, String>{
}
