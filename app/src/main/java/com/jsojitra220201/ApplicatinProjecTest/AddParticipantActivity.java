package com.jsojitra220201.ApplicatinProjecTest;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.jsojitra220201.applicatinprojectest.R;

import java.util.UUID;

import io.realm.Realm;

public class AddParticipantActivity extends AppCompatActivity {

    EditText firstNameInput, lastNameInput, phoneInput, membershipInput;
    Button submitBtn;
    TextView spotInfo;
    String sessionId;
    Realm realm;
    Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Realm.init(getApplicationContext());
        setContentView(R.layout.activity_add_participant);

        realm = Realm.getDefaultInstance();
        sessionId = getIntent().getStringExtra("sessionId");

        firstNameInput = findViewById(R.id.firstNameInput);
        lastNameInput = findViewById(R.id.lastNameInput);
        phoneInput = findViewById(R.id.phoneInput);
        membershipInput = findViewById(R.id.membershipInput);
        submitBtn = findViewById(R.id.submitBtn);
        spotInfo = findViewById(R.id.spotInfo);

        session = realm.where(Session.class).equalTo("id", sessionId).findFirst();
        if (session != null) {
            updateSpotsText();
        }

        submitBtn.setOnClickListener(v -> {
            if (session == null) {
                Toast.makeText(this, "Session not found", Toast.LENGTH_SHORT).show();
                return;
            }

            String firstName = firstNameInput.getText().toString().trim();
            String lastName = lastNameInput.getText().toString().trim();
            String phone = phoneInput.getText().toString().trim();
            String memberId = membershipInput.getText().toString().trim();

            if (firstName.isEmpty() || lastName.isEmpty() || phone.isEmpty() || memberId.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            realm.executeTransaction(r -> {
                Session managed = r.where(Session.class).equalTo("id", sessionId).findFirst();
                if (managed == null) return;

                int max = managed.getMaxOverbookedSize();
                int current = managed.getParticipants().size();

                if (current < max) {
                    Participant p = r.createObject(Participant.class, UUID.randomUUID().toString());
                    p.setFirstName(firstName);
                    p.setLastName(lastName);
                    p.setPhoneNumber(phone);
                    p.setMembershipNumber(memberId);
                    managed.getParticipants().add(p);

                    Toast.makeText(this, "Participant Added", Toast.LENGTH_SHORT).show();
                    updateSpotsText();

                    Intent intent = new Intent(this, com.jsojitra220201.ApplicatinProjecTest.SessionDetailsActivity.class);
                    intent.putExtra("sessionId", sessionId);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "No spots available.", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void updateSpotsText() {
        int max = session.getMaxOverbookedSize();
        int current = session.getParticipants().size();
        int left = max - current;
        spotInfo.setText("Available Spots: " + left + " / " + max);
    }
}
