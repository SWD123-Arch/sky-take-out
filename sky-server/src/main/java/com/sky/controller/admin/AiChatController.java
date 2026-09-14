package com.sky.controller.admin;

import com.sky.dto.AiChatDTO;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/admin/ai")
public class AiChatController {

    @Value("${ai.base-url}")
    private String aiBaseUrl;

    @Value("${ai.api-key}")
    private String apiKey;

    @Value("${ai.model-name}")
    private String modelName;
    @Resource
    private RestTemplate restTemplate;

    @PostMapping("/chat")
    public Result<Map<String, String>> chat(@RequestBody AiChatDTO dto) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("model", modelName);
            body.put("stream", false);

            List<Map<String, String>> messages = new ArrayList<>();
            // system提示词：外卖商家AI客服
            Map<String, String> systemMsg = new HashMap<>();
            systemMsg.put("role", "system");
            systemMsg.put("content", "你是外卖平台商家AI客服，回答尽量简短，面向外卖业务。");
            messages.add(systemMsg);

            Map<String, String> userMsg = new HashMap<>();
            userMsg.put("role", "user");
            userMsg.put("content", dto.getMessage());
            messages.add(userMsg);
            body.put("messages", messages);
            // 阿里云百炼思考模式
            body.put("enable_thinking", true);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<Map> resp = restTemplate.postForEntity(aiBaseUrl, entity, Map.class);
            Map<String, Object> respBody = resp.getBody();

            if (respBody == null) {
                log.warn("大模型接口返回空body");
                return Result.error("AI服务返回数据为空，请稍后重试");
            }
            List<Map<String, Object>> choices = (List<Map<String, Object>>) respBody.get("choices");
            if (choices == null || choices.isEmpty()) {
                log.warn("大模型返回choices为空");
                return Result.error("AI服务返回数据异常，请稍后重试");
            }

            Map<String, Object> choice = choices.get(0);
            Map<String, Object> message = (Map<String, Object>) choice.get("message");

            String reasoningContent = (String) message.get("reasoning_content");
            String content = (String) message.get("content");

            Map<String, String> resultMap = new HashMap<>();
            resultMap.put("reasoning", reasoningContent == null ? "" : reasoningContent);
            resultMap.put("answer", content == null ? "" : content);
            return Result.success(resultMap);

        } catch (ResourceAccessException e) {
            // RestTemplate 超时、网络不通会抛出这个异常
            log.error("调用大模型超时/网络异常", e);
            return Result.error("AI服务响应超时，请稍后重试");
        } catch (Exception e) {
            log.error("调用AI大模型发生异常", e);
            // 区分限流、鉴权等第三方异常，统一友好提示，不暴露原始异常信息
            return Result.error("AI服务繁忙，请稍后重试");
        }
    }
}
