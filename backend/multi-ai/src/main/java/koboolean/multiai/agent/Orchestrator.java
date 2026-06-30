package koboolean.multiai.agent;

import koboolean.multiai.domain.WorkerType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 고객의 질문을 router로 확인 후 Agent로 전달한다.
 */
@Service
@RequiredArgsConstructor
public class Orchestrator {

    private final IntentRouter router;
    private final ConciergeAgent conciergeAgent;
    private final OrderAgent orderAgent;
    private final ReservationAgent reservationAgent;

    public String chat(String message, String conversationId){
        WorkerType workerType = router.determineWorker(message);

        return switch(workerType){
            case RESERVATION -> reservationAgent.process(message, conversationId);
            case CONCIERGE -> conciergeAgent.process(message, conversationId);
            case SOMMELIER -> orderAgent.process(message,conversationId);
        };
    }
}
