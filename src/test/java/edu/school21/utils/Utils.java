package edu.school21.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class Utils {
    /**
     * Метод форматирования даты в формат "yyyy-MM-dd HH:mm:ss:SS"
     *
     * @param timestamp - timestamp
     * @return formatted timestamp
     */
    public static String formatTimestamp(Timestamp timestamp) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SS");
        return formatter.format(timestamp);
    }
}