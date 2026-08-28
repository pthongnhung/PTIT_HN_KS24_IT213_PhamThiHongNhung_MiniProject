package re.com.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import re.com.dto.DocumentResponse;
import re.com.entity.CompanyDocument;
import re.com.service.DocumentService;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping("/upload")
    public ResponseEntity<DocumentResponse> uploadDocument(@RequestParam("file") MultipartFile file) throws IOException {
        log.info("REST request to upload document: {}", file.getOriginalFilename());
        CompanyDocument doc = documentService.uploadDocument(file);
        return ResponseEntity.ok(mapToResponse(doc));
    }

    @PostMapping("/ingest")
    public ResponseEntity<DocumentResponse> ingestDocument(@RequestParam("documentId") Long documentId) throws IOException {
        log.info("REST request to ingest document with ID: {}", documentId);
        CompanyDocument doc = documentService.ingestDocument(documentId);
        return ResponseEntity.ok(mapToResponse(doc));
    }

    @GetMapping
    public ResponseEntity<List<DocumentResponse>> getAllDocuments() {
        log.info("REST request to get all documents");
        List<CompanyDocument> docs = documentService.getAllDocuments();
        return ResponseEntity.ok(docs.stream().map(this::mapToResponse).collect(Collectors.toList()));
    }

    @GetMapping("/search")
    public ResponseEntity<List<DocumentResponse>> searchDocuments(@RequestParam("keyword") String keyword) {
        log.info("REST request to search documents with keyword: {}", keyword);
        List<CompanyDocument> docs = documentService.searchDocuments(keyword);
        return ResponseEntity.ok(docs.stream().map(this::mapToResponse).collect(Collectors.toList()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable("id") Long id) throws IOException {
        log.info("REST request to delete document with ID: {}", id);
        documentService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }

    private DocumentResponse mapToResponse(CompanyDocument doc) {
        return DocumentResponse.builder()
                .id(doc.getId())
                .name(doc.getName())
                .fileType(doc.getFileType())
                .size(doc.getSize())
                .uploadedAt(doc.getUploadedAt())
                .status(doc.getStatus())
                .build();
    }
}
