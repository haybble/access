package com.haybble.access.service;

import com.haybble.access.impl.AccessLimitImpl;
import com.haybble.access.repository.BlockedIpRepository;
import com.haybble.access.repository.UserAccessLogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AccessLimitServiceTest {

    @Mock
    UserAccessLogRepository userAccessLogRepository;
    @Mock
    BlockedIpRepository blockedIpRepository;
    @InjectMocks
    AccessLimitImpl accessLimitImpl;




    @Test
    void testLoadFileToFb() throws URISyntaxException {
        Path assessFilePath = Paths.get(getClass().getClassLoader().getResource("tx.txt").toURI());
        assertNotNull(assessFilePath);
        Boolean result = accessLimitImpl.loadFileToDb(assessFilePath);
        assertEquals(result, true);
    }

    @Test
    void testCheckExceededLimitFromDb() {
        String start = "2022-01-01.00:00:11";
        String duration = "hourly";
        int limit = 2;
        List<String> repoList = new ArrayList<>();
        repoList.add("192.168.1.2");
        repoList.add("192.168.1.1");
        when(userAccessLogRepository.findAllByUserAccesslogIpbetweenStartDateAndEndDate(any(LocalDateTime.class),any(LocalDateTime.class),anyInt())).thenReturn(repoList);
        List<String> result = accessLimitImpl.checkExceededLimitFromDb(start, limit, duration);
        assertEquals(result.size(), repoList.size());
    }


}
