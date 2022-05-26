package com.haybble.access.impl;

import com.haybble.access.entity.BlockedIpTable;
import com.haybble.access.entity.UserAccessLog;
import com.haybble.access.repository.BlockedIpRepository;
import com.haybble.access.repository.UserAccessLogRepository;
import com.haybble.access.service.AccessLimitService;
import com.haybble.access.utils.AccessLimitUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

@Component
@Service
public  class AccessLimitImpl implements AccessLimitService {

    public static final String ACCESS_FILE = "accessFile";
    public static final String START = "start";
    public static final String DURATION = "duration";
    public static final String LIMIT = "limit";


    private AccessLimitUtils accessLimitUtils = new AccessLimitUtils();

    public UserAccessLogRepository userAccessLogRepository ;

    public  BlockedIpRepository blockedIpRepository;

    private Logger logger = LoggerFactory.getLogger(AccessLimitImpl.class);

    @Autowired
    public AccessLimitImpl(UserAccessLogRepository userAccessLogRepository, BlockedIpRepository blockedIpRepository) {
        this.userAccessLogRepository = userAccessLogRepository;
        this.blockedIpRepository = blockedIpRepository;
    }

    @Override
    public boolean loadFileToDb(Path filePath) {
        try {
            File file = new File(String.valueOf(filePath));
            Scanner myReader = getFileContents(file);
            myReader.close();
            return  true;
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public  HashMap<String, String> extractArguments(String[] args) {
        HashMap<String,String> params = new HashMap<>();
        if ((args != null) && (args.length == 4)) {
            for (String arg : args) {
                String[] splitFromEqual = arg.split("=");
                String key = splitFromEqual[0].substring(2);
                String value = splitFromEqual[1];
                params.put(key, value);
            }
        } else {
            logger.debug("Incomplete arguments passed.");
        }
        return params;

    }

    private Scanner getFileContents(File file) throws FileNotFoundException {
        Scanner myReader = new Scanner(file);
        List<UserAccessLog> extactedUserLogs = new ArrayList<>();
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
            if(userAccessLog!=null){
                extactedUserLogs.add(userAccessLog);
            }
            else{
                throw new FileNotFoundException("could not read out access log from file");
            }
        }
        if(!extactedUserLogs.isEmpty()){
            userAccessLogRepository.saveAll(extactedUserLogs);
        }
        return myReader;
    }

    public UserAccessLog setUserAccessLogObject(String date, String ip, String request, String status, String userAgent) {
        UserAccessLog userAccessLog = new UserAccessLog();
        LocalDateTime localDateTime = LocalDateTime.parse((date), DateTimeFormatter.ofPattern(accessLimitUtils.datePattern));
        userAccessLog.setDate(localDateTime);
        userAccessLog.setIp(ip);
        userAccessLog.setRequest(request);
        userAccessLog.setStatus(status);
        userAccessLog.setUserAgent(userAgent);
        return userAccessLog;
    }

    @Override
    public List<String> checkExceededLimitFromDb(String start, int limit, String duration) {
        String startDate = accessLimitUtils.formatStartDateTime(start);
        LocalDateTime startDateTime = LocalDateTime.parse(startDate, DateTimeFormatter.ofPattern(accessLimitUtils.datePattern));
        LocalDateTime endDateTime = accessLimitUtils.getEndDateTime(startDate, duration);
        int durationAllocated = accessLimitUtils.getDurationInHours(duration);
        List<String> requestsPerDurationGreatThanLimit = userAccessLogRepository.findAllByUserAccesslogIpbetweenStartDateAndEndDate(startDateTime, endDateTime, limit);
        saveBlockedIps(limit, durationAllocated, startDateTime, endDateTime, requestsPerDurationGreatThanLimit);
        return requestsPerDurationGreatThanLimit;
    }

    private void saveBlockedIps(int limit, int durationAllocated, LocalDateTime startDateTime, LocalDateTime endDateTime, List<String> requestsPerDurationGreatThanLimit) {
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

}
