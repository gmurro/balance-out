package it.uniba.di.sms1920.madminds.balanceout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import it.uniba.di.sms1920.madminds.balanceout.helper.CircleTrasformation;
import it.uniba.di.sms1920.madminds.balanceout.model.Group;
import it.uniba.di.sms1920.madminds.balanceout.model.MetadateGroup;

public class NewGroupActivity extends AppCompatActivity {

    public final int RESULT_LOAD_IMAGE=21;
    private Button createGroupButton;
    private FirebaseAuth mAuth;
    private DatabaseReference reff;
    private Bitmap imgNewGroupCreateBitmap = null;
    private ImageView imgNewGroupCreateImageView;
    private TextInputEditText nameNewGroupEditText;
    private SwitchMaterial debtSemplificationNewGroupSwitch;
    private SwitchMaterial publicMovementsNewGroupSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);

        Toolbar toolbar = (Toolbar) findViewById(R.id.newGroupGroupToolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        /*inizializzazione da firebase della variabile contenete i dati dell'utente loggato*/
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        reff = FirebaseDatabase.getInstance().getReference();

        /*inizializzazione delle view*/
        createGroupButton = findViewById(R.id.createGroupButton);
        imgNewGroupCreateImageView = findViewById(R.id.imgNewGroupCreateImageView);
        nameNewGroupEditText =findViewById(R.id.nameNewGroupEditText);
        debtSemplificationNewGroupSwitch = findViewById(R.id.debtSemplificationNewGroupSwitch);
        publicMovementsNewGroupSwitch = findViewById(R.id.publicMovementsNewGroupSwitch);


        /*quando viene cliccata la foto, si puo caricare un immagine dalla galleria*/
        imgNewGroupCreateImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

        createGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //se non è stato inserito alcun nome, non fa creare il gruppo
                if(nameNewGroupEditText.getText().toString().equals("")) {
                    nameNewGroupEditText.setError(getResources().getString(R.string.title_error_insert_name_group));
                } else {
                    createNewGroup();
                }
            }
        });
    }


    private boolean createNewGroup() {
        final boolean[] success = new boolean[1];

        //questi sono i valori da memorizzare sul db
        String nameGroup = nameNewGroupEditText.getText().toString();
        boolean debtSemplification = debtSemplificationNewGroupSwitch.isChecked();
        boolean publicMovements = publicMovementsNewGroupSwitch.isChecked();

        /*viene convertita la foto in stringa, sara null invece se non c'e nessuna foto */
        String imgGroup = getStringImage(imgNewGroupCreateBitmap);

        ArrayList<String> utenti = new ArrayList<>();
        utenti.add(mAuth.getUid());


        ArrayList<String> uidMembers = new ArrayList<>();
        uidMembers.add(mAuth.getUid());

        MetadateGroup metagruppoData = new MetadateGroup(0, "00.00");

        Group g = new Group(null, getString(R.string.example_name_group),
                Calendar.getInstance().getTime(),
                null,
                null,
                utenti,
                mAuth.getUid(),
                0,
                0,
                true,
                false,
                false
        );


        Map<String, Object> gruppoMap = g.toMap();
        Map<String, Object> metadateMap = metagruppoData.toMap();

        String key = reff.child("groups").push().getKey();
        Map<String, Object> childUpdate = new HashMap<>();

        //scrittura multipla su rami differenti del db
        childUpdate.put("/groups/" + key, gruppoMap);
        childUpdate.put("/users/"+mAuth.getUid()+"/mygroups/"+key, metagruppoData);

        reff.updateChildren(childUpdate).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                success[0] = true;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                success[0] = false;
            }
        });


        return success[0];
    }


    /*codifica una foto sottoforma di stringa*/
    public String getStringImage(Bitmap bitmap){
        if (bitmap==null) {
            return null;
        }

        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,40, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);

        return temp;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /*viene caricata l'immagine scelta dalla galleria nell image view*/
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                imgNewGroupCreateBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imgNewGroupCreateImageView.setPadding(9,9,9,9);
                Picasso.get().load(filePath).fit().centerInside().transform(new CircleTrasformation()).into(imgNewGroupCreateImageView);
            } catch (IOException e) {
                Toast.makeText(NewGroupActivity.this, "Errore durante il caricamento dell'immagine", Toast.LENGTH_LONG).show();
            }
        }
    }
}
