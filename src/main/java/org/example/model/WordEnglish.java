package org.example.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;

public class WordEnglish {
    private String textVietnamese;
    private String type;
    private String example;
    private String createdAt;
    private String updatedAt;
    private String transcription;
    private String lastViewedAt;
    private boolean favourite;
    

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public WordEnglish(String type, String transcription, String meaning, String example) {
        this.type = type;
        this.transcription = transcription;
        this.textVietnamese = meaning;
        this.example = example;
        this.favourite = false;
        this.createdAt = LocalDateTime.now().format(formatter);
        this.updatedAt = LocalDateTime.now().format(formatter);
        this.lastViewedAt = "";
    }

    //Setters
    public void updateLastViewedNow() {
    this.lastViewedAt = java.time.Instant.now().toString();  
    }
    public void setLastViewedAt(String lastViewedAt) {
        this.lastViewedAt = lastViewedAt;
    }

    public void setTextVietnamese(String textVietnamese) {
        this.textVietnamese = textVietnamese;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setTranscription(String transcription) {
        this.transcription = transcription;
    }

    // Getters
    public String getTextVietnamese() {
        return textVietnamese;
    }

    public String getExample() {
        return example;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public String getType() {
        return type;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getTranscription() {
        return transcription;
    }

    public String getLastViewedAt() {
        return lastViewedAt;
    }

    public long getLastViewedTimestamp() {
    if (lastViewedAt == null || lastViewedAt.isEmpty()) return 0L;
    try {
        LocalDateTime dt = LocalDateTime.parse(lastViewedAt);
        return dt.toInstant(ZoneOffset.UTC).toEpochMilli();
    } catch (DateTimeParseException ex) {
        // nếu format khác (ví dụ có timezone) thử dùng Instant.parse
        try {
            return java.time.Instant.parse(lastViewedAt).toEpochMilli();
            } catch (Exception e) {
            return 0L;
            }
        }
    }
}
