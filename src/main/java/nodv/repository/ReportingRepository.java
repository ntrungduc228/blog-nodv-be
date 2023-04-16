package nodv.repository;

import nodv.model.Reporting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface ReportingRepository extends MongoRepository<Reporting, String> {
    Reporting findByObjectId(String objectId);

//    @Query(value = "{ '_id' : ?0 }", fields = "{ 'users.$.role' : 0, 'user.providerId': 0 }")
    Optional<Reporting> findById(String id);

    Page<Reporting> findAll(Pageable pageable);
}
