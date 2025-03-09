package util.logger;

import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

// MyLogger 클래스는 ILogger 인터페이스를 구현한 싱글톤 패턴의 로거 클래스
public class MyLogger implements ILogger {

    // Logger 인스턴스를 저장하는 변수 (final로 선언하여 변경 불가능)
    private final Logger logger;

    // MyLogger 클래스의 유일한 인스턴스를 저장할 변수 (싱글톤 패턴 적용)
    private static MyLogger instance;

    // private 생성자로 외부에서 직접 객체 생성 방지 (싱글톤 유지)
    private MyLogger() {
        // 콘솔에 로그를 출력하는 핸들러 생성
        ConsoleHandler handler = new ConsoleHandler();

        // 핸들러의 로그 레벨을 DEBUG로 설정 (DEBUG 수준 이상의 로그만 출력됨)
        handler.setLevel(MyLoggerLevel.DEBUG.level);

        // Logger 인스턴스를 현재 클래스명으로 생성
        logger = Logger.getLogger(MyLogger.class.getName());

        // 생성한 핸들러를 Logger에 추가 (콘솔로 로그를 출력하기 위함)
        logger.addHandler(handler);

        // Logger의 기본 로그 레벨을 DEBUG로 설정
        logger.setLevel(MyLoggerLevel.DEBUG.level);

        // 부모 핸들러 사용을 비활성화 (기본 핸들러로 로그가 중복 출력되는 것을 방지)
        logger.setUseParentHandlers(false);
    }

    // 유일한 MyLogger 인스턴스를 반환하는 메서드 (싱글톤 패턴 구현)
    public static MyLogger getLogger() {
        if (instance == null) { // 인스턴스가 없으면 생성
            instance = new MyLogger();
        }
        return instance; // 생성된 인스턴스를 반환
    }

    // 로그 레벨을 변경하는 메서드
    public void setLevel(MyLoggerLevel level) {
        this.logger.setLevel(level.level); // Logger의 로그 레벨을 변경
        System.out.println(level.level); // 변경된 로그 레벨을 콘솔에 출력
        System.out.println(logger.getLevel()); // 현재 Logger의 로그 레벨 출력
    }

    // DEBUG 레벨의 로그를 출력하는 메서드
    @Override
    public void debug(String msg) {
        this.logger.fine(msg); // fine() 메서드는 DEBUG 수준의 로그를 출력함
    }

    // INFO 레벨의 로그를 출력하는 메서드
    @Override
    public void info(String msg) {
        this.logger.info(msg); // info() 메서드는 INFO 수준의 로그를 출력함
    }

    // WARN 레벨의 로그를 출력하는 메서드
    @Override
    public void warn(String msg) {
        this.logger.warning(msg); // warning() 메서드는 WARN 수준의 로그를 출력함
    }

    // ERROR 레벨의 로그를 출력하는 메서드
    @Override
    public void error(String msg) {
        this.logger.severe(msg); // severe() 메서드는 ERROR 수준의 로그를 출력함
    }
}

// ILogger 인터페이스 정의 (로그 메시지를 출력하는 4가지 메서드 선언)
interface ILogger {
    void debug(String msg); // DEBUG 레벨 로그 출력 메서드
    void info(String msg);  // INFO 레벨 로그 출력 메서드
    void warn(String msg);  // WARN 레벨 로그 출력 메서드
    void error(String msg); // ERROR 레벨 로그 출력 메서드
}
