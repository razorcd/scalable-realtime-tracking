package com.takeaway.tracking;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.extern.apachecommons.CommonsLog;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class MongoRepo {

    private MongoTemplate template;

    @PostConstruct
    private void setup() {
        MongoClientOptions.Builder optionsBuilder = MongoClientOptions.builder()
                .connectionsPerHost(5000)
                .connectTimeout(5*60*1000)
                ;
        MongoClientURI url = new MongoClientURI("hhhhhhhhhhh", optionsBuilder);
        MongoClient mongoClient = new MongoClient(url);
        MongoDatabase database = mongoClient.getDatabase("tracking_test");
//        MongoCollection<Document> collection = database.getCollection("location");

        template = new MongoTemplate(mongoClient, "tracking_test");
    }

    public List<Location> findByOrderId(String orderId) {
        Query query = new Query()
                .addCriteria(Criteria.where("orderId")
                        .is(orderId));

        List<Location> locations = template.find(query, Location.class);
        return locations;
    }

}
