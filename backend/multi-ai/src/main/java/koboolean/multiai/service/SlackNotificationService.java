package koboolean.multiai.service;

import io.modelcontextprotocol.client.McpSyncClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.List;

@Service
@Slf4j
public class SlackNotificationService {

    private static final JsonMapper JSON_MAPPER = JsonMapper.shared();

    private final ToolCallback slackPostMessageTool;
    private final String notificationChannelId;

    public SlackNotificationService(List<McpSyncClient> mcpSyncClients,
                                    @Value("classpath:mcp-servers.json") Resource mcpServersResource) {
        ToolCallback[] toolCallbacks = SyncMcpToolCallbackProvider.builder()
                .mcpClients(mcpSyncClients)
                .build()
                .getToolCallbacks();
        this.slackPostMessageTool = findTool(toolCallbacks, "slack_post_message");
        this.notificationChannelId = readNotificationChannelId(mcpServersResource);
    }

    public String sendNotification(String text) {
        try {
            String payload = JSON_MAPPER.writeValueAsString(Map.of(
                    "channel_id", notificationChannelId,
                    "text", text
            ));

            String result = slackPostMessageTool.call(payload);
            log.info("Slack 알림 전송 완료 channelId={} result={}", notificationChannelId, result);
            return result;
        } catch (JacksonException e) {
            throw new IllegalStateException("Slack 알림 payload 생성에 실패했습니다.", e);
        } catch (RuntimeException e) {
            log.error("Slack 알림 전송 실패 channelId={} message={}", notificationChannelId, text, e);
            throw e;
        }
    }

    private String readNotificationChannelId(Resource mcpServersResource) {
        try {
            JsonNode root = JSON_MAPPER.readTree(mcpServersResource.getInputStream());
            JsonNode envNode = root.path("mcpServers").path("slack").path("env");
            JsonNode channelIdsNode = envNode.get("SLACK_CHANNEL_IDS");

            if (channelIdsNode == null || !channelIdsNode.isTextual() || channelIdsNode.asText().isBlank()) {
                throw new IllegalStateException("mcp-servers.json에 SLACK_CHANNEL_IDS가 설정되어 있지 않습니다.");
            }

            return Arrays.stream(channelIdsNode.asText().split(","))
                    .map(String::trim)
                    .filter(value -> !value.isBlank())
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("SLACK_CHANNEL_IDS에 유효한 channel id가 없습니다."));
        } catch (IOException e) {
            throw new IllegalStateException("mcp-servers.json을 읽을 수 없습니다.", e);
        }
    }

    private ToolCallback findTool(ToolCallback[] toolCallbacks, String toolName) {
        return Arrays.stream(toolCallbacks)
                .filter(tool -> toolName.equals(tool.getToolDefinition().name()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(toolName + " MCP 도구를 찾을 수 없습니다."));
    }
}
