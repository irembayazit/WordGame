package com.irem.wordproject;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SiralamaSon extends AppCompatActivity {

    TextView myText;

    String [][]abc = new String[10][10];
    private TextView birName,ikiName,ucName,dortName,besName,altiName,yediName,sekizName,dokuzName,onName;
    private TextView birPuan,ikiPuan,ucPuan,dortPuan,besPuan,altiPuan,yediPuan,sekizPuan,dokuzPuan,onPuan;
    List<Users> myList = new ArrayList<>();
    private DocumentReference noteRef;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    String score, name;
    StorageReference storageReference;
    ImageView birResim,ikiResim,ucResim,dortResim,besResim,altiResim,yediResim,sekizResim,dokuzResim,onResim;
    private FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_dialog_siralama);

        storageReference = FirebaseStorage.getInstance().getReference();

        birResim = (ImageView)findViewById(R.id.main_activity_circleImageViewProfilebir);
        ikiResim = (ImageView)findViewById(R.id.main_activity_circleImageViewProfileiki);
        ucResim = (ImageView)findViewById(R.id.main_activity_circleImageViewProfileuc);
        dortResim = (ImageView)findViewById(R.id.main_activity_circleImageViewProfiledort);
        besResim = (ImageView)findViewById(R.id.main_activity_circleImageViewProfilebes);
        altiResim = (ImageView)findViewById(R.id.main_activity_circleImageViewProfilealti);
        yediResim = (ImageView)findViewById(R.id.main_activity_circleImageViewProfileyedi);
        sekizResim = (ImageView)findViewById(R.id.main_activity_circleImageViewProfilesekiz);
        dokuzResim = (ImageView)findViewById(R.id.main_activity_circleImageViewProfiledokuz);
        onResim = (ImageView)findViewById(R.id.main_activity_circleImageViewProfileon);


        birName = (TextView)findViewById(R.id.birAd);
        ikiName = (TextView)findViewById(R.id.iki);
        ucName = (TextView)findViewById(R.id.uc);
        dortName = (TextView)findViewById(R.id.dort);
        besName = (TextView)findViewById(R.id.bes);
        altiName = (TextView)findViewById(R.id.alti);
        yediName = (TextView)findViewById(R.id.yedi);
        sekizName = (TextView)findViewById(R.id.sekiz);
        dokuzName = (TextView)findViewById(R.id.dokuz);
        onName = (TextView)findViewById(R.id.on);
        birPuan = (TextView)findViewById(R.id.birPuan);
        ikiPuan = (TextView)findViewById(R.id.ikiPuan);
        ucPuan = (TextView)findViewById(R.id.ucPuan);
        dortPuan = (TextView)findViewById(R.id.dortPuan);
        besPuan = (TextView)findViewById(R.id.besPuan);
        altiPuan = (TextView)findViewById(R.id.altiPuan);
        yediPuan = (TextView)findViewById(R.id.yediPuan);
        sekizPuan = (TextView)findViewById(R.id.sekizPuan);
        dokuzPuan = (TextView)findViewById(R.id.dokuzPuan);
        onPuan = (TextView)findViewById(R.id.onPuan);

        bilgiAl();



    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseFirestore.getInstance()
                .collection("Users")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();
                        for(DocumentSnapshot snapshot: snapshotList){
                            String document = snapshot.getId();//user1-user2
                            noteRef = db.document("Users/" + document);
                            noteRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                @Override
                                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                    System.out.println("value: " + value);
                                    System.out.println("value.getId: " + value.getId());
                                    System.out.println("value.name: " + value.getData().get("name").toString());
                                    score = value.getData().get("score").toString();
                                    name = value.getData().get("name").toString();


                                    for (Users list:myList)
                                    {
                                        System.out.println("namelist: " + list.getName());
                                        if(list.getName().equals(name))
                                        {
                                            list.setScore(Integer.valueOf(score));
                                            System.out.println("scoreList: " + list.getScore());
                                        }
                                    }
                                    Collections.sort(myList,new Users.Byscore());
                                    showlist(myList);


                                }
                            });
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


    public void bilgiAl(){
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

                            String score = snapshot.getData().get("score").toString();
                            String heart = snapshot.getData().get("heart").toString();
                            String name = snapshot.getData().get("name").toString();
                            Users userss=new Users(name,heart,Integer.valueOf(score));
                            myList.add(userss);
                            System.out.println("aaaa: " + myList.size());
                        }

                        System.out.println("aaaa1: " + myList.get(0));
                        System.out.println("aaaa1: " + myList.get(1));

                        Collections.sort(myList,new Users.Byscore());
                        System.out.println("aaaa2: " + myList.get(0));
                        System.out.println("aaaa2: " + myList.get(1));

                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("onFailure: " + e.getMessage());
                    }
                });


    }


    public void showlist(List<Users> myList){
        System.out.println("myList.size(): " + myList.size());

            onPuan.setText(String.valueOf(myList.get(9).getScore()));
            dokuzPuan.setText(String.valueOf(myList.get(8).getScore()));
            sekizPuan.setText(String.valueOf(myList.get(7).getScore()));
            yediPuan.setText(String.valueOf(myList.get(6).getScore()));
            altiPuan.setText(String.valueOf(myList.get(5).getScore()));
            besPuan.setText(String.valueOf(myList.get(4).getScore()));
            dortPuan.setText(String.valueOf(myList.get(3).getScore()));
            ucPuan.setText(String.valueOf(myList.get(2).getScore()));
            ikiPuan.setText(String.valueOf(myList.get(1).getScore()));
            birPuan.setText(String.valueOf(myList.get(0).getScore()));
            onName.setText(myList.get(9).getName());
            dokuzName.setText(myList.get(8).getName());
            sekizName.setText(myList.get(7).getName());
            yediName.setText(myList.get(6).getName());
            altiName.setText(myList.get(5).getName());
            besName.setText(myList.get(4).getName());
            dortName.setText(myList.get(3).getName());
            ucName.setText(myList.get(2).getName());
            ikiName.setText(myList.get(1).getName());
            birName.setText(myList.get(0).getName());


        FirebaseFirestore.getInstance()
                .collection("Users")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        System.out.println("on success");
                        List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot snapshot : snapshotList) {
                            System.out.println("snapshotList.get(1).getId() : " + snapshotList.get(0).getId());
                            StorageReference profileRef = storageReference.child(snapshot.getId() + "/profile.png");
                            profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {


                                        if (myList.get(0).getName().equals(snapshot.get("name")))
                                            Picasso.get().load(uri).into(birResim);
                                        else if(myList.get(1).getName().equals(snapshot.get("name")))
                                            Picasso.get().load(uri).into(ikiResim);
                                        else if(myList.get(2).getName().equals(snapshot.get("name")))
                                            Picasso.get().load(uri).into(ucResim);
                                        else if(myList.get(3).getName().equals(snapshot.get("name")))
                                            Picasso.get().load(uri).into(dortResim);
                                        else if(myList.get(4).getName().equals(snapshot.get("name")))
                                            Picasso.get().load(uri).into(besResim);
                                        else if(myList.get(5).getName().equals(snapshot.get("name")))
                                            Picasso.get().load(uri).into(altiResim);
                                        else if(myList.get(6).getName().equals(snapshot.get("name")))
                                            Picasso.get().load(uri).into(yediResim);
                                        else if(myList.get(7).getName().equals(snapshot.get("name")))
                                            Picasso.get().load(uri).into(sekizResim);
                                        else if(myList.get(8).getName().equals(snapshot.get("name")))
                                            Picasso.get().load(uri).into(dokuzResim);
                                        else if(myList.get(9).getName().equals(snapshot.get("name")))
                                            Picasso.get().load(uri).into(onResim);

                                }
                            });

                        }
                    }
                });
    }

    public void siralamaKapat(View v) {
        Intent siralamaIntent = new Intent(this, MainActivity.class);
        finish();
        startActivity(siralamaIntent);
    }

}