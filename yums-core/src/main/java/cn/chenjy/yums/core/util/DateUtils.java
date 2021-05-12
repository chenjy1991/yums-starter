package cn.chenjy.yums.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author ChenJY
 * @create 2021/5/10 10:25 上午
 * @DESCRIPTION
 */
public class DateUtils {

    public static String today(){
        return today("yyyyMMdd");
    }
    public static String today(String pattern){
        return LocalDate.now().format(DateTimeFormatter.ofPattern(pattern));
    }
}
