package com.irem.wordproject;
import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.vungle.mediation.VungleAdapter;
import com.vungle.mediation.VungleExtrasBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements PurchasesUpdatedListener, RewardedVideoAdListener {

    private TextView txtUserHeartCount, txtViewUserName;
    private int imgHeartDuration = 2000, sonCanDurumu;

    private RewardedVideoAd mRewardedAd;
    private Bundle extras;
    private AdRequest adRequest;
    private AdView mAdView;

    private ConstraintLayout constra;
    private ImageView imgHeart;
    private Bitmap imgHeartBitmap;
    private ConstraintLayout.LayoutParams heartParams;
    private ImageView imgHeartDesign;
    private float imgHeartXPos, imgHeartYPos;
    private ObjectAnimator objectAnimatiorHeartX, objectAnimatiorHeartY;
    private AnimatorSet imgHeartAnimatorSet;

    private WindowManager.LayoutParams params;

    //Settings Dialog
    private Dialog settingsDialog;
    private ImageView settingsImgClose;
    private Button settingsBtnChangeName, settingsBtnChangeProfileImg;


    //Change Name Dialog
    private Dialog changeNameDialog;
    private ImageView changeNameImgClose;
    private EditText changeNameEditTxtName;
    private Button changeNameDialogBtn;
    private String getChangeName;

    private int izinVerme = 0, izinVerildi = 1;
    private Intent resimDegistirIntent;
    private Uri resimUri;
    private ImageDecoder.Source resimDosyası;
    private CircleImageView userProfileImage;


    private Dialog getHeartDialog;
    private ImageView getHeartImgClose, getHeartShowAndGet, getHeartBuyAndGet;

    private Dialog shopDialog;
    private ImageView shopDialogImgClose;
    private RecyclerView shopDialogRecyclerView;
    private ShopAdapter adapter;
    private GridLayoutManager manager;

    private BillingClient mBillingClient;
    private ArrayList<String> skuList;
    private ArrayList<Integer> heartList;
    private int heartPos, userHeart2;
    private FirebaseFirestore mFirestore;
    private DocumentReference docRef;
    String userName1="", userHeart, docUser,score;
    StorageReference storageReference;


    private void textIsimYazma(String uid){
        docUser = uid;
        docRef = mFirestore.collection("Users").document(uid);
        docRef.get().addOnSuccessListener(this, new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists())
                    System.out.println("veriler getirldi: " + documentSnapshot.getData());
                userHeart = (String)documentSnapshot.getData().get("heart");
                userName1 = (String)documentSnapshot.getData().get("name");
                score = (String)documentSnapshot.getData().get("score");
                System.out.println("userName1 getirldi: " + userName1);
                System.out.println("userHeart getirldi: " + userHeart);
                userHeart2 = Integer.valueOf(userHeart);
                txtViewUserName = (TextView) findViewById(R.id.main_activity_textViewUserName);
                txtUserHeartCount = (TextView) findViewById(R.id.main_activity_textViewUserHeartCount);
                txtViewUserName.setText(userName1);
                txtUserHeartCount.setText("+" + userHeart);
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println(e.getMessage());
            }
        });
    }


    public void IsımDegistir(String newName){
        mFirestore = FirebaseFirestore.getInstance();
        HashMap<String,String> userMap = new HashMap<>();
        userMap.put("name",newName);
        txtViewUserName = (TextView) findViewById(R.id.main_activity_textViewUserName);
        FirebaseFirestore.getInstance()
                .collection("Users")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        System.out.println("on success");
                        List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();
                        for(DocumentSnapshot snapshot: snapshotList){
                            System.out.println("for iç getId: " + snapshot.getId());//user1-user2
                            System.out.println("for iç name: " + snapshot.getData().get("name").toString());//irem-sitem
                            System.out.println("for iç heart: " + snapshot.getData().get("heart").toString());//irem-sitem
                            String document = snapshot.getId();
                            String name = snapshot.getData().get("name").toString();
                            String heart = snapshot.getData().get("heart").toString();
                            score = snapshot.getData().get("score").toString();
                            userMap.put("heart",heart);
                            userMap.put("score",score);
                            System.out.println("varsayılan isim: " + txtViewUserName.getText().toString());
                            if ((name.matches(txtViewUserName.getText().toString()))){
                                System.out.println("eşit isim");
                                mFirestore.collection("Users").document(document)
                                        .set(userMap)
                                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful())
                                                {
                                                    textIsimYazma(document);
                                                    System.out.println("isim değiştirildi");
                                                }
                                                else
                                                    System.out.println("Hata : " + task.getException().getMessage());
                                            }
                                        });
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("onFailure: " + e.getMessage());
                    }
                });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //ekleneecek her yei kişi için yer ayırılacak
        /*HashMap<String,String> userMap = new HashMap<>();
        userMap.put("name","Sitem");
        userMap.put("heart","3");
        userMap.put("score","4533");
        mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection("Users").document("user11")
                .set(userMap)
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                            System.out.println("Veritabanı kayıt işlemi başarılı");
                        else
                            System.out.println("Hata : " + task.getException().getMessage());
                    }
                });*/


        mFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        textIsimYazma("user6");

        StorageReference profileRef = storageReference.child(docUser + "/profile.png");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(userProfileImage);
            }
        });


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtUserHeartCount = (TextView) findViewById(R.id.main_activity_textViewUserHeartCount);
        txtViewUserName = (TextView) findViewById(R.id.main_activity_textViewUserName);
        userProfileImage = (CircleImageView)findViewById(R.id.main_activity_circleImageViewProfile);
        constra = (ConstraintLayout) findViewById(R.id.main_activity_constra);
        mAdView = (AdView) findViewById(R.id.main_activity_adView);
        imgHeartDesign = (ImageView) findViewById(R.id.main_activity_imageViewHeartDesign);

        extras = new VungleExtrasBuilder(new String[]{"MAINREWARDED-1460793"})
                .setUserId("test_user")
                .build();

        adRequest = new AdRequest.Builder()
                .addNetworkExtrasBundle(VungleAdapter.class, extras)
                .build();

        mRewardedAd = MobileAds.getRewardedVideoAdInstance(getApplicationContext());

        mRewardedAd.setRewardedVideoAdListener(this);

        mRewardedAd.loadAd("ca-app-pub-3940256099942544/5224354917", adRequest);


        skuList = new ArrayList<>();
        heartList = new ArrayList<>();

        skuList.add("buy_heart1");
        skuList.add("buy_heart2");
        skuList.add("buy_heart3");
        skuList.add("buy_heart4");
        skuList.add("buy_heart5");

        heartList.add(3);
        heartList.add(15);
        heartList.add(50);
        heartList.add(90);
        heartList.add(500);

        mBillingClient = BillingClient.newBuilder(this)
                .setListener(this)
                .enablePendingPurchases()
                .build();
        mBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() != BillingClient.BillingResponseCode.OK)
                    Toast.makeText(getApplicationContext(), "Ödeme sistemi için google play hesabınızı kontrol ediniz.", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onBillingServiceDisconnected() {
                Toast.makeText(getApplicationContext(), "Ödeme sistemi şu anda geçerli değil.", Toast.LENGTH_SHORT).show();
            }
        });


        //banner reklam
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mAdView.setVisibility(View.VISIBLE);
            }
        });

        imgHeart = new ImageView(this);
        imgHeartBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.heart);
        imgHeart.setImageBitmap(imgHeartBitmap);
        heartParams = new ConstraintLayout.LayoutParams(96, 96);
        imgHeart.setLayoutParams(heartParams);
        imgHeart.setX(0);
        imgHeart.setY(0);
        imgHeart.setVisibility(View.INVISIBLE);
        constra.addView(imgHeart);
    }


