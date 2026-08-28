package re.com.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import re.com.dto.ChatResponse;
import re.com.dto.SourceDto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AIChatService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    private static final String SYSTEM_PROMPT = """
        Bạn là Rikkei Internal AI Assistant, trợ lý AI nội bộ của công ty Rikkeisoft.

        Bạn có hai nhiệm vụ chính:
        1. Trả lời các câu hỏi về chính sách, quy chế, nội quy công ty (như giờ làm việc, nghỉ phép, bảo mật, OT, phúc lợi...) bằng cách sử dụng thông tin từ tài liệu nội bộ được cung cấp qua Context từ RAG.
           - Chỉ được sử dụng thông tin có trong Context được cung cấp từ hệ thống.
           - Không được tự suy đoán, không được tự bịa thông tin.
           - Không được sử dụng kiến thức bên ngoài của bạn để thay thế nội dung tài liệu.
           - RẤT QUAN TRỌNG: Nếu thông tin không có trong Context từ tài liệu nội bộ, hãy trả lời chính xác: "Xin lỗi, tôi không tìm thấy thông tin này trong tài liệu nội bộ."
        
        2. Trả lời các câu hỏi nghiệp vụ liên quan đến thông tin nhân viên, phòng ban, và đơn nghỉ phép bằng cách gọi các công cụ (Tools) được cung cấp.
           - Không được tự lấy thông tin nhân viên từ RAG hoặc tự suy đoán. Bạn bắt buộc phải gọi Tool tương ứng để truy vấn dữ liệu từ PostgreSQL.
           - Khi người dùng hỏi về nhân viên, phòng ban, đơn nghỉ phép, hoặc số lượng nhân viên, hãy xác định và gọi Tool phù hợp. Sau khi nhận được kết quả từ Tool, hãy dùng nó để trả lời.

        3. Kết hợp RAG + Tool:
           - Nếu người dùng hỏi kết hợp cả hai (ví dụ: "Theo chính sách công ty, Nguyễn Văn An được nghỉ bao nhiêu ngày mỗi năm và năm nay anh ấy đã nghỉ bao nhiêu ngày?"), bạn phải sử dụng tài liệu nội bộ (RAG Context) để tìm chính sách (ví dụ: số ngày phép của nhân viên chính thức dưới 3 năm là 12 ngày) và gọi Tool để lấy dữ liệu nghiệp vụ của nhân viên (ví dụ: Nguyễn Văn An đã nghỉ bao nhiêu ngày), sau đó tổng hợp câu trả lời chính xác cho cả hai phần.
        
        Hãy luôn giữ thái độ lịch sự, chuyên nghiệp. Trả lời bằng tiếng Việt.
        """;

    public ChatResponse chat(String message) {
        log.info("Processing chat request with message: '{}'", message);

        List<Document> similarDocs = new ArrayList<>();
        try {
            similarDocs = vectorStore.similaritySearch(
                    SearchRequest.builder()
                            .query(message)
                            .similarityThreshold(0.2)
                            .topK(3)
                            .build()
            );
            log.info("Similarity search returned {} documents for sources", similarDocs.size());
        } catch (Exception e) {
            log.warn("Similarity search failed (possibly no documents ingested yet): {}", e.getMessage());
        }

        List<SourceDto> sources = similarDocs.stream()
                .map(doc -> {
                    String docName = (String) doc.getMetadata().getOrDefault("document_name", "Unknown Document");
                    double score = 1.0;
                    Object distObj = doc.getMetadata().get("distance");
                    if (distObj instanceof Number) {
                        double distance = ((Number) distObj).doubleValue();
                        score = Math.max(0.0, Math.min(1.0, 1.0 - distance));
                    }
                    return SourceDto.builder()
                            .document(docName)
                            .score(score)
                            .build();
                })
                .collect(Collectors.toList());

        String answer = chatClient.prompt()
                .system(SYSTEM_PROMPT)
                .user(message)
                .advisors(
                        QuestionAnswerAdvisor.builder(vectorStore)
                                .build()
                )
                .call()
                .content();

        log.info("AI Chat Response generated successfully");

        if (sources.size() > 0 && answer.contains("Xin lỗi, tôi không tìm thấy thông tin này trong tài liệu nội bộ.")) {
            sources = new ArrayList<>();
        }

        return ChatResponse.builder()
                .answer(answer)
                .sources(sources)
                .build();
    }
}
