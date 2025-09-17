package com.example.ai_librarian;

import java.io.Serializable;
import java.util.List;

public class ShelfDetailResponse {
    private String status;
    private List<Cell> cells;

    public String getStatus() { return status; }
    public List<Cell> getCells() { return cells; }

    public static class Cell implements Serializable {
        private int cell_row;
        private int cell_col;
        private String image_url;
        private List<Book> books;

        public int getCell_row() { return cell_row; }
        public int getCell_col() { return cell_col; }
        public String getImage_url() { return image_url; }
        public List<Book> getBooks() { return books; }
    }

    public static class Book implements Serializable {
        private int book_id;
        private String final_title;
        private String ocr_text;

        public int getBook_id() { return book_id; }
        public String getFinal_title() { return final_title; }
        public String getOcr_text() { return ocr_text; }
        public void setFinal_title(String final_title) { this.final_title = final_title; }
    }
}