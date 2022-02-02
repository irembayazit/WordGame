package com.irem.wordproject;

        import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class PlayActivity extends AppCompatActivity {

    private AlertDialog.Builder alert;

    private Intent get_intent;
    private int hakSayisi;

    private TextView textViewQuestion, textViewQuest, textViewHearCount, soruCanDegeri, toplamSkorDegeri;
    private EditText editTextTahminDegeri;
    private ArrayList<String> sorularList,sorularList2;
    private ArrayList<String> sorularKodList;
    private ArrayList<String> kelimelerList;
    private ArrayList<Character> answerList;

    private Random rndSoru, rndKelime, rndHarf;
    private int rndHarfNumber;
    private String answer, kelimeBilgisi, textTahminDegeri;
    private int rastgeleBelirlenecekHarfSayisi;

    private Dialog statisticTableDialog;
    private ImageView statisticTableImgClose;
    private LinearLayout statisticTableLinear;
    private Button statisticTableBtnMainMenu, statisticTableBtnPlayAgain;
    private TextView skorSayisi,harfSayisi, statisticTrueQuestionCount;
    private ProgressBar statisticBarWordCount;
    private WindowManager.LayoutParams params;
    private int cozulenKelimeSayisi = 0, cozulenSoruSayisi = 0, kullanilanHarfSayisi = 0,maksimumSoruSayisi=0, maksimumKelimeSayisi;
    private DocumentReference docRefQuestions,docRefUsers, docRefAnswer;
    private FirebaseFirestore mFirestore;
    private String question,docuserGelen,userNameGelen;
    private int sorular;
    private int kacinciSoru=1;
    private String deger, score;
    private int toplamPuan=0;
    private DocumentReference docRef;

    public void SoruGetir(String soruNumber){
        try {
            System.out.println("SoruGetir**********");
            mFirestore = FirebaseFirestore.getInstance();
            textViewQuestion = (TextView) findViewById(R.id.play_activity_textViewQuestion);
            docRefQuestions = mFirestore.collection("Questions").document("questions");
            docRefQuestions.get().addOnSuccessListener(this, new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.exists())
                    {
                        System.out.println("tüm activity veriler getirldi: " + documentSnapshot.getData());
                        sorular = documentSnapshot.getData().size();
                        System.out.println("tüm activity veriler getirldi Array: " + sorular);
                        question = (String)documentSnapshot.getData().get(soruNumber);
                        System.out.println("Soru1: " +question);
                        textViewQuestion.setText(question);
                    }
                }
            }).addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    System.out.println("Play activity error: " + e.getMessage());
                }
            });
        }catch (Exception e) {
            e.printStackTrace();
            System.out.println("Soru getirme hatası: " + e.getMessage());
        }
        CevapGetir(soruNumber);
    }

    protected void onCreate(Bundle savedInstanceState) {

        SoruGetir("1");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        textViewQuestion = (TextView) findViewById(R.id.play_activity_textViewQuestion);
        soruCanDegeri = (TextView) findViewById(R.id.play_activity_soruCanDegeri);
        toplamSkorDegeri = (TextView) findViewById(R.id.play_activity_toplamSkorDegeri);
        textViewQuest = (TextView) findViewById(R.id.play_activity_textViewQuest);
        editTextTahminDegeri = (EditText) findViewById(R.id.play_activity_editTextGuess);
        textViewHearCount = (TextView) findViewById(R.id.play_activity_textViewUserHeartCount);
        sorularList = new ArrayList<>();
        sorularKodList = new ArrayList<>();
        kelimelerList = new ArrayList<>();
        rndSoru = new Random();
        rndKelime = new Random();
        rndHarf = new Random();

        get_intent = getIntent();
        hakSayisi = get_intent.getIntExtra("heartCount", 0);
        docuserGelen = get_intent.getStringExtra("docUser");
        userNameGelen = get_intent.getStringExtra("userName1");
        score = get_intent.getStringExtra("score");
        System.out.println("hakSayisi: " + hakSayisi);
        System.out.println("docuserGelen: " + docuserGelen);
        System.out.println("userNameGelen: " + userNameGelen);
        textViewHearCount.setText("+" + hakSayisi);

    }

    @Override
    public void onBackPressed() {
        alert = new AlertDialog.Builder(this);
        alert.setTitle("Kelime Bilmece");
        alert.setMessage("Geri Dönmek İstediğinize Emin Misiniz?");
        alert.setIcon(R.mipmap.ic_kelimebilmece);
        alert.setPositiveButton("Hayır", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alert.setNegativeButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mainIntent();
            }
        });

        alert.show();
    }

    public void btnIstatistikTablosu(View v) {
        maksimumVerileriHesapla("");
    }

    private void istatistikTablosunuGoster(String oyunDurumu, int maksimumSoruSayisi, int kullanilanHarfSayisi, int cozulenSoruSayisi) {
        System.out.println("oyunDurumu: " + oyunDurumu);
        System.out.println("maksimumSoruSayisi: " + maksimumSoruSayisi);
        System.out.println("kullanilanHarfSayisi: " + kullanilanHarfSayisi);
        System.out.println("cozulenSoruSayisi : " + cozulenSoruSayisi);
        System.out.println("toplamSkorDegeri.getText().toString() : " + toplamSkorDegeri.getText().toString());

        statisticTableDialog = new Dialog(this);
        params = new WindowManager.LayoutParams();
        params.copyFrom(statisticTableDialog.getWindow().getAttributes());
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        statisticTableDialog.setContentView(R.layout.custom_dialog_statistic_table);

        statisticTableImgClose = (ImageView) statisticTableDialog.findViewById(R.id.custom_dialog_statistic_table_imageViewClose);
        statisticTableLinear = (LinearLayout) statisticTableDialog.findViewById(R.id.custom_dialog_statistic_table_linearLayout);

        statisticTableBtnMainMenu = (Button) statisticTableDialog.findViewById(R.id.custom_dialog_statistic_table_btnMainMenu);
        statisticTableBtnPlayAgain = (Button) statisticTableDialog.findViewById(R.id.custom_dialog_statistic_table_btnPlayAgain);

        statisticTrueQuestionCount = (TextView) statisticTableDialog.findViewById(R.id.custom_dialog_statistic_table_textViewTrueQuestionCount);
        skorSayisi = (TextView) statisticTableDialog.findViewById(R.id.skor_sayisi);
        harfSayisi = (TextView) statisticTableDialog.findViewById(R.id.harfSayisi);
        statisticBarWordCount = (ProgressBar) statisticTableDialog.findViewById(R.id.custom_dialog_statistic_table_progressBarWordCount);

        if (oyunDurumu.matches("oyunBitti")) {
            statisticTableDialog.setCancelable(false);
            statisticTableLinear.setVisibility(View.VISIBLE);
            statisticTableImgClose.setVisibility(View.INVISIBLE);
        }

        if(cozulenSoruSayisi<maksimumSoruSayisi)
            statisticTrueQuestionCount.setText(cozulenSoruSayisi+1 + " / " + maksimumSoruSayisi);
        else
            statisticTrueQuestionCount.setText(cozulenSoruSayisi + " / " + maksimumSoruSayisi);

        harfSayisi.setText(String.valueOf(kullanilanHarfSayisi));
        skorSayisi.setText(toplamSkorDegeri.getText().toString());


        statisticTableImgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                statisticTableDialog.dismiss();
            }
        });

        statisticTableBtnMainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Main Menu
                mainIntent();
            }
        });

        statisticTableBtnPlayAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Play Again
                Intent thisIntent = new Intent(PlayActivity.this, PlayActivity.class);
                thisIntent.putExtra("heartCount", Integer.valueOf(textViewHearCount.getText().toString()));
                finish();
                startActivity(thisIntent);
            }
        });



        statisticTableDialog.getWindow().setAttributes(params);
        statisticTableDialog.show();
    }

    private void maksimumVerileriHesapla(String oyunDurumu) {
        try {
            docRef = mFirestore.collection("Questions").document("questions");
            docRef.get().addOnSuccessListener(this, new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    maksimumSoruSayisi = documentSnapshot.getData().size();
                    istatistikTablosunuGoster(oyunDurumu, maksimumSoruSayisi, kullanilanHarfSayisi, cozulenSoruSayisi);
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void btnHarfAl(View v) {
        if(Integer.valueOf(deger) != 100)
        {
            rastgeleHarfAl();
            int intDeger = Integer.valueOf(deger);
            intDeger -= 100;
            deger = String.valueOf(intDeger);
            soruCanDegeri.setText(deger);
            kullanilanHarfSayisi++;
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Lütfen tahmin edin!!", Toast.LENGTH_SHORT).show();
        }

    }

    private void kalanHakkiKaydet(String hakSayisi,String score) {
        try {
            System.out.println("kalanHakkiKaydet**********");
            HashMap<String,String> userMap = new HashMap<>();
            userMap.put("name",userNameGelen);
            userMap.put("score",score);
            docRefUsers = mFirestore.collection("Users").document(docuserGelen);
            docRefUsers.get().addOnSuccessListener(this, new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.exists())
                    {
                        System.out.println("veriler getirldi Kalan hakkı kaydet: " + documentSnapshot.getData());
                        userMap.put("heart",hakSayisi);
                        docRefUsers.set(userMap);
                    }
                }
            }).addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    System.out.println(e.getMessage());
                }
            });
            //can sayısını gunceller ve ekrana yazdırır(oyunda sag ust kosede)
            textViewHearCount.setText("+" + hakSayisi);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void btnTahminEt(View v) {
        textTahminDegeri = editTextTahminDegeri.getText().toString();

        if (!TextUtils.isEmpty(textTahminDegeri)) {
            if (textTahminDegeri.matches(answer)) {
                Toast.makeText(getApplicationContext(), "Tebrikler Doğru Tahminde Bulundunuz.", Toast.LENGTH_SHORT).show();
                editTextTahminDegeri.setText("");
                kacinciSoru += 1;
                cozulenSoruSayisi++;

                toplamPuan += Integer.valueOf(soruCanDegeri.getText().toString());
                toplamSkorDegeri.setText(String.valueOf(toplamPuan));
                kalanHakkiKaydet(String.valueOf(hakSayisi),toplamSkorDegeri.getText().toString());

                if (sorular >= kacinciSoru)
                {
                    System.out.println("btnTahminEt if içi");
                    SoruGetir(String.valueOf(kacinciSoru));
                }
                else
                {
                    System.out.println("btnTahminEt else içi");
                    maksimumVerileriHesapla("oyunBitti");
                }


            } else {
                if (hakSayisi > 1) {
                    hakSayisi--;
                    kalanHakkiKaydet(String.valueOf(hakSayisi), toplamSkorDegeri.getText().toString());
                    Toast.makeText(getApplicationContext(), "Yanlış Tahminde Bulundunuz, Can Sayınız Bir Azaldı.", Toast.LENGTH_SHORT).show();
                }
                else if(hakSayisi == 1){
                    hakSayisi--;
                    kalanHakkiKaydet(String.valueOf(hakSayisi), toplamSkorDegeri.getText().toString());
                    maksimumVerileriHesapla("oyunBitti");
                    Toast.makeText(getApplicationContext(), "Oyun Bitti.!", Toast.LENGTH_SHORT).show();
                }
                else{
                    kalanHakkiKaydet(String.valueOf(hakSayisi), toplamSkorDegeri.getText().toString());
                    maksimumVerileriHesapla("oyunBitti");
                    Toast.makeText(getApplicationContext(), "Oyun Bitti.!", Toast.LENGTH_SHORT).show();
                }
            }
        } else
            Toast.makeText(getApplicationContext(), "Tahmin Değeri Boş Olamaz.", Toast.LENGTH_SHORT).show();
    }

    private void rastgeleHarfAl() {
        if (answerList.size() > 0) {
            rndHarfNumber = rndHarf.nextInt(answerList.size());
            String[] txtHarfler = textViewQuest.getText().toString().split(" ");
            char[] gelenKelimeHarfler = answer.toCharArray();

            for (int i = 0; i < answer.length(); i++) {
                if (txtHarfler[i].equals("_") && gelenKelimeHarfler[i] == answerList.get(rndHarfNumber)) {
                    txtHarfler[i] = String.valueOf(answerList.get(rndHarfNumber));
                    kelimeBilgisi = "";

                    for (int j = 0; j < txtHarfler.length; j++) {
                        if (j < txtHarfler.length - 1)
                            kelimeBilgisi += txtHarfler[j] + " ";
                        else
                            kelimeBilgisi += txtHarfler[j];
                    }

                    break;
                }
            }
            textViewQuest.setText(kelimeBilgisi);
            answerList.remove(rndHarfNumber);
        }
    }

    private void mainIntent() {
        Intent mainIntent = new Intent(this, MainActivity.class);
        finish();
        mainIntent.putExtra("heartCount", hakSayisi);
        startActivity(mainIntent);
        overridePendingTransition(R.anim.slide_out_up, R.anim.slide_in_down);
    }

    private void CevapGetir(String cevapNumber) {
        System.out.println("CevapGetir**********");
        kelimeBilgisi = "";
        mFirestore = FirebaseFirestore.getInstance();
        docRefAnswer = mFirestore.collection("Answers").document("answers");
        docRefAnswer.get().addOnSuccessListener(this, new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists())
                    System.out.println("RandomKelimeGetir getirldi: " + documentSnapshot.getData());
                answer = (String)documentSnapshot.getData().get(cevapNumber);
                System.out.println("Cevap1: " +answer);

                for (int i = 0; i < answer.length(); i++) {
                    if (i < answer.length() - 1)
                        kelimeBilgisi += "_ ";
                    else
                        kelimeBilgisi += "_";
                }
                textViewQuest.setText(kelimeBilgisi);

                System.out.println("Gelen Kelime = " + answer);
                System.out.println("Gelen Kelime Harf Sayısı = " + answer.length());
                answerList = new ArrayList<>();

                for (char harf : answer.toCharArray())
                    answerList.add(harf);

                if (answer.length() >= 5 && answer.length() <= 7)
                    rastgeleBelirlenecekHarfSayisi = 1;
                else if (answer.length() >= 8 && answer.length() <= 10)
                    rastgeleBelirlenecekHarfSayisi = 2;
                else if (answer.length() >= 11 && answer.length() <= 14)
                    rastgeleBelirlenecekHarfSayisi = 3;
                else if (answer.length() >= 15)
                    rastgeleBelirlenecekHarfSayisi = 4;
                else
                    rastgeleBelirlenecekHarfSayisi = 0;

                System.out.println("eksilen puan: " + (answer.length()-rastgeleBelirlenecekHarfSayisi)*100);
                deger = String.valueOf((answer.length()-rastgeleBelirlenecekHarfSayisi)*100);
                soruCanDegeri.setText(deger);

               for (int i = 0; i < rastgeleBelirlenecekHarfSayisi; i++)
                   rastgeleHarfAl();

            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("Play activity error: " + e.getMessage());
            }
        });
    }
}