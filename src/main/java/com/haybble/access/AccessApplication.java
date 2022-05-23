package com.haybble.access;

import com.haybble.access.impl.AccessImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.nio.file.Path;
import java.nio.file.Paths;


@SpringBootApplication
public class AccessApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccessApplication.class, args);
        Path assessFilePath;
        String startDate;
        String duration;
        int limit;
// this is for Test only
//         assessFilePath = Paths.get(new ClassPathResource("user_access").getFile());
//         startDate = "2022-01-01.00:00:11";
//         duration ="hourly";
//         limit = 100;
        if ((args != null) && (args.length == 4)) {
            assessFilePath = Paths.get(String.valueOf(args[0]));
            startDate = String.valueOf(args[1]);
            duration = String.valueOf(args[2]);
            limit = Integer.valueOf(args[3]);
        } else {
            throw new RuntimeException("ACCESSFILE, START, DURATION, lIMIT,not defined");
        }
        AccessImpl.loadUserAccessLogToDb(assessFilePath);
        AccessImpl.checkExceededLimit(startDate, limit, duration);
        System.out.println("Done processing");
    }

}
