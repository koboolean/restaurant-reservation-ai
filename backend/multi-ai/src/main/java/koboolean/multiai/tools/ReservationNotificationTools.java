package koboolean.multiai.tools;

import koboolean.multiai.service.SlackNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationNotificationTools {

    private final SlackNotificationService slackNotificationService;

    @Tool(description = "예약 생성, 변경, 취소처럼 예약 DB가 실제로 바뀐 뒤 공용 운영 Slack 채널에 알림 메시지를 전송합니다. 입력은 완성된 메시지 본문만 전달하십시오.")
    public String sendReservationNotification(String text) {
        return slackNotificationService.sendNotification(text);
    }
}
