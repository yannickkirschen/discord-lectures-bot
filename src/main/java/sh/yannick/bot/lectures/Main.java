package sh.yannick.bot.lectures;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        ((Runnable) SpringApplication.run(Main.class, args).getBean("bot")).run();
    }
}
