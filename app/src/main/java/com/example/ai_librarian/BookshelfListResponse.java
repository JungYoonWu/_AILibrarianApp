package com.example.ai_librarian;

import java.util.List;

public class BookshelfListResponse {
    private String status;
    private List<BookshelfItem> bookshelves;

    public String getStatus() { return status; }
    public List<BookshelfItem> getBookshelves() { return bookshelves; }

    // '책장' 하나의 요약 정보를 담는 내부 클래스
    public static class BookshelfItem {
        private int shelf_id;
        private String shelf_name;
        private int rows;
        private int cols;

        public int getShelf_id() { return shelf_id; }
        public String getShelf_name() { return shelf_name; }
        public int getRows() { return rows; }
        public int getCols() { return cols; }
    }
}