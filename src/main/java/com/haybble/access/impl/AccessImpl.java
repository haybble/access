package com.haybble.access.impl;

import com.haybble.access.entity.BlockedIpTable;
import com.haybble.access.entity.UserAccessLog;
import com.haybble.access.repository.BlockedIpRepository;
import com.haybble.access.repository.UserAccessLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

@Configuration
public class AccessImpl {

    public static final String datePattern = "yyyy-MM-dd HH:mm:ss";

    private static UserAccessLogRepository userAccessLogRepository;

    private static BlockedIpRepository blockedIpRepository;

    private static Logger logger = LoggerFactory.getLogger(AccessImpl.class);

    public AccessImpl(UserAccessLogRepository userAccessLogRepository, BlockedIpRepository blockedIpRepository) {
        this.userAccessLogRepository = userAccessLogRepository;
        this.blockedIpRepository = blockedIpRepository;
    }

    public static boolean loadUserAccessLogToDb(Path filePath) {
        try {
            File file = new File(String.valueOf(filePath));
            Scanner myReader = getFileContents(file);
            myReader.close();
            return true;
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
            return false;
        }
    }

    private static Scanner getFileContents(File file) throws FileNotFoundException {
        Scanner myReader = new Scanner(file);
        while (myReader.hasNextLine()) {
            String data = myReader.nextLine();
            String sp = data.replace("|", ",");
            String[] splited = sp.split(",");
            String date = splited[0].substring(0, 19);
            String ip = splited[1];
            String request = splited[2];
            String status = splited[3];
            String userAgent = splited[4];
            UserAccessLog userAccessLog = setUserAccessLogObject(date, ip, request, status, userAgent);
            userAccessLogRepository.save(userAccessLog);
        }
        return myReader;
    }

    private static UserAccessLog setUserAccessLogObject(String date, String ip, String request, String status, String userAgent) {
        UserAccessLog userAccessLog = new UserAccessLog();
        LocalDateTime localDateTime = LocalDateTime.parse((date), DateTimeFormatter.ofPattern(datePattern));
        userAccessLog.setDate(localDateTime);
        userAccessLog.setIp(ip);
        userAccessLog.setRequest(request);
        userAccessLog.setStatus(status);
        userAccessLog.setUserAgent(userAgent);
        return userAccessLog;
    }

    public static List<String> checkExceededLimit(String start, int limit, String duration) {
        String startDate = formatStartDateTime(start);
        LocalDateTime startDateTime = LocalDateTime.parse(startDate, DateTimeFormatter.ofPattern(datePattern));
        LocalDateTime endDateTime = getEndDateTime(startDate, duration);
        int durationAllocated = getDurationInHours(duration);
        List<String> requestsPerDurationGreatThanLimit = userAccessLogRepository.findAllByUserAccesslogIpbetweenStartDateAndEndDate(startDateTime, endDateTime, limit);
        saveBlockedIps(limit, durationAllocated, startDateTime, endDateTime, requestsPerDurationGreatThanLimit);
        return requestsPerDurationGreatThanLimit;
    }

    private static void saveBlockedIps(int limit, int durationAllocated, LocalDateTime startDateTime, LocalDateTime endDateTime, List<String> requestsPerDurationGreatThanLimit) {
        requestsPerDurationGreatThanLimit.stream().forEach(ip -> {
            int count = 0;
            BlockedIpTable blockedIpTable = new BlockedIpTable();
            blockedIpTable.setIp(ip);
            blockedIpTable.setComment("Request during the duration is greater than limit of " + limit);
//            if(userAccessLogRepository.findCountByIp(startDateTime,endDateTime,ip).isPresent()){
            count = userAccessLogRepository.findCountByIp(startDateTime, endDateTime, ip);
            blockedIpTable.setRequestNumber(count);
            logger.warn("This IP {} has been blocked for making {} requests more than the set limit of {} within this duration {}", ip, String.valueOf(count), String.valueOf(limit), String.valueOf(durationAllocated));
            blockedIpRepository.save(blockedIpTable);
        });
    }

    public static LocalDateTime getEndDateTime(String startDate, String duration) {
        return LocalDateTime.parse(startDate, DateTimeFormatter.ofPattern(datePattern)).plusHours(getDurationInHours(duration));

    }

    public static int getDurationInHours(String duration) {
        return (duration == "hourly") ? 1 : 24;
    }

    public static String formatStartDateTime(String startDate) {
        return startDate.replace(".", " ");
    }


}
