package nodv.repository;

import nodv.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReportingRepository extends MongoRepository<User, String> {
}
