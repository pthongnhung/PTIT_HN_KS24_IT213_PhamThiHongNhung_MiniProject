package re.com.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import re.com.entity.CompanyDocument;
import re.com.repository.CompanyDocumentRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentService {

    private final CompanyDocumentRepository documentRepository;
    private final VectorStore vectorStore;
    private final JdbcTemplate jdbcTemplate;

    private final Path uploadDir = Paths.get("uploads").toAbsolutePath().normalize();

    @Transactional
    public CompanyDocument uploadDocument(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new IllegalArgumentException("Invalid filename");
        }

        String extension = getFileExtension(originalFilename).toLowerCase();
        if (!extension.equals("pdf") && !extension.equals("docx") && !extension.equals("txt")) {
            throw new IllegalArgumentException("Unsupported file type. Only PDF, DOCX, and TXT are allowed.");
        }

        Files.createDirectories(uploadDir);

        CompanyDocument doc = CompanyDocument.builder()
                .name(originalFilename)
                .fileType(file.getContentType())
                .size(file.getSize())
                .uploadedAt(LocalDateTime.now())
                .status("PENDING")
                .build();
        doc = documentRepository.save(doc);

        String savedFilename = doc.getId() + "_" + originalFilename;
        Path targetPath = uploadDir.resolve(savedFilename);
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        log.info("Uploaded file saved to path: {}", targetPath);
        return doc;
    }

    @Transactional
    public CompanyDocument ingestDocument(Long documentId) throws IOException {
        CompanyDocument doc = documentRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Document not found with ID: " + documentId));

        String savedFilename = doc.getId() + "_" + doc.getName();
        Path filePath = uploadDir.resolve(savedFilename);

        if (!Files.exists(filePath)) {
            doc.setStatus("FAILED");
            documentRepository.save(doc);
            throw new IOException("File does not exist on disk: " + filePath);
        }

        log.info("Starting ingestion for document ID: {}, file: {}", doc.getId(), filePath);

        try {
            FileSystemResource resource = new FileSystemResource(filePath.toFile());
            TikaDocumentReader reader = new TikaDocumentReader(resource);
            List<Document> documents = reader.read();

            log.info("Read {} main document(s) from file", documents.size());

            TokenTextSplitter splitter = new TokenTextSplitter();
            List<Document> splitDocs = splitter.split(documents);

            log.info("Split document into {} chunks", splitDocs.size());

            for (Document chunk : splitDocs) {
                chunk.getMetadata().put("document_id", doc.getId().toString());
                chunk.getMetadata().put("document_name", doc.getName());
            }

            vectorStore.add(splitDocs);

            log.info("Successfully loaded {} vectors into PGVector store", splitDocs.size());

            doc.setStatus("INGESTED");
            return documentRepository.save(doc);
        } catch (Exception e) {
            log.error("Failed to ingest document ID: {}", doc.getId(), e);
            doc.setStatus("FAILED");
            documentRepository.save(doc);
            throw e;
        }
    }

    public List<CompanyDocument> getAllDocuments() {
        return documentRepository.findAll();
    }

    public List<CompanyDocument> searchDocuments(String keyword) {
        return documentRepository.findByNameContainingIgnoreCase(keyword);
    }

    @Transactional
    public void deleteDocument(Long id) throws IOException {
        CompanyDocument doc = documentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Document not found with ID: " + id));

        deleteVectorsByDocumentId(doc.getId());

        String savedFilename = doc.getId() + "_" + doc.getName();
        Path filePath = uploadDir.resolve(savedFilename);
        if (Files.exists(filePath)) {
            Files.delete(filePath);
            log.info("Deleted file from disk: {}", filePath);
        }

        documentRepository.delete(doc);
        log.info("Deleted document metadata from DB: {}", doc.getId());
    }

    private void deleteVectorsByDocumentId(Long documentId) {
        log.info("Deleting vectors for document ID: {} from PGVector store", documentId);
        String sql = "DELETE FROM vector_store WHERE metadata::jsonb ->> 'document_id' = ?";
        try {
            int rowsDeleted = jdbcTemplate.update(sql, documentId.toString());
            log.info("Deleted {} vector rows from database", rowsDeleted);
        } catch (Exception e) {
            log.error("Failed to delete vectors from vector_store", e);
        }
    }

    private String getFileExtension(String filename) {
        int lastIndex = filename.lastIndexOf('.');
        if (lastIndex == -1) {
            return "";
        }
        return filename.substring(lastIndex + 1);
    }
}
