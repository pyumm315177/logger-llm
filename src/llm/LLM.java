package llm;
// 이 클래스가 llm라는 패키지 안에 있다는 뜻. (폴더 구조를 나타냄)

import util.logger.MyLogger;
// 로그를 남길 때 사용할 MyLogger 클래스 임포트 (로그 관리용)

import util.logger.MyLoggerLevel;
// 로그 레벨(DEBUG, INFO 등)을 지정할 때 사용할 enum 임포트

import util.secret.MySecret;
// API 키 등 환경 변수에서 보안 정보를 가져오는 MySecret 클래스 임포트

import util.secret.NoEnvException;
// 환경 변수가 없을 때 던지는 예외 클래스 임포트

import util.webclient.WebClient;
// HTTP 요청을 보낼 때 사용할 WebClient 클래스 임포트

import java.io.IOException;
// IO 관련 예외 처리를 위해 필요 (http 요청 시 발생 가능)

import java.util.HashMap;
// HTTP 요청 정보 담을 때 사용할 HashMap 임포트 (Key-Value 형태 데이터 저장)

import java.util.Map;
// Map 인터페이스 임포트 (HashMap은 Map의 구현체)

// LLM 클래스 정의 (LLM API를 호출하는 기능 담당) 
public class LLM { 
    
    private final MyLogger logger; 
    // 로그 기록용 객체 (MyLogger는 싱글톤이라, 프로그램 전체에서 하나만 생성됨)
    
    private final WebClient webClient;  
    // HTTP 요청을 보낼 때 사용할 WebClient 객체 (싱글톤)
    
    private final MySecret secret;  
    // 환경변수에서 보안 키를 가져올 MySecret 객체 (싱글톤)
    
    // 생성자 - LLM 객체가 생성될 때 자동으로 실행되는 부분
    public LLM() throws NoEnvException {
        logger = MyLogger.getLogger();  
        // 프로그램 전체에서 하나만 있는 MyLogger 객체 가져오기 (싱글톤 패턴)
    
        logger.setLevel(MyLoggerLevel.DEBUG);  
        // 로그 레벨을 DEBUG로 설정 (DEBUG는 상세한 로그까지 출력)
        // logger.setLevel(MyLoggerLevel.INFO);
        // INFO로 설정하는 부분은 주석 처리됨 (필요하면 나중에 INFO로 변경 가능)
    
        secret = MySecret.getSecret();  
        // MySecret 객체 가져오기 (환경 변수에서 API 키 같은 보안정보 읽는 역할)
    
        webClient = WebClient.getWebClient();  
        // 프로그램 전체에서 하나만 있는 WebClient 객체 가져오기 (싱글톤 패턴)
    }

    // LLM API에 프롬프트를 보내는 함수 (모델 종류와 프롬프트 내용을 전달받음)
    public String sendPrompt(ModelCategory model, String prompt) throws NoEnvException, IOException, InterruptedException {
        Map<String, String> map = new HashMap<>();  
        // HTTP 요청 정보를 담을 HashMap 생성 (요청 메서드, URL, 헤더, 바디 등)
    
        // 기본 설정 - 메서드는 POST (모든 요청은 POST 방식으로 보냄)
        map.put("method", "POST");
    
        // 요청 헤더 설정 - Authorization에 API 키 추가, Content-Type은 JSON
        map.put("headers", "Authorization;Bearer %s;Content-Type;application/json".formatted(secret.getSecret("TOGETHER_API_KEY")));
    
        // 어떤 모델을 쓰는지에 따라 요청 URL과 Body 내용 분기 처리
        switch (model) {
            case R1:
            case LLAMA:
                // R1 또는 LLAMA 모델인 경우 아래 URL과 Body 사용
                map.put("url", "https://api.together.xyz/v1/chat/completions");
    
                // 요청 본문 - 모델 이름, 사용자 메시지, 최대 토큰 수 설정
                map.put("body", """
                    {
                    "model": "%s",
                    "messages": [{
                        "role": "user",
                        "content": "%s"
                    }],
                    "max_tokens": %d
                    }
                """.formatted(model.name, prompt, 2048));
                break;
    
            case FLUX:
                // 이미지 생성 모델인 경우
                map.put("url", "https://api.together.xyz/v1/images/generations");
    
                // 요청 본문 - 모델 이름과 프롬프트만 필요
                map.put("body", """
                    {
                    "model": "%s",
                    "prompt": "%s"
                    }
                """.formatted(model.name, prompt));
                break;
    
            default:
                // 위에서 정의되지 않은 모델이 들어오면 예외 발생
                throw new RuntimeException();  // "대체 뭘 넣은거냐;;;"는 개발자 실수 방지용 주석
        }
    
        // WebClient로 실제 HTTP 요청 보내기 (makeRequest로 HttpRequest 객체 만들고, sendRequest로 보냄)
        String result = webClient.sendRequest(webClient.makeRequest(map));
    
        // 응답 결과를 로그에 DEBUG 레벨로 남김 (개발자 확인용)
        logger.debug(result);
    
        // 모델 종류에 따라 응답 파싱 방법도 다르게 처리 (텍스트 모델과 이미지 모델 구분)
        switch (model) {
            case R1:
            case LLAMA:
                // 텍스트 모델인 경우 응답에서 content 부분만 추출
                String content = result.split("content")[1].split("tool_calls")[0].substring(4);
                content = content.substring(0, content.length() - 1).strip();
                content = content.substring(0, content.length() - 2);  // 맨 끝의 ", 제거
    
                if (model.equals(ModelCategory.R1)) {
                    // R1 모델인 경우 /think 이후 부분만 추출
                    logger.debug(content);  // 디버깅용 로그 남기기
                    content = content.split("/think")[1].substring(1).strip();
                }
    
                // 개행문자(\n)를 제거하고 최종 결과 반환
                return content.replace("\\n", "");
    
            case FLUX:
                // 이미지 생성 모델인 경우 응답에서 이미지 URL만 추출
                String url = result.split("url")[1].split("timings")[0].substring(4);
                url = url.substring(0, url.length() - 1).strip();
                url = url.substring(0, url.length() - 2);  // 맨 끝의 ", 제거
                return url;  // 이미지 URL 반환
    
            default:
                // 여기도 방어 코드 - 만약 정의되지 않은 모델이 들어오면 예외 발생
                throw new RuntimeException();  // "대체 뭘 넣은거냐;;;"는 개발자 실수 방지용
        }
    }
}
