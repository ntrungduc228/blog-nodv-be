package nodv.repository;

import nodv.model.Reporting;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReportingRepository extends MongoRepository<Reporting, String> {
    Reporting findByObjectId(String objectId);
}
