package com.takeaway.tracking;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.IndexOperations;
//import org.springframework.data.mongodb.core.index.IndexResolver;
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

import javax.annotation.PostConstruct;
import javax.annotation.Priority;
import java.util.Arrays;
import java.util.List;

@Configuration
@Priority(0)
@EnableBinding(LocationSink.class)
public class Config {

    private List<String> mongoCollectionsToCapp = Arrays.asList(Location.class.getSimpleName().toLowerCase());

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    MongoMappingContext mongoMappingContext;

    /**
     * Cap mongo collection to support tailing.
     */
    @PostConstruct
    private void createCappedCollections(){

        mongoCollectionsToCapp.forEach(collectionName -> {
            if (!mongoTemplate.collectionExists(collectionName)) return;

            Document stats = mongoTemplate.getDb().runCommand(new Document("collStats", collectionName));
            if ((Boolean) stats.get("capped") == true) {
//                LOGGER.debug("Collection {} is already capped.", collectionName);
            } else {
                mongoTemplate.executeCommand("{\"convertToCapped\":\""+collectionName+"\",\"size\":500000000}");
//                LOGGER.debug("Collection {} was capped now.", collectionName);
            }
        });
    }


//    // set indexes
//    @EventListener(ApplicationReadyEvent.class)
//    public void initIndicesAfterStartup() {
//
//        IndexOperations indexOps = mongoTemplate.indexOps(Location.class);
//
//        IndexResolver resolver = new MongoPersistentEntityIndexResolver(mongoMappingContext);
//        resolver.resolveIndexFor(Location.class).forEach(indexOps::ensureIndex);
//    }
}