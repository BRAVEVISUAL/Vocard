package com.example.vocard;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.view.ViewOutlineProvider;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CardFlipActivity extends AppCompatActivity {

    private List<Word> wordList; // 셔플된 단어 리스트
    private List<ImageView> cardList = new ArrayList<>();
    private Word currentPromptWord; // 현재 제시된 단어
    private TextView tvPrompt; // 제시어를 표시하는 TextView
    private int incorrectAttempts = 0; // 잘못된 시도 횟수
    private List<ImageView> flippedCards = new ArrayList<>(); // 이미 정답 처리된 카드
    private int level; // 챕터 정보

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_flip);

        ImageView card1 = findViewById(R.id.card1);
        card1.setClipToOutline(true);
        ImageView card2 = findViewById(R.id.card2);
        card2.setClipToOutline(true);
        ImageView card3 = findViewById(R.id.card3);
        card3.setClipToOutline(true);
        ImageView card4 = findViewById(R.id.card4);
        card4.setClipToOutline(true);
        ImageView card5 = findViewById(R.id.card5);
        card5.setClipToOutline(true);
        ImageView card6 = findViewById(R.id.card6);
        card6.setClipToOutline(true);
        ImageView card7 = findViewById(R.id.card7);
        card7.setClipToOutline(true);
        ImageView card8 = findViewById(R.id.card8);
        card8.setClipToOutline(true);
        ImageView card9 = findViewById(R.id.card9);
        card9.setClipToOutline(true);
        // 전달된 레벨 데이터와 Word 배열 가져오기
        level = getIntent().getIntExtra("chapter", -1);
        Word[] wordArray = (Word[]) getIntent().getSerializableExtra("wordArray");

        // 뒤로 가기 버튼
        Button prevBtn = findViewById(R.id.backMain);
        prevBtn.setOnClickListener(view -> finish());

        // 제시어를 표시할 TextView
        tvPrompt = findViewById(R.id.tv_prompt);

        // Word 배열을 카드에 랜덤으로 배치
        if (wordArray != null) {
            populateCards(wordArray);

            // 모든 카드 클릭 불가로 설정 (초기 5초 동안)
            setCardsClickable(false);

            // 5초 동안 카드 앞면을 보여주고 카운트다운 표시
            startCountdown(5);
        }
    }

    private void populateCards(Word[] wordArray) {
        // 9개의 카드 ID를 배열로 저장
        int[] cardIds = {
                R.id.card1, R.id.card2, R.id.card3,
                R.id.card4, R.id.card5, R.id.card6,
                R.id.card7, R.id.card8, R.id.card9
        };

        // Word 배열을 List로 변환 후 셔플
        wordList = new ArrayList<>(Arrays.asList(wordArray));
        Collections.shuffle(wordList);

        // 카드 개수보다 단어 개수가 적을 경우 빈 데이터 추가
        while (wordList.size() < cardIds.length) {
            wordList.add(null); // 빈 데이터를 추가
        }

        // 카드에 이미지 배치
        for (int i = 0; i < cardIds.length; i++) {
            ImageView card = findViewById(cardIds[i]);
            cardList.add(card);

            if (wordList.get(i) != null) {
                card.setImageResource(wordList.get(i).getImageResId());
            } else {
                card.setImageResource(android.R.color.transparent); // 빈 카드
            }

            // 카드 클릭 이벤트 추가
            addCardClickListener(card, i);
        }
    }

    // 5초 동안 카운트다운 표시
    private void startCountdown(int seconds) {
        new Handler().postDelayed(() -> {
            if (seconds > 0) {
                tvPrompt.setText(String.valueOf(seconds));
                startCountdown(seconds - 1);
            } else {
                hideAllCards();
            }
        }, 1000);
    }

    // 5초 후 모든 카드를 뒷면으로 숨기기
    private void hideAllCards() {
        for (ImageView card : cardList) {
            if (!flippedCards.contains(card)) {
                card.setImageResource(R.drawable.back); // 뒷면 이미지로 변경
            }
        }

        // 카드 클릭 가능하게 설정
        setCardsClickable(true);

        // 첫 제시어 설정
        showNewPrompt();
    }

    // 무작위로 새로운 제시어를 보여줌
    private void showNewPrompt() {
        List<Word> shuffledWordList = new ArrayList<>(wordList);
        Collections.shuffle(shuffledWordList);

        // null이 아닌 유효한 단어 중 이미 맞춘 단어가 아닌 무작위 선택
        for (Word word : shuffledWordList) {
            if (word != null && !isWordAlreadyFlipped(word)) {
                currentPromptWord = word;
                tvPrompt.setText("제시어: " + word.getEnglishWord());
                break;
            }
        }
    }

    private boolean isWordAlreadyFlipped(Word word) {
        for (ImageView card : flippedCards) {
            int index = cardList.indexOf(card);
            if (index >= 0 && wordList.get(index).equals(word)) {
                return true;
            }
        }
        return false;
    }

    private void addCardClickListener(ImageView card, int index) {
        card.setOnClickListener(v -> {
            Word selectedWord = wordList.get(index);

            if (selectedWord != null) {
                // 카드 뒤집기 애니메이션 실행
                flipCard(card, selectedWord);

                // 정답인지 확인
                if (selectedWord.equals(currentPromptWord)) {
                    // 정답이면 카드 유지하고 새로운 제시어
                    card.setEnabled(false); // 정답 카드는 더 이상 클릭되지 않도록 비활성화
                    flippedCards.add(card); // 정답 처리된 카드 추가
                    incorrectAttempts = 0; // 정답 시 틀린 횟수 초기화

                    // 모든 카드가 정답 처리되었는지 확인
                    if (flippedCards.size() == wordList.size()) {
                        proceedToNextActivity(); // 다음 액티비티로 이동
                    } else {
                        showNewPrompt();
                    }
                } else {
                    // 틀렸으면 카드 다시 뒷면으로 (2초 동안 앞면 유지)
                    incorrectAttempts++;

                    new Handler().postDelayed(() -> card.setImageResource(R.drawable.back), 2000);

                    // 3번 틀렸을 경우, 모든 카드 다시 보여주기
                    if (incorrectAttempts == 3) {
                        incorrectAttempts = 0; // 시도 횟수 초기화

                        // 0.5초 기다린 후 모든 카드 다시 보여주기 프로세스 시작
                        new Handler().postDelayed(this::showAllCardsTemporarily, 500);
                    }
                }
            }
        });
    }

    private void showAllCardsTemporarily() {
        // 모든 카드 클릭 불가능하게 설정
        setCardsClickable(false);

        // "다시 보여드리죠!" 메시지 표시
        Toast.makeText(this, "다시 보여드리죠!", Toast.LENGTH_SHORT).show();

        // 모든 카드를 2초 동안 다시 앞면으로 보여줌 (이미 맞춘 카드는 그대로 유지)
        for (int i = 0; i < cardList.size(); i++) {
            ImageView card = cardList.get(i);
            if (!flippedCards.contains(card)) {
                Word word = wordList.get(i);
                if (word != null) {
                    card.setImageResource(word.getImageResId()); // 앞면으로 설정
                }
            }
        }

        // 2초 후 다시 모든 카드를 뒷면으로 뒤집음
        new Handler().postDelayed(() -> {
            for (int i = 0; i < cardList.size(); i++) {
                ImageView card = cardList.get(i);
                if (!flippedCards.contains(card)) {
                    card.setImageResource(R.drawable.back); // 뒷면으로 설정
                }
            }
            // 모든 카드 클릭 가능하게 설정
            setCardsClickable(true);
        }, 2000);
    }

    // 모든 카드의 클릭 가능 여부 설정
    private void setCardsClickable(boolean clickable) {
        for (ImageView card : cardList) {
            if (!flippedCards.contains(card)) {
                card.setEnabled(clickable);
            }
        }
    }

    private void flipCard(ImageView card, Word word) {
        ObjectAnimator flipOut = ObjectAnimator.ofFloat(card, "rotationY", 0f, 90f);
        ObjectAnimator flipIn = ObjectAnimator.ofFloat(card, "rotationY", 90f, 180f);

        flipOut.setDuration(300);
        flipIn.setDuration(300);

        flipOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                card.setImageResource(word.getImageResId()); // 이미지 변경
                flipIn.start();
            }
        });

        flipOut.start();
    }

    // 모든 카드 정답 시 다음 액티비티로 이동
    private void proceedToNextActivity() {
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(CardFlipActivity.this, StoryActivity.class);
            intent.putExtra("chapter", level); // 챕터 정보 전달
            startActivity(intent);
            finish(); // 현재 액티비티 종료
        }, 2000); // 2초(2000ms) 딜레이 후 실행

    }
}
