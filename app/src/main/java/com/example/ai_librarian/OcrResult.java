package com.example.ai_librarian;

import java.util.List;    // 변경점: List import 추가

/**
 * OcrResult:
 *  - 서버로부터 OCR 결과(복수의 책 제목 리스트)를 받기 위한 모델 클래스
 */
public class OcrResult {
    // 변경점: 단일 title 대신 복수 제목 리스트를 사용
    private List<String> titles;

    /**
     * 변경점: 복수 제목을 반환합니다.
     * @return List of recognized titles
     */
    public List<String> getTitles() {
        return titles;
    }

    /**
     * 변경점: 서버 응답으로 받은 제목 리스트를 설정합니다.
     * @param titles List of titles from OCR
     */
    public void setTitles(List<String> titles) {
        this.titles = titles;
    }
}
