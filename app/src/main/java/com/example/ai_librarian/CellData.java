package com.example.ai_librarian;

public class CellData {
    private int row;            //셀의 행 인덱스(0부터)
    private int col;            //셀의 열 인덱스(0부터)
    private String imagePath;   //해당 셀에 업로드된 이미지 파일 경로
    private String labelPath;   //해당 셀에 업로드된 라벨 파일 경로(없는경우도 있음)

    public CellData(int row, int col, String imagePath, String labelPath){
        this.row = row;
        this.col = col;
        this.imagePath = imagePath;
        this.labelPath = labelPath;
    }

    public int getRow() {
        return row;
    }
    public void setRow(int row) {
        this.row = row;
    }
    public int getCol() {
        return col;
    }
    public void setCol(int col) {
        this.col = col;
    }
    public String getImagePath() {
        return imagePath;
    }
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
    public String getLabelPath() {
        return labelPath;
    }
    public void setLabelPath(String labelPath) {
        this.labelPath = labelPath;
    }
}
