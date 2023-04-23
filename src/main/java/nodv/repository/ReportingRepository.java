package nodv.repository;

import nodv.model.Reporting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface ReportingRepository extends MongoRepository<Reporting, String> {
    Reporting findByObjectId(String objectId);

    @Query(value = "{ '_id' : ?0 }", fields = "{ 'user.providerId': 0 }")
    Optional<Reporting> findById(String id);

    @Query(value= "{}", fields = "{ 'users': 0 }")
    Page<Reporting> findAll(Pageable pageable);

    Page<Reporting> findAllBy(Criteria criteria, Pageable pageable);
}
