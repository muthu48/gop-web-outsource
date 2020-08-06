package com.jpw.springboot;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;
 
@Configuration
public class MongoDBConnection  extends AbstractMongoConfiguration{
	
	@Value("${spring.data.mongodb.host}")
	private String host; 
	
	@Value("${spring.data.mongodb.database}")
	private String database;

    @Value("${spring.data.mongodb.username}")
    private String username;
    
    @Value("${spring.data.mongodb.password}")
    private String password;
    
    @Value("${spring.data.mongodb.port}")
    private String port;

    @Bean
	public GridFsTemplate gridFsTemplate() throws Exception {
	    return new GridFsTemplate(mongoDbFactory(), mappingMongoConverter());
	}
	
	@Override
	protected String getDatabaseName() {
		return database;
	}
 
	@Override
	public Mongo mongo() throws Exception {
		//return new MongoClient(host);
		List<MongoCredential> allCred = new ArrayList<MongoCredential>();
        allCred.add(MongoCredential.createCredential(username, database, password.toCharArray()));
        MongoClient client = new MongoClient((new ServerAddress(host, Integer.parseInt(port))), allCred);
        client.setWriteConcern(WriteConcern.ACKNOWLEDGED);

        return client;
	}
}