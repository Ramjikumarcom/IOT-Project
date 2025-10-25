package com.client.client.model;

public class SecureMessage {
    private int seq;
    private String payload;
    private String checksum;

    public SecureMessage() {}

    public SecureMessage(int seq, String payload, String checksum) {
        this.seq = seq;
        this.payload = payload;
        this.checksum = checksum;
    }

    public int getSeq() { return seq; }
    public void setSeq(int seq) { this.seq = seq; }

    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }

    public String getChecksum() { return checksum; }
    public void setChecksum(String checksum) { this.checksum = checksum; }
}
