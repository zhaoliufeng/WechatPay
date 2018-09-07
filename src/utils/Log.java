package utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss:SSS");
    public static void info(String TAG, String logMsg){
        System.out.println(String.format("LogInfo: %s: %s: %s",dateFormat.format(new Date()), TAG, logMsg));
    }
}
