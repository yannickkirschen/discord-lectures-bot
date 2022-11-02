package sh.yannick.bot.lectures.calendar;

import lombok.*;

import java.time.LocalTime;

/**
 * Value object for a single lecture.
 *
 * @author Yannick Kirschen
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Lecture {
    private String title;
    private String location;

    private LocalTime begin;
    private LocalTime end;
}
