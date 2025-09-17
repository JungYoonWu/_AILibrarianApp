package com.example.ai_librarian;

import java.util.List;
import java.util.Map;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIService {

    // ────────── 책 제목 추출용 OCR 검색 (단일 이미지) ──────────
    @Multipart
    @POST("ocr-search")
    Call<OcrSearchResult> upLoadImageForOcrSearch(
            @Part MultipartBody.Part image
    );

    // ────────── 책 제목 추출용 OCR 검색 (다중 이미지+라벨) ──────────
    @Multipart
    @POST("ocr-search")
    Call<OcrSearchResponse> uploadAllPartsForOcrSearch(
            @Part List<MultipartBody.Part> allParts
    );

    // ────────── 책 재배치용 엔드포인트 ──────────
    @Multipart
    @POST("rearrange")
    Call<RearrangeResult> uploadImageForRearrange(
            @Part MultipartBody.Part image,
            @Part MultipartBody.Part labels,
            @Part("order") RequestBody order,
            @Part("gap") RequestBody gap
    );

    /**
     * 새로운 책장을 생성하고, 모든 셀의 이미지/라벨을 업로드합니다.
     */
    @Multipart
    @POST("shelf-upload")
    Call<ShelfResult> uploadShelf(
            @Part("user_id") RequestBody userId,
            @Part("shelf_name") RequestBody name,
            @Part("shelf_rows") RequestBody rows,
            @Part("shelf_cols") RequestBody cols,
            @Part List<MultipartBody.Part> cells
    );

    @POST("correct-title")
    Call<OcrSearchResult.OcrResultItem> correctTitle(@Body CorrectTitleRequest body);

    /**
     * 특정 사용자의 모든 책장 목록을 조회합니다.
     */
    @GET("bookshelves/{user_id}")
    Call<BookshelfListResponse> getBookshelves(@Path("user_id") String userId); // ★ 반환 타입을 BookshelfListResponse 로 명확히 함

    /**
     * 특정 책장의 상세 내용(모든 셀의 이미지 URL과 책 정보)을 조회합니다.
     */
    @GET("shelf-details/{shelf_id}") // ★ 서버와 경로 일치
    Call<ShelfDetailResponse> getShelfDetails(@Path("shelf_id") int shelfId); // ★ 메소드 이름 일치

    /**
     * 수정된 책 제목들을 DB에 일괄 업데이트합니다.
     */
    @POST("books/update")
    Call<ResponseBody> updateBookTitles(@Body Map<String, List<ShelfDetailResponse.Book>> body);

    /**
     * 특정 사용자의 모든 책에서 키워드로 검색합니다.
     */
    @GET("search-books/{user_id}")
    Call<GlobalSearchResponse> searchMyBooks(
            @Path("user_id") String userId,
            @Query("query") String query
    );

    /**
     * 생성된 책 목록 CSV 파일을 서버에 업로드합니다.
     */
    @Multipart
    @POST("upload-booklist")
    Call<ResponseBody> uploadBooklist(@Part MultipartBody.Part booklistFile);
}