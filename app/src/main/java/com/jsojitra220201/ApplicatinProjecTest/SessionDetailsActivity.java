package com.jsojitra220201.ApplicatinProjecTest;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.jsojitra220201.applicatinprojectest.R;

import java.util.ArrayList;

import io.realm.Realm;

public class SessionDetailsActivity extends AppCompatActivity {

    TextView sessionInfo, spotsText;
    ListView participantListView;
    Button addParticipantBtn, dropParticipantBtn;
    Button navToHome, navToAddParticipant, navToPlay;
    Session session;
    Realm realm;
    String sessionId;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Realm.init(getApplicationContext());
        setContentView(R.layout.activity_session_details);

        // Views
        sessionInfo = findViewById(R.id.sessionInfo);
        spotsText = findViewById(R.id.spotsAvailable);
        participantListView = findViewById(R.id.participantList);
        addParticipantBtn = findViewById(R.id.addParticipantBtn);
        dropParticipantBtn = findViewById(R.id.dropParticipantBtn);
        navToHome = findViewById(R.id.navToHome);
        navToAddParticipant = findViewById(R.id.navToAddParticipant);
        navToPlay = findViewById(R.id.navToPlay);

        // Realm setup
        realm = Realm.getDefaultInstance();
        sessionId = getIntent().getStringExtra("sessionId");

        if (sessionId == null) {
            Toast.makeText(this, "No session ID received", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        session = realm.where(Session.class).equalTo("id", sessionId).findFirst();

        if (session == null) {
            Toast.makeText(this, "Session not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d("DEBUG", "Opened SessionDetails for sessionId = " + sessionId);
        refreshUI();

        // Add participant button
        addParticipantBtn.setOnClickListener(v -> {
            Intent intent = new Intent(SessionDetailsActivity.this, AddParticipantActivity.class);
            intent.putExtra("sessionId", sessionId);
            startActivity(intent);
        });

        // Drop last participant safely
        dropParticipantBtn.setOnClickListener(v -> {
            realm.executeTransaction(r -> {
                Session managed = r.where(Session.class).equalTo("id", sessionId).findFirst();
                if (managed != null && managed.getParticipants().size() > 0) {
                    int lastIndex = managed.getParticipants().size() - 1;
                    managed.getParticipants().remove(lastIndex);
                    Toast.makeText(this, "Last participant dropped", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "No participants to drop", Toast.LENGTH_SHORT).show();
                }
            });
            refreshUI();
        });

        // Navigation buttons
        navToHome.setOnClickListener(v -> startActivity(new Intent(this, HomeActivity.class)));

        navToAddParticipant.setOnClickListener(v -> {
            Intent i = new Intent(this, AddParticipantActivity.class);
            i.putExtra("sessionId", sessionId);
            startActivity(i);
        });

        navToPlay.setOnClickListener(v -> startActivity(new Intent(this, PlayActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshUI();
    }

    private void refreshUI() {
        session = realm.where(Session.class).equalTo("id", sessionId).findFirst();
        if (session == null) return;

        sessionInfo.setText("Sport: " + session.getSportName()
                + "\nTime: " + session.getTimeFrame()
                + "\nGym: " + session.getGymNumber());

        int availableSpots = session.getMaxOverbookedSize() - session.getParticipants().size();
        spotsText.setText("Available Spots: " + availableSpots);

        ArrayList<String> participantNames = new ArrayList<>();
        for (Participant p : session.getParticipants()) {
            participantNames.add(p.getFirstName() + " " + p.getLastName() + " (" + p.getMembershipNumber() + ")");
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, participantNames);
        participantListView.setAdapter(adapter);
    }
}
