package koboolean.multiai.config;

import com.knuddels.jtokkit.api.EncodingType;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RegLoader {

    @Value("classpath:menu-knowledge.txt")
    private Resource resource;
    private final VectorStore vectorStore;
    private final JdbcClient jdbcClient;

    @PostConstruct
    public void init() {
        try{
            String sql = "select count(*) from vector_store";

            Integer c = jdbcClient.sql(sql).query(Integer.class).single();

            if(c == 0){
                try(BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))){
                    List<Document> documents = br.lines().map(Document::new).toList();

                    TokenTextSplitter splitter = TokenTextSplitter.builder()
                            .withEncodingType(EncodingType.O200K_BASE)
                            .withChunkSize(800) // 청크 사이즈 지정
                            .withMinChunkSizeChars(200) // 최소 청크 사이즈 크기를 지정한다.
                            .withMinChunkLengthToEmbed(10) // 분할된 chunk가 10자 이상
                            .withMaxNumChunks(5000) // 최대 청크 사이즈를 지정한다.
                            .withKeepSeparator(true) // \n 글자를 유지시킨다.
                            .build();

                    for(Document doc : documents){
                        List<Document> chunks = splitter.split(doc);
                        vectorStore.accept(chunks);
                    }
                }
            }
        }catch(Exception e){
            log.error("Failed to Data Loading : {}", e.getMessage());
        }
    }


}
