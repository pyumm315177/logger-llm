import llm.LLM;  // LLM API 호출 기능 담당 클래스
import llm.ModelCategory;  // LLM 모델 종류(LLAMA, FLUX, R1)를 정의한 enum
import slack.Slack;  // Slack으로 메시지 전송 기능 클래스
import util.logger.MyLogger;  // 로그 기록 담당 싱글톤 로거 클래스
import util.secret.NoEnvException;  // 환경 변수 없을 때 던지는 예외

import java.io.IOException;  // 입출력 예외 처리
import java.util.concurrent.*;  // ExecutorService, Future 등 멀티스레드 관련 클래스

// 애플리케이션의 시작점이자, 메인 비즈니스 로직을 담은 클래스
public class Application {

    public static void main(String[] args) throws NoEnvException, InterruptedException, IOException, ExecutionException {
        // 로그 객체 생성 (싱글톤)
        MyLogger logger = MyLogger.getLogger();

        // Slack 객체 생성 (Slack 메시지 전송 담당, 내부 구현은 없지만 외부로 전송하는 역할 가정)
        Slack slack = new Slack();

        // LLM 객체 생성 (LLM API 호출 기능을 갖는 객체)
        LLM llm = new LLM();

        // 프로그램 시작 시간 기록 (작업 시간 측정용)
        long startTime = System.currentTimeMillis();
        logger.info("START!");  // 프로그램 시작 로그 출력

        // 1. 첫 번째 LLM 요청 - Java에 대한 기본 설명 생성 요청 (LLAMA 모델 사용)
        String aiResult = llm.sendPrompt(ModelCategory.LLAMA, "What is Java? No markdown, No escape character, nut shell");

        // 2. 스레드풀 생성 (최대 3개 스레드 동시에 처리 가능)
        ExecutorService executor = Executors.newFixedThreadPool(3);

        // 3. 각 요청을 처리할 작업(LLMTask) 생성 후 스레드풀에 제출
        LLMTask imageTask = new LLMTask(
                ModelCategory.FLUX,
                "Create thumbnail images for '%s'.".formatted(aiResult)
        );
        Future<String> imageFuture = executor.submit((Callable<String>) imageTask);
        // imageTask를 스레드풀에 넘겨서 실행 (비동기 실행), Future로 결과 받을 준비

        LLMTask reasoningTask = new LLMTask(
                ModelCategory.R1,
                "A fairly detailed and complex description of '%s'. Without markdown and escape characters...".formatted(aiResult)
        );
        Future<String> reasoningFuture = executor.submit((Callable<String>) reasoningTask);
        // 두 번째 작업 제출 (상세 설명 생성 요청)

        LLMTask reasoningTask2 = new LLMTask(
                ModelCategory.R1,
                "Write tips to help you get a job based on '%s'. Without markdown and escape characters...".formatted(aiResult)
        );
        Future<String> reasoningFuture2 = executor.submit((Callable<String>) reasoningTask2);
        // 세 번째 작업 제출 (취업 팁 생성 요청)

        // 4. 각 스레드의 결과(Future) 수집 (get() 호출로 해당 작업이 끝날 때까지 대기)
        String imageResult = imageFuture.get();
        String reasoningResult = reasoningFuture.get();
        String reasoningResult2 = reasoningFuture2.get();

        // 5. Slack으로 최종 결과 전송 (텍스트 2개 + 이미지 1개)
        slack.sendMessage("%s %s".formatted(reasoningResult, reasoningResult2), imageResult);

        // 6. 스레드풀 종료 (더 이상 추가 작업 안 받음, 남은 작업 완료 후 종료)
        executor.shutdown();

        // 7. 종료 로그 및 전체 소요 시간 출력
        logger.info("FINISH! %d".formatted(System.currentTimeMillis() - startTime));
    }
}

/**
 * 각 스레드가 실행할 작업 정의 클래스
 * LLMTask는 LLM 요청 1개를 담당
 * Runnable은 "실행 가능한 작업", Callable은 "결과를 반환하는 작업"
 */
class LLMTask implements Runnable, Callable<String> {
    private final LLM llm;  // 요청을 보낼 LLM 객체
    private final String prompt;  // 요청할 프롬프트
    private final ModelCategory model;  // 사용할 모델 종류
    private String result;  // 요청 결과 저장

    // 생성자 - 모델과 프롬프트를 받아서 작업 준비
    LLMTask(ModelCategory model, String prompt) throws NoEnvException {
        llm = new LLM();  // 각 작업마다 별도 LLM 객체 생성 (환경 변수 및 웹클라이언트 준비됨)
        this.prompt = prompt;
        this.model = model;
    }

    // Runnable 인터페이스 구현 (스레드풀에서 실행될 때 run() 호출)
    @Override
    public void run() {
        try {
            result = llm.sendPrompt(model, prompt);  // 실제 LLM 요청 수행
        } catch (Exception e) {
            throw new RuntimeException(e);  // 예외 발생 시 런타임 예외로 감싸서 던짐
        }
    }

    // Callable 인터페이스 구현 (스레드풀에서 결과를 받을 때 호출, Future로 감싸짐)
    @Override
    public String call() throws Exception {
        run();  // 같은 로직 실행 (run() 호출)
        return result;  // 작업 결과 반환
    }
}
