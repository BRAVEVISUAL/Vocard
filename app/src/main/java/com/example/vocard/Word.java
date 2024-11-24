package com.example.vocard;

public class Word {
    private int imageResId;  // 이미지 리소스 ID
    private String englishWord;  // 영단어
    private String koreanMeaning;  // 한글 뜻

    public Word(int imageResId, String englishWord, String koreanMeaning) {
        this.imageResId = imageResId;
        this.englishWord = englishWord;
        this.koreanMeaning = koreanMeaning;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getEnglishWord() {
        return englishWord;
    }

    public String getKoreanMeaning() {
        return koreanMeaning;
    }
}
