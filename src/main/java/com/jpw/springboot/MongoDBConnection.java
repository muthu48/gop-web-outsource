package com.jpw.springboot;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.ConnectionString;
import com.mongodb.Mongo;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
 
//NOT USED, SPRING HANDLES THE CONNECTION
//REQUIRED ONLY WHEN DEFAULT NEEDS TO BE OVERWRITTEN
@Configuration
public class MongoDBConnection extends AbstractMongoClientConfiguration {
	
	//@Value("${spring.data.mongodb.host}")
	private String host; 
	
	@Value("${spring.data.mongodb.database}")
	private String database;

    //@Value("${spring.data.mongodb.username}")
    private String username;
    
    //@Value("${spring.data.mongodb.password}")
    private String password;
    
    //@Value("${spring.data.mongodb.port}")
    private String port;

    @Value("${spring.data.mongodb.uri}")
    private String uri;

    @Bean
	public GridFsTemplate gridFsTemplate() throws Exception {
	    return new GridFsTemplate(mongoDbFactory(), mappingMongoConverter());
	}
	
	@Override
	protected String getDatabaseName() {
		return database;
	}

	@Override
	public MongoClient mongoClient() {
/*		List<MongoCredential> allCred = new ArrayList<MongoCredential>();
        allCred.add(MongoCredential.createCredential(username, database, password.toCharArray()));
        MongoClient client = new MongoClient((new ServerAddress(host, Integer.parseInt(port))), allCred);
        client.setWriteConcern(WriteConcern.ACKNOWLEDGED);

        return client;*/
	      ConnectionString connectionString = new ConnectionString(uri);
	        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
	            .applyConnectionString(connectionString)
	            .build();
	        
	        return MongoClients.create(mongoClientSettings);
	}
	
	@Bean
    public  MongoClientOptions mongoClientOptions(){
        System.setProperty ("javax.net.ssl.keyStore","C:/Users/OPSKY/Java/jdk1.8.0_25/jre/lib/security/cacerts");
        System.setProperty ("javax.net.ssl.keyStorePassword","jksadmin");   
        MongoClientOptions.Builder builder = MongoClientOptions.builder();
        MongoClientOptions options=builder.sslEnabled(true).build();        
        return options;
    }
	
	@Bean
    public MongoTemplate mongoTemplate() throws Exception {
        return new MongoTemplate(mongoClient(), getDatabaseName());
    }

}