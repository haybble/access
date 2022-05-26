package com.haybble.access;

import com.haybble.access.service.AccessLimitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;


@SpringBootApplication
public class AccessLimitApplication implements CommandLineRunner {

    public static final String ACCESS_FILE = "accessFile";
    public static final String START = "start";
    public static final String DURATION = "duration";
    public static final String LIMIT = "limit";


@Autowired
    private AccessLimitService accessLimitService;


    public AccessLimitApplication(AccessLimitService accessLimitService) {
        this.accessLimitService = accessLimitService;
    }

    public static void main(String[] args) {
        SpringApplication.run(AccessLimitApplication.class, args);
    }
        @Override
        public void run (String...args)  {
            Path assessFilePath;
            String startDate;
            String duration;
            int limit;
            HashMap<String, String> arguments = accessLimitService.extractArguments(args);
            if (!arguments.isEmpty()) {
                assessFilePath = Paths.get(arguments.get(ACCESS_FILE));
                startDate = arguments.get(START);
                duration = arguments.get(DURATION);
                limit = Integer.valueOf(arguments.get(LIMIT));
                 accessLimitService.loadFileToDb(assessFilePath);
                 accessLimitService.checkExceededLimitFromDb(startDate, limit, duration);
                System.out.println("Done processing");

            } else {
                throw new RuntimeException("ACCESS FILE, START, DURATION, lIMIT are not defined");
            }
        }


}
