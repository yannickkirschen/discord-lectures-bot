package sh.yannick.bot.lectures.calendar;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Value object for all lectures of a given day.
 *
 * @author Yannick Kirschen
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Lectures {
    private LocalDate date;
    private List<Lecture> lectures;
}
