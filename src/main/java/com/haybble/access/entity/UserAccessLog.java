package com.haybble.access.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity(name = "USER_ACCESS_LOG")
@Table(name = "USER_ACCESS_LOG")
public class UserAccessLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userAccessId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime date;
    private String ip;
    private String request;
    private String status;
    private String userAgent;

    public UserAccessLog() {

    }

    public UserAccessLog(int userAccessId, LocalDateTime date, String ip, String request, String status, String userAgent) {
        this.userAccessId = userAccessId;
        this.date = date;
        this.ip = ip;
        this.request = request;
        this.status = status;
        this.userAgent = userAgent;
    }

    public int getUserAccessId() {
        return userAccessId;
    }

    public void setUserAccessId(int userAccessId) {
        this.userAccessId = userAccessId;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
}
