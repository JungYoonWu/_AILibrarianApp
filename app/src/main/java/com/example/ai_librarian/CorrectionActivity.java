package com.example.ai_librarian;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CorrectionActivity extends AppCompatActivity {

    private ArrayList<ShelfDetailResponse.Book> bookList; // [오류 수정] 올바른 Book 클래스 타입으로 변경
    private CorrectionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_correction);

        // Intent에서 Book 객체 리스트 수신
        bookList = (ArrayList<ShelfDetailResponse.Book>) getIntent().getSerializableExtra("books_in_cell"); // [오류 수정]
        String cellKey = getIntent().getStringExtra("cell_key");

        TextView tvTitle = findViewById(R.id.tvCorrectionTitle);
        ListView lvResults = findViewById(R.id.lv_ocr_results);
        Button btnDone = findViewById(R.id.btnCorrectionDone);

        tvTitle.setText("책 제목 수정 (셀 " + cellKey + ")");

        // 어댑터 설정
        adapter = new CorrectionAdapter(this, R.layout.list_item_correction, bookList);
        lvResults.setAdapter(adapter);

        btnDone.setOnClickListener(v -> {
            updateTitlesToDB();
        });
    }

    private void updateTitlesToDB() {
        List<ShelfDetailResponse.Book> updatedBooks = adapter.getUpdatedBooks();

        Map<String, List<ShelfDetailResponse.Book>> requestBody = new HashMap<>(); // [오류 수정]
        requestBody.put("updates", updatedBooks);

        APIService service = ApiClient.getService();
        service.updateBookTitles(requestBody).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CorrectionActivity.this, "책 제목이 DB에 저장되었습니다.", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(CorrectionActivity.this, "DB 저장 실패: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(CorrectionActivity.this, "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --- ListView를 위한 커스텀 어댑터 ---
    private class CorrectionAdapter extends ArrayAdapter<ShelfDetailResponse.Book> { // [오류 수정]

        private List<ShelfDetailResponse.Book> books; // [오류 수정]

        public CorrectionAdapter(@NonNull Context context, int resource, @NonNull List<ShelfDetailResponse.Book> objects) { // [오류 수정]
            super(context, resource, objects);
            this.books = objects;
        }

        public List<ShelfDetailResponse.Book> getUpdatedBooks() { // [오류 수정]
            return this.books;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_correction, parent, false);
            }

            TextView tvOcrOriginal = convertView.findViewById(R.id.tvOcrOriginal);
            TextView tvFinalTitle = convertView.findViewById(R.id.tvFinalTitle);
            EditText etEditTitle = convertView.findViewById(R.id.etEditTitle);
            Button btnEdit = convertView.findViewById(R.id.btnEdit);
            Button btnSave = convertView.findViewById(R.id.btnSave);

            ShelfDetailResponse.Book currentBook = books.get(position); // [오류 수정]

            tvOcrOriginal.setText("OCR 원문: " + currentBook.getOcr_text());
            tvFinalTitle.setText(currentBook.getFinal_title());

            btnEdit.setOnClickListener(v -> {
                tvFinalTitle.setVisibility(View.GONE);
                etEditTitle.setVisibility(View.VISIBLE);
                etEditTitle.setText(tvFinalTitle.getText().toString());
                etEditTitle.requestFocus();
                btnEdit.setVisibility(View.GONE);
                btnSave.setVisibility(View.VISIBLE);
            });

            btnSave.setOnClickListener(v -> {
                String newTitle = etEditTitle.getText().toString().trim();
                currentBook.setFinal_title(newTitle);
                tvFinalTitle.setText(newTitle);
                tvFinalTitle.setVisibility(View.VISIBLE);
                etEditTitle.setVisibility(View.GONE);
                etEditTitle.clearFocus();
                btnEdit.setVisibility(View.VISIBLE);
                btnSave.setVisibility(View.GONE);
                Toast.makeText(getContext(), "임시 저장되었습니다.", Toast.LENGTH_SHORT).show();
            });

            return convertView;
        }
    }
}