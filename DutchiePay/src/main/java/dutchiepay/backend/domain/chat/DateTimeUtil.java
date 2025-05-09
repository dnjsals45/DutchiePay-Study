package dutchiepay.backend.domain.chat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateTimeUtil {
    public static LocalDateTime toLocalDateTime(String dateStr, String timeStr) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate localDate = LocalDate.parse(dateStr, dateFormatter);

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("a h:mm", Locale.KOREA);
        LocalTime localTime = LocalTime.parse(timeStr, timeFormatter);

        return LocalDateTime.of(localDate, localTime);
    }
}