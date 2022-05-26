package com.haybble.access;

import com.haybble.access.service.AccessLimitService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(args = {"--accessFile=C:\\Users\\jesufemio\\Downloads\\tx.txt" ,"--start=2022-01-01.00:00:11","--duration=hourly", "--limit=100"  })
class AccessApplicationTests {
    @Autowired
    private AccessLimitService accessLimitService;

    @Test
    void contextLoads() {

    }



}
