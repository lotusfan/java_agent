package com.pre;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @title: LogEnhance
 * @description:
 * @author: zhangfan
 * @data: 2022年08月15日 19:57
 */
public class LogEnhance {

    public static String filePath;
    public static ThreadLocal<LogEnhance> logEnhanceThreadLocal = ThreadLocal.withInitial(() -> new LogEnhance().init());
    public static int methodNameColumnLength = 100;
    public static int timeColumnLength = 25;
    public static int uniqueKeyColumnLength = 35;
    public static long greaterMS = -1;
    public static long threeSeconds = 3 * 1000;
    public static long threeMinutes = 3 * 60 * 1000;

    LinkedList<Long> point;
    File file;
    BufferedWriter bw;
    String uniqueKey;

    public LogEnhance init() {

        file = new File(filePath);
        point = new LinkedList<>();
        uniqueKey = UUID.randomUUID().toString().replace("-", "").toUpperCase();
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            bw = new BufferedWriter(new FileWriter(file, true));
            String threadName = Thread.currentThread().getName();
            String t = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

            bw.append(tableHead(new StringBuffer()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public void point() {
        point.add(System.currentTimeMillis());
    }

    public long end() {
        return System.currentTimeMillis() - point.pollLast();
    }

    public void printEnd() {

        long elapsed = end();
        if (elapsed < greaterMS) {
            return;
        }
        StackTraceElement ste = Thread.currentThread().getStackTrace()[2];
        int j = point.size();
        try {
            bw.append(tableLine(new StringBuffer(), ste.getClassName() + "#" + ste.getMethodName(), elapsed, uniqueKey));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (j == 0) {
            logEnhanceThreadLocal.remove();
            try {
                bw.append(tableTail(new StringBuffer()));
                bw.flush();
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static StringBuffer tableLine(StringBuffer sb, String methodName, Long elapsedTime, String uniqueKey) {

        sb.append("\n|");
        int ml = methodName.length();
        if (ml > methodNameColumnLength) {
            methodName = methodName.substring(ml - methodNameColumnLength);
        }
        sb.append(methodName);
        for (int i = ml; i < methodNameColumnLength; i++) {
            sb.append(" ");
        }
        sb.append("| ");
        sb.append(elapsedTime);
        sb.append("ms");
        if (elapsedTime > threeSeconds) {
            sb.append(" ≈");
            sb.append(elapsedTime / 1000);
            sb.append("s");
        }
        if (elapsedTime > threeMinutes) {
            sb.append(" ≈");
            sb.append(elapsedTime / 60 / 1000);
            sb.append("m");
        }

        int tl = methodNameColumnLength + timeColumnLength;
        for (int i = sb.length(); i < tl; i++) {
            sb.append(" ");
        }
        sb.append("| ");
        sb.append(uniqueKey);

        return sb;
    }

    public static StringBuffer tableHead(StringBuffer sb) {

        tableTail(sb);
        return sb;
    }

    public static StringBuffer tableTail(StringBuffer sb) {

        sb.append("\n|");
        sb.append(
                Stream.generate(() -> "-")
                        .limit(methodNameColumnLength + timeColumnLength + uniqueKeyColumnLength)
                        .collect(Collectors.joining()));

        return sb;
    }

    public static LogEnhance get() {
        return logEnhanceThreadLocal.get();
    }
}
