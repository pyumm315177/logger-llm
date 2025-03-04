import llm.LLM;
import llm.ModelCategory;
import slack.Slack;
import util.logger.MyLogger;
import util.secret.NoEnvException;

import java.io.IOException;

public class Application {
    public static void main(String[] args) throws NoEnvException, InterruptedException, IOException {
        MyLogger logger = MyLogger.getLogger();
        Slack slack = new Slack();
        LLM llm = new LLM();
        long startTime = System.currentTimeMillis();
        logger.info("START!");
        String aiResult = llm.sendPrompt(ModelCategory.LLAMA, "What is Java? No markdown, No escape character, nut shell");
        // 이미지 만들기
        String imageResult = llm.sendPrompt(
                ModelCategory.FLUX,
          "Create thumbnail images for '%s'.".formatted(aiResult)
        );
//        System.out.println(imageResult);
        slack.sendMessage(aiResult, imageResult);
        // 추론 만들기
        String reasoningResult = llm.sendPrompt(ModelCategory.R1, "A fairly detailed and complex description of '%s'. Without markdown and escape characters. No more than 500 characters. only use korean character and english character and use korean. Translate all Chinese and Chinese characters that can be translated into Korean if possible, and English if not possible. Finally, review your compliance with the restrictions so far.".formatted(aiResult));
//        System.out.println(reasoning);
        slack.sendMessage(reasoningResult, imageResult);
        logger.info("FINISH! %d".formatted(System.currentTimeMillis() - startTime));
    }
}
