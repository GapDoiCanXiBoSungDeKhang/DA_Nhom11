package org.example.model;

public class WordEnglish {
    private String textVietnamese;
    private String type;
    private String example;
    private String createdAt;
    private String updatedAt;
    private String transcription;
    private boolean favourite;

    public WordEnglish() 
    {
        
    }

    public WordEnglish(String type, String transcription, String meaning, String example) {
    this.type = type;
    this.transcription = transcription;
    this.textVietnamese = meaning;
    this.example = example;
    }
    //Setters
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

    @Override
    public String toString() {
        return String.format(
                "VN: %s | Type: %s | Example: %s | Transcription: %s",
                textVietnamese, type, example, transcription
        );
    }
}
