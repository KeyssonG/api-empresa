package keysson.apis.empresa.config;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.sql.Date;

public class FormatDate {

    public static Date formatDate(String date) {
        if (date == null || date.isBlank()) return null;

        List<String> patterns = Arrays.asList("yyyy-MM-dd", "ddMMyyyy");

        for (String pattern : patterns) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(pattern);
                sdf.setLenient(false);
                java.util.Date parsedDate = sdf.parse(date);
                return new Date(parsedDate.getTime());
            } catch (ParseException ignored) {}
        }

        throw new IllegalArgumentException("Formato de data inválido: " + date);
    }
}