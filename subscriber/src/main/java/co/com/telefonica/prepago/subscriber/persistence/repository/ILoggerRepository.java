package co.com.telefonica.prepago.subscriber.persistence.repository;

import co.com.telefonica.prepago.subscriber.persistence.entity.LoggerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ILoggerRepository extends JpaRepository<LoggerEntity, Long> {
}
