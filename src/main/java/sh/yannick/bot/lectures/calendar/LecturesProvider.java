package sh.yannick.bot.lectures.calendar;

import java.time.LocalDate;

/**
 * Provides {@link Lectures}.
 * <p>
 * Implementations of this interface provide all lectures of a given day. To do so, they may query any kind of
 * datasource.
 *
 * @author Yannick Kirschen
 * @since 1.0.0
 */
@FunctionalInterface
public interface LecturesProvider {
    /**
     * Retrieves lectures of a given day.
     *
     * @param date date to get the lectures for.
     * @return lectures for the given date
     * @throws NoLecturesFoundException if there are no lectures in the calendar for the given date
     */
    Lectures retrieve(LocalDate date) throws NoLecturesFoundException;
}
