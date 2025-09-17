package com.example.ai_librarian;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_GALLERY = 2;
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final int REQUEST_SHELF_CELL_IMAGE = 3000;
    private static final int REQUEST_CODE_CORRECTION = 4000;

    private final List<String> selectedImagePathList = new ArrayList<>();
    private ImageView overlayImage;
    private RadioGroup functionGroup;
    private Button btnProcess, btnCamera, btnGallery;
    private String selectedImagePath = null;
    private String currentImageFilename = null;
    private String currentUserId = "user_with_books";

    //추가된 shelf관련 view/data
    private String shelfName;
    private int shelfRows, shelfCols;
    private GridLayout gridContainer;
    private Button btnUploadShelf;
    private List<CellData> cellDataList = new ArrayList<>();
    private final Map<String, List<String>> shelfOcrMap = new HashMap<>();
    private int selectedCellRow, selectedCellCol;
    private Button btnExportCsv;
    private List<ShelfDetailResponse.Cell> currentCellsInShelf; // [오류 수정]
    private EditText etSearchQuery;
    private Button btnSearch;
    private LinearLayout layoutMyBookshelves;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        overlayImage = findViewById(R.id.overlay_image);
        functionGroup = findViewById(R.id.function_group);
        btnProcess = findViewById(R.id.btnProcess);
        btnCamera = findViewById(R.id.btnCamera);
        btnGallery = findViewById(R.id.btnGallery);
        gridContainer = findViewById(R.id.grid_shelf_container);
        btnUploadShelf = findViewById(R.id.btnUploadShelf);
        etSearchQuery = findViewById(R.id.etSearchQuery);
        btnSearch = findViewById(R.id.btnSearch);
        btnExportCsv = findViewById(R.id.btnExportCsv);

        btnExportCsv.setOnClickListener(v -> {
            if (currentCellsInShelf != null && !currentCellsInShelf.isEmpty()) { // [오류 수정]
                createAndUploadCsv(currentCellsInShelf); // [오류 수정]
            } else {
                Toast.makeText(this, "내보내기할 책 데이터가 없습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        btnSearch.setOnClickListener(v -> {
            String query = etSearchQuery.getText().toString().trim();
            if (query.isEmpty()) {
                Toast.makeText(this, "검색어를 입력하세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            performGlobalSearch(query);
        });

        layoutMyBookshelves = findViewById(R.id.layout_my_bookshelves);

        Intent launchIntent = getIntent();
        if (launchIntent.hasExtra("shelf_name")) {
            shelfName = launchIntent.getStringExtra("shelf_name");
            shelfRows = launchIntent.getIntExtra("rows", 1);
            shelfCols = launchIntent.getIntExtra("cols", 1);
            setupShelfGrid();
            btnUploadShelf.setText("책장 업로드");
            getIntent().removeExtra("shelf_name");
        } else {
            btnUploadShelf.setText("새 책장 등록");
            loadUserBookshelves();
        }

        btnCamera.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                } else {
                    Toast.makeText(this, "에뮬레이터에 카메라 앱이 없습니다. AVD 설정을 확인하세요.", Toast.LENGTH_SHORT).show();
                }
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            }
        });

        btnGallery.setOnClickListener(v -> {
            Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(Intent.createChooser(galleryIntent, "Select Pictures"), REQUEST_IMAGE_GALLERY);
        });

        btnProcess.setOnClickListener(v -> {
            int selectedId = functionGroup.getCheckedRadioButtonId();
            RadioButton rb = findViewById(selectedId);
            if (rb != null) {
                processImage(rb.getText().toString());
            } else {
                Toast.makeText(this, "먼저 기능을 선택하세요.", Toast.LENGTH_SHORT).show();
            }
        });

        btnUploadShelf.setOnClickListener(v -> {
            if (shelfName == null) {
                Intent i = new Intent(MainActivity.this, ShelfRegistrationActivity.class);
                startActivity(i);
            } else {
                uploadShelfData();
            }
        });
    }

    private void loadUserBookshelves() {
        btnExportCsv.setVisibility(View.GONE);
        gridContainer.setVisibility(View.GONE);
        overlayImage.setVisibility(View.GONE);
        layoutMyBookshelves.setVisibility(View.VISIBLE);

        btnUploadShelf.setText("새 책장 등록");
        btnUploadShelf.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, ShelfRegistrationActivity.class);
            startActivity(i);
        });

        APIService service = ApiClient.getService();
        service.getBookshelves(currentUserId).enqueue(new Callback<BookshelfListResponse>() { // [오류 수정]
            @Override
            public void onResponse(Call<BookshelfListResponse> call, Response<BookshelfListResponse> response) { // [오류 수정]
                if (response.isSuccessful() && response.body() != null) {
                    List<BookshelfListResponse.BookshelfItem> shelves = response.body().getBookshelves(); // [오류 수정]
                    layoutMyBookshelves.removeAllViews();
                    if (shelves.isEmpty()) {
                        TextView tv = new TextView(MainActivity.this);
                        tv.setText("등록된 책장이 없습니다.");
                        layoutMyBookshelves.addView(tv);
                    } else {
                        for (BookshelfListResponse.BookshelfItem shelf : shelves) { // [오류 수정]
                            Button btnShelf = new Button(MainActivity.this);
                            btnShelf.setText(shelf.getShelf_name() + " (" + shelf.getRows() + "x" + shelf.getCols() + ")");
                            btnShelf.setOnClickListener(v -> {
                                loadShelfDetails(shelf);
                            });
                            layoutMyBookshelves.addView(btnShelf);
                        }
                    }
                } else {
                    Toast.makeText(MainActivity.this, "책장 목록 로딩 실패", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<BookshelfListResponse> call, Throwable t) { // [오류 수정]
                Toast.makeText(MainActivity.this, "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadShelfDetails(BookshelfListResponse.BookshelfItem shelf) {
        APIService service = ApiClient.getService();
        service.getShelfDetails(shelf.getShelf_id()).enqueue(new Callback<ShelfDetailResponse>() {
            @Override
            public void onResponse(Call<ShelfDetailResponse> call, Response<ShelfDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    displayShelfContents(shelf, response.body().getCells());
                } else {
                    Toast.makeText(MainActivity.this, "책 내용 로딩 실패", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ShelfDetailResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "네트워크 오류", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayShelfContents(BookshelfListResponse.BookshelfItem shelf, List<ShelfDetailResponse.Cell> cells) {
        layoutMyBookshelves.setVisibility(View.GONE);
        gridContainer.setVisibility(View.VISIBLE);
        btnExportCsv.setVisibility(View.VISIBLE);
        btnUploadShelf.setText("다른 책장 보기");
        btnUploadShelf.setOnClickListener(v -> loadUserBookshelves());

        this.currentCellsInShelf = cells;

        gridContainer.removeAllViews();
        gridContainer.setRowCount(shelf.getRows());
        gridContainer.setColumnCount(shelf.getCols());

        Map<String, ShelfDetailResponse.Cell> cellMap = new HashMap<>();
        if (cells != null) {
            for (ShelfDetailResponse.Cell cell : cells) {
                cellMap.put(cell.getCell_row() + "_" + cell.getCell_col(), cell);
                Log.d("SHELF_DEBUG", "Cell(" + cell.getCell_row() + "," + cell.getCell_col() + ") Image URL: " + cell.getImage_url());
            }
        }

        for (int r = 0; r < shelf.getRows(); r++) {
            for (int c = 0; c < shelf.getCols(); c++) {
                String key = r + "_" + c;
                final ShelfDetailResponse.Cell currentCell = cellMap.get(key);

                // ▼▼▼ [오류 수정] ImageView의 LayoutParams 설정 수정 ▼▼▼
                ImageView cellView = new ImageView(this);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams(GridLayout.spec(r, 1, 1f), GridLayout.spec(c, 1, 1f));
                params.width = 0;
                params.height = GridLayout.LayoutParams.WRAP_CONTENT; // 높이를 0이 아닌 WRAP_CONTENT로 설정
                params.setMargins(8, 8, 8, 8);
                cellView.setLayoutParams(params);
                cellView.setAdjustViewBounds(true); // 비율에 맞게 뷰 크기 조정
                cellView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                // ▲▲▲ [오류 수정] ▲▲▲

                if (currentCell != null && currentCell.getImage_url() != null) {
                    Glide.with(this).load(currentCell.getImage_url()).placeholder(R.drawable.ic_placeholder).into(cellView);
                } else {
                    cellView.setImageResource(R.drawable.ic_placeholder);
                }

                cellView.setOnClickListener(v -> {
                    if (currentCell != null && currentCell.getBooks() != null && !currentCell.getBooks().isEmpty()) {
                        Intent intent = new Intent(this, CorrectionActivity.class);
                        intent.putExtra("books_in_cell", new ArrayList<>(currentCell.getBooks()));
                        intent.putExtra("cell_key", key);
                        startActivityForResult(intent, REQUEST_CODE_CORRECTION);
                    } else {
                        Toast.makeText(this, "이 셀에는 등록된 책이 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
                gridContainer.addView(cellView);
            }
        }
    }

    private void setupShelfGrid() { // 이 메소드는 이제 사용되지 않음 (setupShelfGridForNewShelf로 대체)
        gridContainer.removeAllViews();
        gridContainer.setRowCount(shelfRows);
        gridContainer.setColumnCount(shelfCols);
        cellDataList.clear();

        for (int r = 0; r < shelfRows; r++) {
            for (int c = 0; c < shelfCols; c++) {
                ImageView cellView = new ImageView(this);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams(GridLayout.spec(r, 1, 1f), GridLayout.spec(c, 1, 1f));
                params.width = 0;
                params.height = GridLayout.LayoutParams.WRAP_CONTENT;
                params.setMargins(8, 8, 8, 8);
                cellView.setLayoutParams(params);
                cellView.setImageResource(R.drawable.ic_placeholder);
                cellView.setAdjustViewBounds(true);

                final int row = r;
                final int col = c;
                cellView.setOnClickListener(v -> {
                    selectedCellRow = row;
                    selectedCellCol = col;
                    Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    galleryIntent.setType("image/*");
                    startActivityForResult(Intent.createChooser(galleryIntent, "셀 이미지 선택"), REQUEST_SHELF_CELL_IMAGE);
                });
                gridContainer.addView(cellView);
                cellDataList.add(new CellData(r, c, null, null));
            }
        }
    }

    private void setupShelfGridForNewShelf() {
        gridContainer.setVisibility(View.VISIBLE);
        layoutMyBookshelves.setVisibility(View.GONE);
        btnExportCsv.setVisibility(View.GONE);
        btnUploadShelf.setText("책장 업로드");
        btnUploadShelf.setOnClickListener(v -> uploadShelfData());
        gridContainer.removeAllViews();
        gridContainer.setRowCount(shelfRows);
        gridContainer.setColumnCount(shelfCols);
        cellDataList.clear();

        for (int r = 0; r < shelfRows; r++) {
            for (int c = 0; c < shelfCols; c++) {
                ImageView cellView = new ImageView(this);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams(GridLayout.spec(r, 1, 1f), GridLayout.spec(c, 1, 1f));
                params.width = 0;
                params.height = 0;
                params.setMargins(8, 8, 8, 8);
                cellView.setLayoutParams(params);
                cellView.setImageResource(R.drawable.ic_placeholder);
                cellView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                final int row = r;
                final int col = c;
                cellView.setOnClickListener(v -> {
                    selectedCellRow = row;
                    selectedCellCol = col;
                    Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    galleryIntent.setType("image/*");
                    startActivityForResult(Intent.createChooser(galleryIntent, "셀 이미지 선택"), REQUEST_SHELF_CELL_IMAGE);
                });
                gridContainer.addView(cellView);
                cellDataList.add(new CellData(r, c, null, null));
            }
        }
    }

    private void uploadShelfData() {
        RequestBody rbUserId = RequestBody.create(MediaType.parse("text/plain"), currentUserId);
        RequestBody rbName = RequestBody.create(MediaType.parse("text/plain"), shelfName);
        RequestBody rbRows = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(shelfRows));
        RequestBody rbCols = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(shelfCols));
        List<MultipartBody.Part> parts = new ArrayList<>();
        for (CellData cell : cellDataList) {
            String imgPath = cell.getImagePath();
            if (imgPath != null) {
                File imgFile = new File(imgPath);
                RequestBody imgBody = RequestBody.create(MediaType.parse("image/*"), imgFile);
                parts.add(MultipartBody.Part.createFormData("cells", cell.getRow() + "_" + cell.getCol() + "_image", imgBody));
            }
        }
        APIService service = ApiClient.getService();
        service.uploadShelf(rbUserId, rbName, rbRows, rbCols, parts).enqueue(new Callback<ShelfResult>() {
            @Override
            public void onResponse(Call<ShelfResult> call, Response<ShelfResult> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "책장 업로드 성공, 목록을 새로고침합니다.", Toast.LENGTH_SHORT).show();
                    loadUserBookshelves();
                } else {
                    Toast.makeText(MainActivity.this, "업로드 실패: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ShelfResult> call, Throwable t) {
                Toast.makeText(MainActivity.this, "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void performGlobalSearch(String query) {
        APIService service = ApiClient.getService();
        service.searchMyBooks(currentUserId, query).enqueue(new Callback<GlobalSearchResponse>() {
            @Override
            public void onResponse(Call<GlobalSearchResponse> call, Response<GlobalSearchResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<GlobalSearchResponse.SearchResultItem> results = response.body().getSearchResults();
                    StringBuilder resultText = new StringBuilder();
                    if (results.isEmpty()) {
                        resultText.append("검색 결과가 없습니다.");
                    } else {
                        resultText.append("총 ").append(results.size()).append("건의 책을 찾았습니다.\n\n");
                        for (GlobalSearchResponse.SearchResultItem item : results) {
                            resultText.append("• '").append(item.getFound_title()).append("'\n");
                            resultText.append("  (위치: ").append(item.getShelf_name()).append(" 책장, ").append(item.getCell_row()).append("행 ").append(item.getCell_col()).append("열)\n\n");
                        }
                    }
                    new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this)
                            .setTitle("'" + query + "' 검색 결과")
                            .setMessage(resultText.toString())
                            .setPositiveButton("확인", null)
                            .show();
                }
            }
            @Override
            public void onFailure(Call<GlobalSearchResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createAndUploadCsv(List<ShelfDetailResponse.Cell> cells) { // [오류 수정]
        StringBuilder csvContent = new StringBuilder();
        csvContent.append("Book Title,Row,Column\n");
        if (cells != null) {
            for (ShelfDetailResponse.Cell cell : cells) {
                if (cell.getBooks() != null) {
                    for (ShelfDetailResponse.Book book : cell.getBooks()) { // [오류 수정]
                        String originalTitle = book.getFinal_title();
                        String safeTitle = (originalTitle == null) ? "" : originalTitle;
                        String title = "\"" + safeTitle.replace("\"", "\"\"") + "\"";
                        csvContent.append(title).append(",").append(cell.getCell_row()).append(",").append(cell.getCell_col()).append("\n"); // [오류 수정]
                    }
                }
            }
        }
        try {
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!downloadsDir.exists()) downloadsDir.mkdirs();
            String fileName = "bookshelf_" + System.currentTimeMillis() + ".csv";
            File file = new File(downloadsDir, fileName);
            try (FileOutputStream fos = new FileOutputStream(file);
                 java.io.OutputStreamWriter writer = new java.io.OutputStreamWriter(fos, "UTF-8")) {
                fos.write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});
                writer.append(csvContent.toString());
                writer.flush();
            }
            Toast.makeText(this, "CSV 파일 생성 완료: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
            uploadCsvFile(file);
        } catch (IOException e) {
            Toast.makeText(this, "CSV 파일 생성 실패", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadCsvFile(File file) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("text/csv"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("booklist_file", file.getName(), requestFile);
        APIService service = ApiClient.getService();
        service.uploadBooklist(body).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "파일 서버 업로드 성공!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "파일 업로드 실패: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(MainActivity.this, "네트워크 오류", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;

        if (requestCode == REQUEST_CODE_CORRECTION) {
            loadUserBookshelves();
        } else if (data != null && requestCode == REQUEST_SHELF_CELL_IMAGE) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                try {
                    String imgPath = copyImageOnly(imageUri);
                    for (CellData cell : cellDataList) {
                        if (cell.getRow() == selectedCellRow && cell.getCol() == selectedCellCol) {
                            cell.setImagePath(imgPath);
                            break;
                        }
                    }
                    int index = selectedCellRow * shelfCols + selectedCellCol;
                    ImageView iv = (ImageView) gridContainer.getChildAt(index);
                    Glide.with(this).load(imageUri).into(iv);
                } catch (IOException e) {
                    Log.e("SHELF_CELL", "이미지 복사 실패", e);
                }
            }
        }
    }

    private String getDisplayName(Uri uri) {
        String result = null;
        if (uri != null && "content".equals(uri.getScheme())) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int colIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (colIndex >= 0) {
                        result = cursor.getString(colIndex);
                    }
                }
            }
        }
        if (result == null && uri != null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private String copyImageOnly(Uri imageUri) throws IOException {
        String fileName = getDisplayName(imageUri);
        File picturesDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Images");
        if (!picturesDir.exists()) picturesDir.mkdirs();
        File file = new File(picturesDir, fileName);
        try (InputStream inputStream = getContentResolver().openInputStream(imageUri);
             FileOutputStream outputStream = new FileOutputStream(file)) {
            byte[] buf = new byte[8192];
            int len;
            while ((len = inputStream.read(buf)) > 0) {
                outputStream.write(buf, 0, len);
            }
        }
        return file.getAbsolutePath();
    }

    // 이하는 이전 버전에서 사용되었던 메소드들입니다. (삭제하지 않고 보존)
    private void processImage(String function) {}
    private File saveBitmapToFile(Bitmap bitmap) throws IOException {return null;}
    private void saveOcrResult(String content) {}
    private String copyBitmapAndGetPathAndLabel(Uri imageUri, Uri labelUri) throws IOException {return null;}

}