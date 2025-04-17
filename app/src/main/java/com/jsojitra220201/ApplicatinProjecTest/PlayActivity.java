package com.jsojitra220201.ApplicatinProjecTest;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.jsojitra220201.applicatinprojectest.R;

import java.util.ArrayList;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;

public class PlayActivity extends AppCompatActivity {

    TextView roundTimer, roundLengthText, sessionLengthText, spotsLeftText, sittingCountText;
    ListView playingListView;
    Spinner sessionSpinner;
    Button navToHome, navToAdd, navToSession;
    CountDownTimer timer;
    Realm realm;
    ArrayList<Session> sessions;
    ArrayList<Participant> playing;
    Session selectedSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Realm.init(getApplicationContext());
        setContentView(R.layout.activity_play);

        roundTimer = findViewById(R.id.roundTimer);
        roundLengthText = findViewById(R.id.roundLengthText);
        sessionLengthText = findViewById(R.id.sessionLengthText);
        spotsLeftText = findViewById(R.id.spotsLeftText);
        sittingCountText = findViewById(R.id.sittingCountText);
        playingListView = findViewById(R.id.playingListView);
        sessionSpinner = findViewById(R.id.sessionSpinner);
        navToHome = findViewById(R.id.navToHome);
        navToAdd = findViewById(R.id.navToAdd);
        navToSession = findViewById(R.id.navToSession);

        realm = Realm.getDefaultInstance();

        RealmResults<Session> results = realm.where(Session.class).findAll();
        sessions = new ArrayList<>(realm.copyFromRealm(results));

        ArrayList<String> sessionTitles = new ArrayList<>();
        for (Session s : sessions) {
            sessionTitles.add(s.getSportName() + " | " + s.getTimeFrame());
        }

        sessionSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, sessionTitles));

        sessionSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                selectedSession = sessions.get(position);
                refreshSession(selectedSession.getId());
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        // Navigation
        navToHome.setOnClickListener(v -> startActivity(new Intent(this, HomeActivity.class)));

        navToAdd.setOnClickListener(v -> {
            if (selectedSession != null) {
                Intent i = new Intent(this, com.jsojitra220201.ApplicatinProjecTest.AddParticipantActivity.class);
                i.putExtra("sessionId", selectedSession.getId());
                startActivity(i);
            }
        });

        navToSession.setOnClickListener(v -> {
            if (selectedSession != null) {
                Intent i = new Intent(this, SessionDetailsActivity.class);
                i.putExtra("sessionId", selectedSession.getId());
                startActivity(i);
            }
        });
    }

    private void startNextRound(Session session) {
        playing = new ArrayList<>(session.getPlayingParticipants());

        // Count values
        int spotsLeft = session.getAvailableSpots();
        int sittingOutCount = session.getSittingParticipants().size();

        // Show updated numbers
        spotsLeftText.setText("Spots Left: " + spotsLeft);
        sittingCountText.setText("Sitting Out: " + sittingOutCount);

        // Court display
        ArrayList<String> playingDisplay = new ArrayList<>();
        int court = 1;
        int perCourt = session.getTotalPlayCapacity() / session.getGamesPerGym();

        for (int i = 0; i < playing.size(); i++) {
            if (i % perCourt == 0) court++;
            playingDisplay.add("Court " + court + ": " + playing.get(i).getFirstName() + " " + playing.get(i).getLastName());
        }

        playingListView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, playingDisplay));

        // Round/session length
        int roundMinutes = (int) (session.getPlayRoundTimeMillis() / 60000);
        int sessionMinutes = (int) (session.getTotalSessionTimeMillis() / 60000);

        String sessionFormatted = sessionMinutes >= 60
                ? (sessionMinutes / 60) + " hour" + (sessionMinutes >= 120 ? "s" : "")
                : sessionMinutes + " mins";

        roundLengthText.setText("Round Duration: " + roundMinutes + " mins");
        sessionLengthText.setText("Total Session Time: " + sessionFormatted);

        if (timer != null) timer.cancel();
        timer = new CountDownTimer(session.getPlayRoundTimeMillis(), 1000) {
            public void onTick(long millisUntilFinished) {
                long min = millisUntilFinished / 60000;
                long sec = (millisUntilFinished % 60000) / 1000;
                roundTimer.setText(String.format(Locale.getDefault(), "⏱ Time Left: %02d:%02d", min, sec));
            }

            public void onFinish() {
                roundTimer.setText(" Round Finished");
            }
        }.start();
    }

    private void refreshSession(String sessionId) {
        Session reloaded = realm.where(Session.class).equalTo("id", sessionId).findFirst();
        if (reloaded != null) {
            selectedSession = realm.copyFromRealm(reloaded);
            startNextRound(selectedSession);
        }
    }
}
