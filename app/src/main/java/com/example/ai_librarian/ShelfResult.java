package com.example.ai_librarian;

import java.util.List;

/**
 *  Server /shelf-upload 응답을 매핑하기 위한 모델 클래스
 */
public class ShelfResult {
    private String status;
    private String shelf_name;
    private List<CellResult> results;

    public String getStatus(){return status;}
    public String getShelf_name(){return shelf_name;}
    public List<CellResult> getResults(){return results;}

    /**
     * 각 셀(row,col)에 대한 ocr된 책 리스트를 담는 내부 클래스
     */
    public static class CellResult{
        private int row;                //셀의 행
        private int col;                //셀의 열
        private List<BookItem> books;   //검출된 spine별 ocr결과 리스트

        public int getRow(){return row;}
        public int getCol(){return col;}
        public List<BookItem> getBooks(){return books;}
    }

    /**
     * 각 책(spine)별 ocr결과
     */
    public static class BookItem{
        private int order;              //선반 내 책 순서(왼쪽부터 1,2,3,...)
        private String ocr_string;      //합쳐진 OCR 문자열
        private List<String> ocr_texts; //OCR 라인별 원문

        public int getOrder(){return order;}
        public String getOcr_string(){return ocr_string;}
        public List<String> getOcr_texts(){return ocr_texts;}
    }
}