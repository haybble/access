package com.haybble.access.repository;

import com.haybble.access.entity.UserAccessLog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@SpringBootTest(args = {"--accessFile=C:\\Users\\jesufemio\\Downloads\\tx.txt", "--start=2022-01-01.00:00:11", "--duration=hourly", "--limit=100"})
@TestPropertySource(locations = "classpath:test.yaml")
class UserAccessLogRepositoryTest {

    @Autowired
    private UserAccessLogRepository userAccessLogRepository;


    @BeforeEach
    void setUp() {
        userAccessLogRepository.deleteAll();
        UserAccessLog entity = new UserAccessLog(1, LocalDateTime.parse("2022-09-05T07:50:55"), "192.168.1.1", "GET", "200", "TestUser1");
        UserAccessLog entity2 = new UserAccessLog(2, LocalDateTime.parse("2022-09-05T07:51:55"), "192.168.1.1", "GET", "200", "TestUser1");
        UserAccessLog entity3 = new UserAccessLog(3, LocalDateTime.parse("2022-09-05T07:52:55"), "192.168.1.2", "GET", "200", "TestUser1");
        UserAccessLog entity4 = new UserAccessLog(4, LocalDateTime.parse("2022-09-05T08:53:55"), "192.168.1.1", "GET", "200", "TestUser1");
        List<UserAccessLog> userList = new ArrayList<>();
        userList.add(entity);
        userList.add(entity2);
        userList.add(entity3);
        userList.add(entity4);
        userAccessLogRepository.saveAll(userList);
    }

    LocalDateTime start = LocalDateTime.parse("2022-09-05T07:50:55");
    LocalDateTime end = LocalDateTime.parse("2022-09-05T08:53:55");

    @Test
    void testFindDistinctIp() {
        List<String> result = userAccessLogRepository.findDistinctIp();
        System.out.println(result.size());
        assert (result.size() == 2);
    }

    @Test
    void testFindAllByUserAccesslogIpbetweenStartDateAndEndDate() {
        List<String> result = userAccessLogRepository.findAllByUserAccesslogIpbetweenStartDateAndEndDate(start, end, 1);
        assert (result.size() == 1);
        assert (result.get(0).equals("192.168.1.1"));
    }

    @Test
    void testFindCountByIp() {
        int result = userAccessLogRepository.findCountByIp(start, end, "192.168.1.1");
        assert (result == 3);
    }
}
