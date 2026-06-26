package koboolean.ai.config;

import koboolean.ai.tools.GourmetBotTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

@Configuration
public class AiConfig {

    @Bean
    public ChatMemory chatMemory(JdbcChatMemoryRepository jdbcChatMemoryRepository) {

        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(jdbcChatMemoryRepository)
                .maxMessages(15)
                .build();
    }

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder, GourmetBotTools tools, @Value("classpath:/prompt/system.st") Resource systemResource, ChatMemory chatMemory){

        // 시스템 프롬프트 등록
        SystemPromptTemplate system = new SystemPromptTemplate(systemResource);
        // 날짜 포멧 지정
        String currentDateWithDay = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd (E요일)", Locale.KOREAN));

        // 시스템 프롬프트에 날짜 포멧 등록 -> current_date를 오늘로 치환한다.
        String systemPromptTxt = system.render(Map.of("current_date", currentDateWithDay));

        return builder
                .defaultSystem(systemPromptTxt) // 시스템 프롬프트 등록
                .defaultTools(tools) // 툴 등록
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(), // 요청 및 응답 로깅
                        MessageChatMemoryAdvisor.builder(chatMemory).build() // 대화 기록 자동관리
                )
                .build();
    }
}
