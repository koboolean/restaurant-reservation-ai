package koboolean.multiai.agent;

import koboolean.multiai.domain.WorkerType;
import koboolean.multiai.tools.ReservationNotificationTools;
import koboolean.multiai.tools.OrderTools;
import koboolean.multiai.tools.ReservationTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

@Component
public class ReservationAgent {

    private final ChatClient chatClient;
    private final String systemPrompt;
    private final ChatMemory chatMemory;


    public ReservationAgent(ChatClient.Builder builder,
                            @Qualifier("workerPrompts") Map<WorkerType, String> prompts,
                            ChatMemory chatMemory,
                            ReservationTools reservationTools,
                            OrderTools orderTools,
                            ReservationNotificationTools reservationNotificationTools) {
        this.chatClient = builder.clone()
                .defaultTools(reservationTools, orderTools, reservationNotificationTools)
                .build();
        this.systemPrompt = prompts.get(WorkerType.RESERVATION);
        this.chatMemory = chatMemory;
    }

    public String process(String message, String conversationId) {
        // 날짜 포멧 지정
        String currentDateWithDay = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd (E요일)", Locale.KOREAN));

        String prompt = new SystemPromptTemplate(systemPrompt)
                .create(Map.of("current_date", currentDateWithDay)).getContents();

        return chatClient.prompt()
                .system(prompt)
                .user(message)
                .advisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .content();
    }
}
