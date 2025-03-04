import util.logger.MyLogger;
import util.logger.MyLoggerLevel;
import util.webclient.WebClient;

import java.io.IOException;
import java.util.HashMap;

public class Application {
    public static void main(String[] args) {
        MyLogger logger = MyLogger.getLogger();
        logger.setLevel(MyLoggerLevel.DEBUG);
        WebClient webClient = WebClient.getWebClient();
        String result = "";
        try {
            result = webClient.sendRequest("", webClient.makeRequest(new HashMap<>()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        logger.info(result);
    }
}
