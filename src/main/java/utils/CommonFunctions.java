package utils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

public class CommonFunctions {

    public static LocalDate calculateNextDate(String dayOfWeek) {
        LocalDate d = LocalDate.now();

        switch (dayOfWeek.toUpperCase()) {
            case "MONDAY":
                return d.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
            case "TUESDAY":
                return d.with(TemporalAdjusters.next(DayOfWeek.TUESDAY));
            case "WEDNESDAY":
                return d.with(TemporalAdjusters.next(DayOfWeek.WEDNESDAY));
            case "THURSDAY":
                return d.with(TemporalAdjusters.next(DayOfWeek.THURSDAY));
            case "FRIDAY":
                return d.with(TemporalAdjusters.next(DayOfWeek.FRIDAY));
            case "SATURDAY":
                return d.with(TemporalAdjusters.next(DayOfWeek.SATURDAY));
            case "SUNDAY":
                return d.with(TemporalAdjusters.next(DayOfWeek.SUNDAY));
            default:
                return null;
        }
    }

}
