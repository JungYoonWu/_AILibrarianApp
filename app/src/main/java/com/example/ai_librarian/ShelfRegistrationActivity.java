package com.example.ai_librarian;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;
import com.example.ai_librarian.R;
public class ShelfRegistrationActivity extends AppCompatActivity {

    private EditText etShelfName;
    private Spinner spnRows;
    private Spinner spnCols;
    private Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shelf_registration);

        etShelfName = findViewById(R.id.etShelfName);
        spnRows     = findViewById(R.id.spnRows);
        spnCols     = findViewById(R.id.spnCols);
        btnNext     = findViewById(R.id.btnNext);

        // ★ 변경/추가: 1~8까지 값을 가진 어댑터 설정
        Integer[] rowOptions = new Integer[]{1,2,3,4,5,6,7,8};
        ArrayAdapter<Integer> rowAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, rowOptions
        );
        rowAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnRows.setAdapter(rowAdapter);
        spnCols.setAdapter(rowAdapter);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ★ 변경/추가: 입력값 가져오기
                String shelfName = etShelfName.getText().toString().trim();
                if (shelfName.isEmpty()) {
                    // 자동 지정: "Book Shelf 1, 2, ..."
                    shelfName = "Book Shelf " + System.currentTimeMillis();
                }
                int rows = (int) spnRows.getSelectedItem();
                int cols = (int) spnCols.getSelectedItem();

                // ★ 변경/추가: MainActivity로 전달
                Intent intent = new Intent(ShelfRegistrationActivity.this, MainActivity.class);
                intent.putExtra("shelf_name", shelfName);
                intent.putExtra("rows", rows);
                intent.putExtra("cols", cols);
                startActivity(intent);
                finish();
            }
        });
    }
}

