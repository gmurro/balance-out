package it.uniba.di.sms1920.madminds.balanceout.ui.detailGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;

import it.uniba.di.sms1920.madminds.balanceout.R;
import it.uniba.di.sms1920.madminds.balanceout.helper.CircleTrasformation;
import it.uniba.di.sms1920.madminds.balanceout.model.Group;
import it.uniba.di.sms1920.madminds.balanceout.model.Movement;

public class BalanceDebtActivity extends AppCompatActivity {

    String idGroup;
    Movement movement;
    TextView textDebtToBalanceTextView;
    ImageView imgDebtorBalanceDebtImageView, imgCreditorBalanceDebtImageView;
    TextInputEditText valueDebtToBalanceEditText;
    MaterialButton balanceDebtButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance_debt);

        Toolbar toolbar = (Toolbar) findViewById(R.id.balanceDebtToolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //viene letto il movimento passato dall'activity precedente
        movement = (Movement) getIntent().getSerializableExtra(Movement.MOVEMENTS);
        idGroup = getIntent().getStringExtra(Group.ID_GROUP);

        textDebtToBalanceTextView = findViewById(R.id.textDebtToBalanceTextView);
        imgDebtorBalanceDebtImageView = findViewById(R.id.imgDebtorBalanceDebtImageView);
        imgCreditorBalanceDebtImageView = findViewById(R.id.imgCreditorBalanceDebtImageView);
        valueDebtToBalanceEditText = findViewById(R.id.valueDebtToBalanceEditText);
        balanceDebtButton = findViewById(R.id.balanceDebtButton);

        //viene settata la foto del debitore
        if (movement.getDebitor().getPicture() != null) {
            imgDebtorBalanceDebtImageView.setPadding(9, 9, 9, 9);
            Picasso.get().load(movement.getDebitor().getPicture()).fit().centerInside().transform(new CircleTrasformation()).into(imgDebtorBalanceDebtImageView);
        }

        //viene settata la foto del creditore
        if (movement.getCreditor().getPicture() != null) {
            imgCreditorBalanceDebtImageView.setPadding(9, 9, 9, 9);
            Picasso.get().load(movement.getCreditor().getPicture()).fit().centerInside().transform(new CircleTrasformation()).into(imgCreditorBalanceDebtImageView);
        }

        //viene settato il testo che spiega chi deve dare a chi
        textDebtToBalanceTextView.setText(movement.getDebitor().getName() + " " + movement.getDebitor().getSurname() + " " +
                getString(R.string.title_paid_to) + " " +
                movement.getCreditor().getName() + " " + movement.getCreditor().getSurname()
        );

        //viene settato l'importo da pareggiare
        valueDebtToBalanceEditText.setText(movement.getAmount());

        balanceDebtButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //se non ci sono errori
                if(!checkError()){

                    //viene convertito l'importo in stringa con 2 cifre decimali
                    String amount = String.format("%.2f", new BigDecimal(valueDebtToBalanceEditText.getText().toString())).replace(",",".");

                    //viene scritto sul db un nuovo movimento opposto a quello da pareggiare
                    Movement m = new Movement(movement.getUidDebitor(), movement.getUidCreditor(), amount, null, true);
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(Movement.MOVEMENTS).child(idGroup);
                    final String key = databaseReference.push().getKey();
                    m.setIdMovement(key);

                    //scrittura su db
                    databaseReference.child(key).setValue(m.toMap());

                    finish();
                }
            }
        });
    }

    private boolean checkError() {
        if (valueDebtToBalanceEditText.getText().toString().trim().isEmpty()|| valueDebtToBalanceEditText.getText().toString().equals(".")) {
            valueDebtToBalanceEditText.setError(getString(R.string.error_input_amount_debt));
            return true;
        }

        //se il campo relativo al pagamento in corrispondenza di un membro selezionato ha più di 2 cifre decimali, viene segnalato un errore
        if (!isMaxTwoDecimalPlaces(valueDebtToBalanceEditText.getText().toString())) {
            valueDebtToBalanceEditText.setError(getString(R.string.title_error_decimal_places));
            return true;
        }

        //non si può inserire un valore maggiore del debito che si ha
        BigDecimal amount = new BigDecimal(valueDebtToBalanceEditText.getText().toString());
        BigDecimal debt = new BigDecimal(movement.getAmount());
        if (amount.compareTo(debt) > 0) {
            valueDebtToBalanceEditText.setError(getString(R.string.error_max_input_amount_debt));
            return true;
        }

        return false;
    }

    //controlla se una stringa contenente un numero ha piu di 2 nuemri dopo il punto
    private boolean isMaxTwoDecimalPlaces(String decimal) {
        if (decimal.contains(".")) {
            String decimalPart = decimal.substring(decimal.lastIndexOf(".") + 1);
            if (decimalPart.length() > 2) {
                return false;
            }
        }
        return true;
    }
}
