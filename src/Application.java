import llm.LLM;
import llm.ModelCategory;
import slack.Slack;
import util.secret.NoEnvException;

import java.io.IOException;

public class Application {
    public static void main(String[] args) throws NoEnvException, InterruptedException, IOException {
        Slack slack = new Slack();
        LLM llm = new LLM();
        String aiResult = llm.sendPrompt(ModelCategory.LLAMA, "What is Java? No markdown, No escape character, nut shell");
        // 이미지 만들기
        String imageResult = llm.sendPrompt(
                ModelCategory.FLUX,
          "Create thumbnail images for '%s'.".formatted(aiResult)
        );
//        System.out.println(imageResult);
        slack.sendMessage(imageResult);
        // 추론 모델 만들기

//        System.out.println(aiResult);

    }
}
