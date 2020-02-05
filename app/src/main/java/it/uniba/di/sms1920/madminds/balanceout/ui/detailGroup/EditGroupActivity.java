package it.uniba.di.sms1920.madminds.balanceout.ui.detailGroup;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import it.uniba.di.sms1920.madminds.balanceout.R;
import it.uniba.di.sms1920.madminds.balanceout.helper.CircleTrasformation;
import it.uniba.di.sms1920.madminds.balanceout.model.Group;

public class EditGroupActivity extends AppCompatActivity {
    public final int RESULT_LOAD_IMAGE = 21;
    public final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 11;
    private static final String TAG = "balanceOutTracker";
    private Uri filePath;
    private Group group;
    private ImageView imgEditGroupCreateImageView;
    private TextInputEditText nameEditGroupEditText;
    private MaterialButton editGroupButton;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group);

        Toolbar toolbar = findViewById(R.id.editGroupToolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference("imagesGroups");

        group = (Group) getIntent().getExtras().getSerializable(Group.GROUP);

        imgEditGroupCreateImageView = findViewById(R.id.imgEditGroupCreateImageView);
        nameEditGroupEditText = findViewById(R.id.nameEditGroupEditText);
        editGroupButton = findViewById(R.id.editGroupButton);

        //vengono impostati nome e foto dai dati passati dall'activity precednete
        nameEditGroupEditText.setText(group.getNameGroup());
        if (group.getImgGroup() != null) {
            imgEditGroupCreateImageView.setPadding(9, 9, 9, 9);
            Picasso.get().load(group.getImgGroup()).fit().centerInside().transform(new CircleTrasformation()).into(imgEditGroupCreateImageView);
        }

        /*quando viene cliccata la foto, si puo caricare un immagine dalla galleria*/
        imgEditGroupCreateImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Devo chiedere il permesso di poter leggere dallo storage per caricare una foto e la leggo
                checkPermissionReadExternalStorage();
            }
        });


        editGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //se non è stato inserito alcun nome, non fa creare il gruppo
                if(nameEditGroupEditText.getText().toString().isEmpty()) {
                    nameEditGroupEditText.setError(getResources().getString(R.string.title_error_insert_name_group));
                } else {
                    editGroup();
                    //dopo aver creato il gruppo viene chiusa l'activity
                    finish();
                }
            }
        });
    }


    public void editGroup() {

        //scrittura del nome sul db
        String nameGroup = nameEditGroupEditText.getText().toString();
        databaseReference.child(Group.GROUPS).child(group.getIdGroup()).child(Group.NAME_GROUP).setValue(nameGroup);

        //scrittura della foto
        if(filePath != null){
            fileUpdater(group.getIdGroup());
        }
    }




    private String getExtension(Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return  mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));

    }


    private void fileUpdater(final String idGroup){

        final StorageReference ref = storageReference.child(idGroup+"."+getExtension(filePath));

        ref.putFile(filePath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        //Scrittura della posizione della foto nello storage
                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                databaseReference.child(Group.GROUPS).child(idGroup).child(Group.IMG_GROUP).setValue(uri.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //scrittura avvenuta con successo
                                    }

                                });


                            }
                        });


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {


                    }
                });


    }

    public void checkPermissionReadExternalStorage() {

        if (ContextCompat.checkSelfPermission(EditGroupActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(EditGroupActivity.this,
                    Manifest.permission.READ_CONTACTS)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(EditGroupActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            /* Apro la galleria per selezionare la foto */
            Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            Log.i(TAG, "ha chiesto di prendere le foto");
            startActivityForResult(i, RESULT_LOAD_IMAGE);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i(TAG, "request code = " + requestCode + " resultCode = " + resultCode + " ResultLoadImage/ResultOk = " + RESULT_LOAD_IMAGE + "/" + RESULT_OK);

        Log.i(TAG, "data = " + data);

        /*viene caricata l'immagine scelta dalla galleria nell image view*/
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {

            filePath = data.getData();
            imgEditGroupCreateImageView.setPadding(9, 9, 9, 9);
            Picasso.get().load(filePath).fit().centerInside().transform(new CircleTrasformation()).into(imgEditGroupCreateImageView);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
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
                    Toast.makeText(EditGroupActivity.this, "E'necessario dare il permesso per poter caricare la foto", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
}

