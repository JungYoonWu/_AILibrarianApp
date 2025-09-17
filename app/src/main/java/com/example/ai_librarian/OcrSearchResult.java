package com.example.ai_librarian;

import java.util.List;

public class OcrSearchResult {
    private String status;
    // ① 서버가 내려주는 JSON에 results[] 라는 배열이 생겼다.
    //    따라서 이 변수 하나가 가장 최상위에 들어와야 JSON 매핑이 정상 동작한다.
    private List<OcrResultItem> results;

    public String getStatus() {
        return status;
    }
    public List<OcrResultItem> getResults() {
        return results;
    }

    // ② JSON “results” 배열의 각 요소(아이템)를 담을 내부 클래스
    public static class OcrResultItem {
        // 서버에서 내려주는 키 이름과 정확히 동일하게 맞춰야 한다.
        private List<String> ocr_texts;      // OCR 라인별 원문
        private String ocr_string;           // 합쳐진 OCR 문자열
        private String corrected;            // 사전 교정된 문자열 (없으면 빈 문자열)
        private String used_query;           // 네이버 검색에 실제로 사용된 쿼리
        private List<String> candidates;     // 네이버 검색 후보 제목 리스트
        private String matched_title;        // 최종 추천 도서명 (없으면 빈 문자열)
        private int similarity_score;        // 추천 시점 유사도 점수

        // getter만 있어도 Gson이 필드 이름을 보고 자동으로 매핑한다.
        public List<String> getOcr_texts() { return ocr_texts; }
        public String getOcr_string() { return ocr_string; }
        public String getCorrected() { return corrected; }
        public String getUsed_query() { return used_query; }
        public List<String> getCandidates() { return candidates; }
        public String getMatched_title() { return matched_title; }
        public int getSimilarity_score() { return similarity_score; }
    }
}