public void mainBtnClick(View v) {
        switch (v.getId()) {
            case R.id.main_activity_btnPlay:
                Intent playIntent = new Intent(this, PlayActivity.class);
                finish();
                playIntent.putExtra("heartCount", userHeart2);
                playIntent.putExtra("docUser", docUser);
                playIntent.putExtra("userName1", userName1);
                playIntent.putExtra("score", score);
                startActivity(playIntent);
                overridePendingTransition(R.anim.slide_out_up, R.anim.slide_in_down);
                break;

            case R.id.main_activity_btnShop:
                marketDiyalog();
                break;

            case R.id.main_activity_btnExit:
                uygulamadanCik();
                break;
        }
    }

    public void btnAyarlar(View v) {
        ayarlariGoster();
    }

    public void btnKupa(View v){
        Intent kupaIntent = new Intent(this, SiralamaSon.class);
        startActivity(kupaIntent);
    }

    private void marketDiyalog(){
        shopDialog = new Dialog(this);
        params = new WindowManager.LayoutParams();
        params.copyFrom(shopDialog.getWindow().getAttributes());
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        shopDialog.setCancelable(false);
        shopDialog.setContentView(R.layout.custom_dialog_shop);

        shopDialogImgClose = (ImageView)shopDialog.findViewById(R.id.custom_dialog_shop_imageViewClose);
        shopDialogRecyclerView = (RecyclerView)shopDialog.findViewById(R.id.custom_dialog_shop_recyclerView);

        shopDialogImgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shopDialog.dismiss();
            }
        });

        adapter = new ShopAdapter(Shop.getData(), this);
        shopDialogRecyclerView.setHasFixedSize(true);
        manager = new GridLayoutManager(this, 3);
        shopDialogRecyclerView.setLayoutManager(manager);
        shopDialogRecyclerView.addItemDecoration(new GridItemDecoration(3, 5));
        shopDialogRecyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new ShopAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Shop mShop, final int pos) {
                if (mBillingClient.isReady()){
                    SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
                    params.setSkusList(Collections.singletonList(skuList.get(pos))).setType(BillingClient.SkuType.INAPP);
                    mBillingClient.querySkuDetailsAsync(params.build(), new SkuDetailsResponseListener() {
                        @Override
                        public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> list) {
                            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null){
                                BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                                        .setSkuDetails(list.get(0))
                                        .build();

                                mBillingClient.launchBillingFlow(MainActivity.this, flowParams);
                                heartPos = pos;
                            }
                        }
                    });
                }
            }
        });

        shopDialog.getWindow().setAttributes(params);
        shopDialog.show();
    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> list) {
        if (billingResult.getResponseCode() == Purchase.PurchaseState.PURCHASED)
            handlePurchase(list.get(0));
    }

    private void handlePurchase(Purchase purchase) {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED){
            if (!purchase.isAcknowledged()){
                AcknowledgePurchaseParams purchaseParams = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.getPurchaseToken())
                        .build();

                AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener = new AcknowledgePurchaseResponseListener() {
                    @Override
                    public void onAcknowledgePurchaseResponse(BillingResult billingResult) {
                        Toast.makeText(getApplicationContext(), "Satın Alma İşlemi Başarılı.", Toast.LENGTH_SHORT).show();
                        sonCanDurumu += heartList.get(heartPos);
                        canMiktariniGuncelle(txtUserHeartCount.getText().toString());
                    }
                };

                mBillingClient.acknowledgePurchase(purchaseParams, acknowledgePurchaseResponseListener);
            }
        }
    }

    private class GridItemDecoration extends RecyclerView.ItemDecoration{
        private int spanCount;
        private int spacing;

        public GridItemDecoration(int spanCount, int spacing) {
            this.spanCount = spanCount;
            this.spacing = spacing;
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            int column = position % spanCount;

            outRect.left = (column + 1) * spacing / spanCount;
            outRect.right = (column + 1) * spacing / spanCount;
            outRect.bottom = spacing;
        }
    }

    private void canKazanmaMenusu(){
        getHeartDialog = new Dialog(this);
        params = new WindowManager.LayoutParams();
        params.copyFrom(getHeartDialog.getWindow().getAttributes());
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getHeartDialog.setCancelable(false);
        getHeartDialog.setContentView(R.layout.custom_dialog_get_heart);

        getHeartImgClose = (ImageView)getHeartDialog.findViewById(R.id.custom_dialog_get_heart_imageViewClose);
        getHeartShowAndGet = (ImageView)getHeartDialog.findViewById(R.id.custom_dialog_get_heart_imageViewShowAndGet);
        getHeartBuyAndGet = (ImageView)getHeartDialog.findViewById(R.id.custom_dialog_get_heart_imageViewBuyAndGet);

        getHeartImgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getHeartDialog.dismiss();
            }
        });

        getHeartShowAndGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mRewardedAd.isLoaded()) {
                    mRewardedAd.show();
                    getHeartDialog.dismiss();
                }
                else
                    Toast.makeText(getApplicationContext(), "Video Henüz Yüklenmedi.", Toast.LENGTH_SHORT).show();
            }
        });

        getHeartBuyAndGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Shop Menu
                marketDiyalog();
                getHeartDialog.dismiss();
            }
        });

        getHeartDialog.getWindow().setAttributes(params);
        getHeartDialog.show();
    }

    private void ayarlariGoster() {
        settingsDialog = new Dialog(this);
        params = new WindowManager.LayoutParams();
        params.copyFrom(settingsDialog.getWindow().getAttributes());
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        settingsDialog.setCancelable(false);
        settingsDialog.setContentView(R.layout.custom_dialog_settings);

        settingsImgClose = (ImageView) settingsDialog.findViewById(R.id.custom_dialog_settings_imageViewClose);
        settingsBtnChangeName = (Button) settingsDialog.findViewById(R.id.custom_dialog_settings_btnChangeName);
        settingsBtnChangeProfileImg = (Button) settingsDialog.findViewById(R.id.custom_dialog_settings_btnChangeProfileImage);

        settingsImgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingsDialog.dismiss();
            }
        });

        settingsBtnChangeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isimDegisterDiyalog();
                settingsDialog.dismiss();
            }
        });

        settingsBtnChangeProfileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, izinVerme);
                else{
                    resimDegistirIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(resimDegistirIntent, izinVerildi);
                }
            }
        });

        settingsDialog.getWindow().setAttributes(params);
        settingsDialog.show();
    }




    private void isimDegisterDiyalog() {
        changeNameDialog = new Dialog(this);
        params = new WindowManager.LayoutParams();
        params.copyFrom(changeNameDialog.getWindow().getAttributes());
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        changeNameDialog.setCancelable(false);
        changeNameDialog.setContentView(R.layout.custom_dialog_change_name);

        changeNameImgClose = (ImageView) changeNameDialog.findViewById(R.id.custom_dialog_change_name_imageViewClose);
        changeNameEditTxtName = (EditText) changeNameDialog.findViewById(R.id.custom_dialog_change_name_editTextName);
        changeNameDialogBtn = (Button) changeNameDialog.findViewById(R.id.custom_dialog_change_name_btnChangeName);

        changeNameImgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeNameDialog.dismiss();
            }
        });

        changeNameDialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getChangeName = changeNameEditTxtName.getText().toString();

                if (!TextUtils.isEmpty(getChangeName)) {
                    if (!(getChangeName.matches(txtViewUserName.getText().toString())))
                        ismiGuncelle(getChangeName);
                    else
                        Toast.makeText(getApplicationContext(), "Zaten Bu İsimi Kullanıyorsunuz.", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getApplicationContext(), "İsim Değeri Boş Olamaz.", Toast.LENGTH_SHORT).show();
            }
        });

        changeNameDialog.getWindow().setAttributes(params);
        changeNameDialog.show();
    }

    private void ismiGuncelle(String yeniDeger) {
        try {
            IsımDegistir(yeniDeger);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        //AlertDialog Açılmalı
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Kelime Bilmece");
        alert.setIcon(R.mipmap.ic_kelimebilmece);
        alert.setMessage("Uygulamadan Çıkmak İstediğinize Emin Misiniz?");
        alert.setPositiveButton("Hayır", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alert.setNegativeButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                uygulamadanCik();
            }
        });

        alert.show();
    }

    private void uygulamadanCik(){
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    public void btnHakKazan(View v) {
        canKazanmaMenusu();
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        Toast.makeText(getApplicationContext(), "Video Yüklendi,\nCan Kazanma Zamanı.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {
        mRewardedAd.loadAd("ca-app-pub-3940256099942544/5224354917", adRequest);
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {

    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
        mRewardedAd.loadAd("ca-app-pub-3940256099942544/5224354917", adRequest);
    }

    @Override
    public void onRewardedVideoCompleted() {
        mRewardedAd.loadAd("ca-app-pub-3940256099942544/5224354917", adRequest);

        imgHeart.setX(constra.getPivotX());
        imgHeart.setY(constra.getPivotY());
        imgHeart.setVisibility(View.VISIBLE);
        imgHeartXPos = (imgHeartDesign.getX() + (imgHeartDesign.getWidth() / 2f) - 48);
        imgHeartYPos = (imgHeartDesign.getY() + (imgHeartDesign.getHeight() / 2f) - 48);

        objectAnimatiorHeartX = ObjectAnimator.ofFloat(imgHeart, "x", imgHeartXPos);
        objectAnimatiorHeartX.setDuration(imgHeartDuration);

        objectAnimatiorHeartY = ObjectAnimator.ofFloat(imgHeart, "y", imgHeartYPos);
        objectAnimatiorHeartY.setDuration(imgHeartDuration);

        imgHeartAnimatorSet = new AnimatorSet();
        imgHeartAnimatorSet.playTogether(objectAnimatiorHeartX);
        imgHeartAnimatorSet.playTogether(objectAnimatiorHeartY);
        imgHeartAnimatorSet.start();
        objectAnimatiorHeartY.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                imgHeart.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), "+1 Can Kazandınız.", Toast.LENGTH_SHORT).show();
                userHeart2 += 1;
                canMiktariniGuncelle(String.valueOf(userHeart2));
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    private void canMiktariniGuncelle(String sonCanSayisi) {
        try {
            HashMap<String,String> userMap = new HashMap<>();
            userMap.put("name",userName1);
            userMap.put("score",score);
            docRef = mFirestore.collection("Users").document(docUser);
            docRef.get().addOnSuccessListener(this, new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.exists())
                    {
                        System.out.println("veriler getirldi: " + documentSnapshot.getData());
                        userMap.put("heart",sonCanSayisi);
                        docRef.set(userMap);
                    }
                }
            }).addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    System.out.println(e.getMessage());
                }
            });
            txtUserHeartCount.setText("+" + sonCanSayisi);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == izinVerme){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                resimDegistirIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(resimDegistirIntent, izinVerildi);
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == izinVerildi){
            if (resultCode == RESULT_OK && data != null){
                resimUri = data.getData();
                uploadImageToFirebase(resimUri);
            }
        }
    }

    private void uploadImageToFirebase(Uri resimUri) {

        StorageReference fileRef = storageReference.child(docUser + "/profile.png");
        fileRef.putFile(resimUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                System.out.println("Resim yüklendi!!!");
                Toast.makeText(getApplicationContext(), "Profil Resminiz Başarıyla Değiştirildi!", Toast.LENGTH_SHORT).show();
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(userProfileImage);
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("Resim yüklenemedi!!");
                Toast.makeText(getApplicationContext(), "Profil Resminiz Değiştirilemedi!.", Toast.LENGTH_SHORT).show();
            }
        });
    }


}