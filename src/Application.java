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
    public static void main(String[] args) throws NoEnvException, InterruptedException {
        Slack slack = new Slack();
        for (int i = 0; i < 100; i++) {
            Thread.sleep(1000);
            slack.sendMessage("안녕안녕 나는 슬랙이야");
            Thread.sleep(1000);
            slack.sendMessage("헬륨가스 마시고");
            Thread.sleep(1000);
            slack.sendMessage("요를레이히~");
        }
    }
}
