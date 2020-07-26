package in.gotiit.bigbro.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Util {

    static DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM");

    public static String instantToText(String instant) {
        LocalDateTime ldt = LocalDateTime.ofInstant(Instant.parse(instant), ZoneId.systemDefault());
        LocalDate today = LocalDate.now();
        if(ldt.isBefore(today.atStartOfDay())){
            if(ldt.getYear()<today.getYear()){
                return String.valueOf(ldt.getYear());
            }
            return ldt.format(dateFormatter);
        }
        return ldt.format(timeFormatter);
    }

    public static String atWrapUsername(String username) {
        return "@" + username;
    }

}
