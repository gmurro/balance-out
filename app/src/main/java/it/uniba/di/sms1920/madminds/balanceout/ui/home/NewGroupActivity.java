package it.uniba.di.sms1920.madminds.balanceout.ui.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import com.google.android.material.snackbar.Snackbar;
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

import it.uniba.di.sms1920.madminds.balanceout.R;
import it.uniba.di.sms1920.madminds.balanceout.helper.CircleTrasformation;
import it.uniba.di.sms1920.madminds.balanceout.model.Group;
import it.uniba.di.sms1920.madminds.balanceout.model.MetadateGroup;

public class NewGroupActivity extends AppCompatActivity {

    public final int RESULT_LOAD_IMAGE=21;
    public final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 11;
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
                // Devo chiedere il permesso di poter leggere dallo storage per caricare una foto e la leggo
                checkPermissionReadExternalStorage();
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

    public void checkPermissionReadExternalStorage() {

        if (ContextCompat.checkSelfPermission(NewGroupActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(NewGroupActivity.this,
                    Manifest.permission.READ_CONTACTS)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(NewGroupActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            /* Apro la galleria per selezionare la foto */
            Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, RESULT_LOAD_IMAGE);
        }
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
                Toast.makeText(NewGroupActivity.this, getString(R.string.message_error_read_photo), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                    /* Apro la galleria per selezionare la foto */
                    Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, RESULT_LOAD_IMAGE);
                } else {
                    Snackbar.make(findViewById(R.id.newGroupConstraintLayout), getString(R.string.message_error_permission_read_external), Snackbar.LENGTH_LONG).show();
                }
                return;
            }
        }
    }
}
