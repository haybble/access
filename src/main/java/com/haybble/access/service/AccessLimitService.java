package com.haybble.access.service;

import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

@Service
public interface AccessLimitService {
       boolean loadFileToDb(Path filePath);
       HashMap<String,String> extractArguments(String[] args);
       List<String> checkExceededLimitFromDb(String start, int limit, String duration);
}
