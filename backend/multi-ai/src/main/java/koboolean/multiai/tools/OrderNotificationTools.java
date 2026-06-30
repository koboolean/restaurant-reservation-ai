package koboolean.multiai.tools;

import koboolean.multiai.service.SlackNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderNotificationTools {

    private final SlackNotificationService slackNotificationService;

    @Tool(description = "주문 추가, 부분 취소, 전체 취소처럼 주문 DB가 실제로 바뀐 뒤 공용 운영 Slack 채널에 알림 메시지를 전송합니다. 입력은 완성된 메시지 본문만 전달하십시오.")
    public String sendOrderNotification(String text) {
        return slackNotificationService.sendNotification(text);
    }
}
