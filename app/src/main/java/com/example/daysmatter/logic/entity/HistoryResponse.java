package com.example.daysmatter.logic.entity;

import java.util.List;


public class HistoryResponse {
    private String reason;
    private List<HistoryEvent> result;
    private int error_code;

    public HistoryResponse() {}

    public HistoryResponse(String reason, List<HistoryEvent> result, int error_code) {
        this.reason = reason;
        this.result = result;
        this.error_code = error_code;
    }

    // Getter and Setter
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public List<HistoryEvent> getResult() { return result; }
    public void setResult(List<HistoryEvent> result) { this.result = result; }

    public int getError_code() { return error_code; }
    public void setError_code(int error_code) { this.error_code = error_code; }
}