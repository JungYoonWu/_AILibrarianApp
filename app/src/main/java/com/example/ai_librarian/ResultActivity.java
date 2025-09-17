// com/example/ai_librarian/ResultActivity.java
package com.example.ai_librarian;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        LinearLayout container = findViewById(R.id.results_container);
        // → activity_result.xml 에서 결과를 붙일 LinearLayout 을 미리 정의해 두어야 합니다.

        Button btnHome = findViewById(R.id.btnHome);

        Intent intent = getIntent();
        int itemCount = intent.getIntExtra("items_count", 0);

        // 여러 개로 넘어온 ArrayList 들을 꺼낸다.
        ArrayList<String> ocrStrings       = intent.getStringArrayListExtra("ocr_strings");
        ArrayList<String> correcteds       = intent.getStringArrayListExtra("correcteds");
        ArrayList<String> usedQueries      = intent.getStringArrayListExtra("used_queries");
        ArrayList<String> matchedTitles    = intent.getStringArrayListExtra("matched_titles");
        ArrayList<Integer> similarityScores= intent.getIntegerArrayListExtra("similarity_scores");

        // (주의) getSerializableExtra 로 꺼낸, ArrayList<ArrayList<String>> 형식
        ArrayList<ArrayList<String>> allOcrTexts    = (ArrayList<ArrayList<String>>) intent.getSerializableExtra("all_ocr_texts");
        ArrayList<ArrayList<String>> allCandidates  = (ArrayList<ArrayList<String>>) intent.getSerializableExtra("all_candidates");

        for (int i = 0; i < itemCount; i++) {
            // 동적으로 하나씩 TextView 를 만들어서 붙여 보자
            TextView tvBlock = new TextView(this);
            tvBlock.setTextSize(14);
            tvBlock.setPadding(0, 16, 0, 16);

            StringBuilder sb = new StringBuilder();
            sb.append("— Spine #").append(i + 1).append(" —\n");

            // OCR 합쳐진 텍스트
            String ocrString = ocrStrings.get(i);
            sb.append("OCR 합본: ").append(ocrString.isEmpty() ? "없음" : ocrString).append("\n");

            // 사전 교정
            String corr = correcteds.get(i);
            sb.append("사전 치환: ").append(corr.isEmpty() ? "없음" : corr).append("\n");

            // 사용 쿼리
            String usedQ = usedQueries.get(i);
            sb.append("검색 쿼리: ").append(usedQ.isEmpty() ? "없음" : usedQ).append("\n");

            // 추천 제목 + 유사도
            String matchT = matchedTitles.get(i);
            int simScore = similarityScores.get(i);
            if (!matchT.isEmpty()) {
                sb.append("추천 도서: ").append(matchT).append(" (").append(simScore).append("%)\n");
            } else {
                sb.append("추천 도서: 없음\n");
            }

            // 네이버 검색 후보 목록
            ArrayList<String> candList = allCandidates.get(i);
            if (candList != null && !candList.isEmpty()) {
                sb.append("검색 후보:\n");
                for (String c : candList) {
                    sb.append("  • ").append(c).append("\n");
                }
            } else {
                sb.append("검색 후보: 없음\n");
            }

            // OCR 라인별 원문
            ArrayList<String> textLines = allOcrTexts.get(i);
            if (textLines != null && !textLines.isEmpty()) {
                sb.append("OCR 라인별 원문:\n");
                for (String line : textLines) {
                    sb.append("  - ").append(line).append("\n");
                }
            } else {
                sb.append("OCR 라인별 원문: 없음\n");
            }

            tvBlock.setText(sb.toString());
            container.addView(tvBlock);
        }

        btnHome.setOnClickListener(v -> {
            Intent homeIntent = new Intent(ResultActivity.this, MainActivity.class);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(homeIntent);
        });
    }
}
