import llm.LLM;
import slack.Slack;
import util.logger.MyLogger;
import util.logger.MyLoggerLevel;
import util.secret.MySecret;
import util.secret.NoEnvException;
import util.secret.SecretCategory;
import util.webclient.WebClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

public class Application {
    public static void main(String[] args) throws NoEnvException, InterruptedException, IOException {
        LLM llm = new LLM();
        String aiResult = llm.sendPrompt("meta-llama/Llama-3.3-70B-Instruct-Turbo-Free", "What is Java? No markdown, No escape character, nut shell");
//        System.out.println(aiResult);
        Slack slack = new Slack();
        slack.sendMessage(aiResult);
    }
}
