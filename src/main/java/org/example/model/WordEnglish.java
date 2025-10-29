package org.example.model;

public class WordEnglish {
    private String textVietnamese;
    private String type;
    private String example;
    private String createdAt;
    private String updatedAt;
    private String transcription;

    // Getters
    public String getTextVietnamese() {
        return textVietnamese;
    }

    public String getType() {
        return type;
    }

    public String getExample() {
        return example;
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

    @Override
    public String toString() {
        return String.format(
                "VN: %s | Type: %s | Example: %s | Transcription: %s",
                textVietnamese, type, example, transcription
        );
    }
}
