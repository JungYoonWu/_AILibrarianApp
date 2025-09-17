package com.example.ai_librarian;

public class CorrectTitleRequest {
    private final String ocr_text;

    public CorrectTitleRequest(String ocr_text) {
        this.ocr_text = ocr_text;
    }
}
