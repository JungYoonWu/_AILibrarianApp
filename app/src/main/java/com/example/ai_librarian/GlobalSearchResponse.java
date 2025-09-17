package com.example.ai_librarian;

import java.util.List;

public class GlobalSearchResponse {
    private String status;
    private List<SearchResultItem> search_results;

    public List<SearchResultItem> getSearchResults() { return search_results; }

    public static class SearchResultItem {
        private String found_title;
        private String shelf_name;
        private int cell_row;
        private int cell_col;

        public String getFound_title() { return found_title; }
        public String getShelf_name() { return shelf_name; }
        public int getCell_row() { return cell_row; }
        public int getCell_col() { return cell_col; }
    }
}