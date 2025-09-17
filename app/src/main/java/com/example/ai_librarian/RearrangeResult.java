package com.example.ai_librarian;

import java.util.List;

/**
 * RearrangeResult:
 *  - /rearrange 엔드포인트의 JSON 응답을 매핑하기 위한 모델 클래스
 *
 * 서버가 반환하는 예시 JSON:
 * {
 *   "status":"success",
 *   "spines":[ "iVBORw0K…", "iVBORw0K…", … ],
 *   "order":"asc"
 * }
 */
public class RearrangeResult {
    private String status;
    private List<String> spines;
    private String order;
    private String compose;

    /**
     * 추출된 spine 이미지들의 Base64 리스트를 반환합니다.
     * @return List of Base64-encoded spine images
     */
    public List<String> getSpines() {
        return spines;
    }

    /**
     * 서버로부터 받은 Base64 spine 리스트를 설정합니다.
     * @param spines List of Base64-encoded spine images
     */
    public void setSpines(List<String> spines) {
        this.spines = spines;
    }

    /**
     * 요청한 정렬 순서("asc" 또는 "desc")를 반환합니다.
     * @return "asc" 또는 "desc"
     */
    public String getOrder() {
        return order;
    }

    /**
     * 서버로부터 받은 정렬 순서를 설정합니다.
     * @param order "asc" 또는 "desc"
     */
    public void setOrder(String order) {
        this.order = order;
    }

    public void setStatus(String status){
        this.status = status;
    }

    public String getStatus(){
        return status;
    }

    public String getCompose(){
        return compose;
    }

    public void setCompose(String compose){
        this.compose = compose;
    }
}
