package sh.yannick.bot.lectures.calendar;

import lombok.extern.slf4j.Slf4j;
import net.fortuna.ical4j.data.*;
import net.fortuna.ical4j.model.*;
import org.apache.hc.client5.http.ClientProtocolException;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.*;
import org.apache.hc.core5.http.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Provides access to an ICAL calendar (ICS) that can be downloaded via a web resource.
 * <p>
 * This bean requires a property <code>calendar.url</code> pointing to the ICS file.
 *
 * @author Yannick Kirschen
 * @since 1.0.0
 */
@Slf4j
@Repository
public class LecturesProviderICAL implements LecturesProvider {
    @Value("${calendar.url}")
    private String url;

    @Override
    public Lectures retrieve(LocalDate date) throws NoLecturesFoundException {
        Calendar calendar = getCalendar(url);

        log.info("Today's lecture plan:");
        List<Lecture> lectures = calendar.getComponents().stream().filter(c -> {
            Property start = c.getProperty("DTSTART");
            if (start != null) {
                LocalDateTime dateTime = parseDateTime(start.getValue());
                return dateTime.toLocalDate().equals(date);
            }
            return false;
        }).map(c -> {
            LocalTime begin = parseDateTime(c.getProperty("DTSTART").getValue()).toLocalTime();
            LocalTime end = parseDateTime(c.getProperty("DTEND").getValue()).toLocalTime();
            String title = c.getProperty("SUMMARY").getValue();
            String location = c.getProperty("LOCATION").getValue();

            log.info("    {} - {}: {} in {}", begin, end, title, location);
            return new Lecture(title, location, begin, end);
        }).toList();

        if (lectures.isEmpty()) {
            log.warn("   No lectures today!");
            throw new NoLecturesFoundException();
        }

        return new Lectures(date, lectures);
    }

    /**
     * Parses the terrible timestamp we receive from the ICS file to a {@link LocalDateTime}.
     *
     * @param value terrible timestamp from the ICS file
     * @return a {@link LocalDateTime} you can actually work with
     */
    private LocalDateTime parseDateTime(String value) {
        String[] dateTime = value.split("T");
        LocalDate date = LocalDate.parse(dateTime[0], DateTimeFormatter.BASIC_ISO_DATE);

        int hour = Integer.parseInt(dateTime[1].substring(0, 2));
        int minute = Integer.parseInt(dateTime[1].substring(2, 4));
        int second = Integer.parseInt(dateTime[1].substring(4, 6));

        return LocalDateTime.of(date, LocalTime.of(hour, minute, second));
    }

    private Calendar getCalendar(String url) {
        log.info("Downloading ICS calendar file: {}", url);

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet get = new HttpGet(url);
            ClassicHttpResponse response = client.execute(get);

            int status = response.getCode();
            if (status >= HttpStatus.SC_SUCCESS && status < HttpStatus.SC_REDIRECTION) {
                log.info("Received ICS file.");

                HttpEntity entity = response.getEntity();
                InputStream in = entity.getContent();
                CalendarBuilder builder = new CalendarBuilder();
                try {
                    return builder.build(in);
                } catch (ParserException e) {
                    log.error("Unable to parse ICS file: {}", e.getMessage());
                    throw new RuntimeException(e);
                }
            } else {
                log.error("Bad response from server while downloading ICS file. Status: {}", status);
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
        } catch (IOException e) {
            log.error("Error while downloading ICS file: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
