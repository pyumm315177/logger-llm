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

public class Application {
    public static void main(String[] args) throws NoEnvException {
        Slack slack = new Slack();
        slack.sendMessage("안녕 나는 슬랙이야 헬륨가스 마시고 요로케됐지");
    }
}
