package re.com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import re.com.entity.CompanyDocument;
import java.util.List;

@Repository
public interface CompanyDocumentRepository extends JpaRepository<CompanyDocument, Long> {
    List<CompanyDocument> findByNameContainingIgnoreCase(String keyword);
}
