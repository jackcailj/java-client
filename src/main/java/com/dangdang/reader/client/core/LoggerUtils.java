package com.dangdang.reader.client.core;

import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by cailianjie on 2016-6-27.
 */
public class LoggerUtils {


    static Appender appender;
    static String appenderName="FileAppender";

    /*
    添加自定义appender
     */
    public static  void  addFileAppender(String fileName) throws IOException {
        PatternLayout layout = new PatternLayout();
        layout.setConversionPattern("%-d{yyyy-MM-dd HH:mm:ss}  [ %t:%r ] - [ %p ]  %m%n");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String date = sdf.format(new Date());

        SimpleDateFormat logNamesdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String lognameDate = logNamesdf.format(new Date());

        String fileFullName = "logs/"+date+"/"+fileName+lognameDate+".log";


        appender = new RollingFileAppender(layout,fileFullName,false);
        appender.setName(appenderName);

        Logger.getRootLogger().addAppender(appender);
    }

    /*
    获取appender名字
     */
    public static String getAppenderName() {
        return appenderName;
    }

    /*
    移除自定义的appender
     */
    public static void removeAppender(){
        if(appender!=null){
            Logger.getRootLogger().removeAppender(appender);
            appender=null;
        }
    }

    /*
    获取日志文件全路径，去掉后缀
     */
    public static String getLogsFile(){
        RollingFileAppender rollingFileAppender = (RollingFileAppender) Logger.getRootLogger().getAppender(appenderName);
        String fileName = rollingFileAppender.getFile();
        int index = fileName.lastIndexOf(".");
        String path = fileName.substring(0,index);

        return path;
    }

    public static String getStrackTrace(Throwable t){
         StringWriter stringWriter= new StringWriter();
         PrintWriter writer= new PrintWriter(stringWriter);
         t.printStackTrace(writer); StringBuffer buffer= stringWriter.getBuffer();
         return buffer.toString();
    }
}
