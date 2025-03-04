package llm;

import util.logger.MyLogger;
import util.logger.MyLoggerLevel;
import util.secret.MySecret;
import util.secret.NoEnvException;
import util.secret.SecretCategory;
import util.webclient.WebClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LLM {
    private final MyLogger logger;
    private final WebClient webClient;
    private final MySecret secret;
    public LLM() throws NoEnvException {
        logger = MyLogger.getLogger();
//        logger.setLevel(MyLoggerLevel.DEBUG);
        logger.setLevel(MyLoggerLevel.INFO);
        secret = MySecret.getSecret();
        webClient = WebClient.getWebClient();
    }

    public String sendPrompt(String model, String prompt) throws NoEnvException, IOException, InterruptedException {
        Map<String, String> map = new HashMap<>();
        map.put("url", "https://api.together.xyz/v1/chat/completions");
        map.put("method", "POST");
        map.put("headers", "Authorization;Bearer %s;Content-Type;application/json".formatted(secret.getSecret("TOGETHER_API_KEY")));
        map.put("body", """
                    {
                    "model": "%s",
                    "messages": [{
                        "role": "user",
                        "content": "%s"
                    }],
                    "max_tokens": %d
                    }
                """.formatted(model, prompt, 2048));
        String result = webClient.sendRequest(webClient.makeRequest(map));
//        System.out.println(result);
        String content = result.split("content")[1].split("tool_calls")[0].substring(4);
        content = content.substring(0, content.length() - 1).strip();
        content = content.substring(0, content.length() - 2); // ",이라 2개를 제거해야 한다
        return content;
    }
}
