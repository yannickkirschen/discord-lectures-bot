package sh.yannick.bot.lectures;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sh.yannick.bot.lectures.calendar.*;
import sh.yannick.bot.lectures.message.*;

import java.time.LocalDate;
import java.util.Locale;

/**
 * Lectures bot telling you all lectures of a given day.
 * <p>
 * Basically, this bot works as follows:
 * <ol>
 *     <li>Get today's lecture plan ({@link LecturesProvider})</li>
 *     <li>Generate a localized message with all lectures({@link MessageGenerator})</li>
 *     <li>Send the message to a Discord channel ({@link MessageSender})</li>
 * </ol>
 *
 * @author Yannick Kirschen
 * @since 1.0.0
 */
@Slf4j
@Service("bot")
@RequiredArgsConstructor
public class LecturesBot implements Runnable {
    private final LecturesProvider lecturesProvider;
    private final MessageGenerator messageGenerator;
    private final MessageSender messageSender;

    @Value("${messages.locale}")
    private String locale;

    @Override
    public void run() {
        LocalDate date = LocalDate.now();
        log.info("Retrieving lecture plan for {}.", date);

        try {
            Lectures lectures = lecturesProvider.retrieve(date);
            String message = messageGenerator.generate(lectures, new Locale(locale));
            messageSender.send(message);
        } catch (NoLecturesFoundException e) {
            log.error("There are no lectures for today ({}).", date);
        }
    }
}
