package sh.yannick.bot.lectures.message;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sh.yannick.bot.lectures.calendar.*;

import java.time.LocalDate;
import java.time.format.*;
import java.util.*;

/**
 * Generates a localized message based on {@link Lectures} containing all lectures of a day.
 *
 * @author Yannick Kirschen
 * @since 1.0.0
 */
@Slf4j
@Service
public class MessageGenerator {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Generates a localized message with all lectures of a day.
     *
     * @param lectures lectures of a given day
     * @param locale   locale the message should have
     * @return a message
     */
    public String generate(Lectures lectures, Locale locale) {
        StringBuilder builder = new StringBuilder();

        LocalDate date = lectures.getDate();
        ResourceBundle messages = ResourceBundle.getBundle("messages", locale);
        builder
                .append("**")
                .append(messages.getString("lecture.plan.for"))
                .append(" ")
                .append(date.getDayOfWeek().getDisplayName(TextStyle.FULL, locale))
                .append(", ");

        if (locale.equals(new Locale("de"))) {
            builder
                    .append(messages.getString("date.divider"))
                    .append(" ")
                    .append(date.getDayOfMonth())
                    .append(messages.getString("day.separator"))
                    .append(" ")
                    .append(date.getMonth().getDisplayName(TextStyle.FULL, locale));
        } else {
            builder
                    .append(date.getMonth().getDisplayName(TextStyle.FULL, locale))
                    .append(" ")
                    .append(date.getDayOfMonth())
                    .append(",");
        }

        builder
                .append(" ")
                .append(date.getYear())
                .append(":**")
                .append("\n");

        for (Lecture lecture : lectures.getLectures()) {
            builder
                    .append(lecture.getBegin().format(FORMATTER))
                    .append(" - ")
                    .append(lecture.getEnd().format(FORMATTER))
                    .append(": ")
                    .append(lecture.getTitle())
                    .append(" in ")
                    .append(lecture.getLocation())
                    .append("\n");
        }

        String message = builder.toString();
        log.info("Message is: \n{}", message);
        return message;
    }
}
