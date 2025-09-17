// com/example/ai_librarian/OcrSearchResponse.java
package com.example.ai_librarian;

import java.util.List;

// 서버가 반환하는 JSON 최상단 구조: {"status":"success","results":[ ... ]}
public class OcrSearchResponse {
    private String status;
    private List<OcrItem> results;   // ← 서버의 "results" 배열

    // getter / setter
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public List<OcrItem> getResults() {
        return results;
    }
    public void setResults(List<OcrItem> results) {
        this.results = results;
    }

    // 내부 클래스: results 배열 안의 객체 (서버에서 each spine마다 만들어 주는 Map 형태)
    public static class OcrItem {
        private List<String> ocr_texts;
        private String ocr_string;
        private String corrected;
        private String used_query;
        private List<String> candidates;
        private String matched_title;
        private int similarity_score;

        // 각 필드에 대한 getter/setter
        public List<String> getOcr_texts() {
            return ocr_texts;
        }
        public void setOcr_texts(List<String> ocr_texts) {
            this.ocr_texts = ocr_texts;
        }

        public String getOcr_string() {
            return ocr_string;
        }
        public void setOcr_string(String ocr_string) {
            this.ocr_string = ocr_string;
        }

        public String getCorrected() {
            return corrected;
        }
        public void setCorrected(String corrected) {
            this.corrected = corrected;
        }

        public String getUsed_query() {
            return used_query;
        }
        public void setUsed_query(String used_query) {
            this.used_query = used_query;
        }

        public List<String> getCandidates() {
            return candidates;
        }
        public void setCandidates(List<String> candidates) {
            this.candidates = candidates;
        }

        public String getMatched_title() {
            return matched_title;
        }
        public void setMatched_title(String matched_title) {
            this.matched_title = matched_title;
        }

        public int getSimilarity_score() {
            return similarity_score;
        }
        public void setSimilarity_score(int similarity_score) {
            this.similarity_score = similarity_score;
        }
    }
}
