package com.haybble.access.entity;

import lombok.Getter;

import javax.persistence.*;

@Getter

@Entity(name = "BLOCKED_IP_TABLE")
@Table(name = "BLOCKED_IP_TABLE")
public class BlockedIpTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int blockedIpTableId;
    private  String ip;
    private int requestNumber;
    private String comment ;

    public BlockedIpTable() {
    }

    public BlockedIpTable(int blockedIpTableId, String ip, int requestNumber, String comment) {
        this.blockedIpTableId = blockedIpTableId;
        this.ip = ip;
        this.requestNumber = requestNumber;
        this.comment = comment;
    }

    public int getBlockedIpTableId() {
        return blockedIpTableId;
    }

    public void setBlockedIpTableId(int blockedIpTableId) {
        this.blockedIpTableId = blockedIpTableId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getRequestNumber() {
        return requestNumber;
    }

    public void setRequestNumber(int requestNumber) {
        this.requestNumber = requestNumber;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
