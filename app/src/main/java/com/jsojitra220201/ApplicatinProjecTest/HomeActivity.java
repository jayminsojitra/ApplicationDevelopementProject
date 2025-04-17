package com.jsojitra220201.ApplicatinProjecTest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jsojitra220201.applicatinprojectest.R;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class HomeActivity extends AppCompatActivity {
    ListView listView;
    Button navToSessionDetails, navToAddParticipant, navToPlay;
    Realm realm;
    ArrayList<Session> sessionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ✅ Realm setup with schema auto-reset for dev
        Realm.init(getApplicationContext());
        RealmConfiguration config = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .allowWritesOnUiThread(true)
                .build();
        Realm.setDefaultConfiguration(config);
        realm = Realm.getDefaultInstance();

        // ✅ Clear old data and load new test data
        realm.executeTransaction(r -> {
            r.deleteAll();
            List<Session> sessions = loadTestSessionsFromJson();
            for (Session s : sessions) {
                Session realmSession = r.createObject(Session.class, UUID.randomUUID().toString());
                realmSession.setSportName(s.getSportName());
                realmSession.setNumberOfParticipants(s.getNumberOfParticipants());
                realmSession.setTimeFrame(s.getTimeFrame());
                realmSession.setGymNumber(s.getGymNumber());
                realmSession.configureSportRules();

                // ✅ Add 3 test participants
                for (int i = 1; i <= 3; i++) {
                    Participant p = r.createObject(Participant.class, UUID.randomUUID().toString());
                    p.setFirstName(s.getSportName() + "Player" + i);
                    p.setLastName("Test");
                    p.setPhoneNumber("555-000" + i);
                    p.setMembershipNumber("M00" + i);
                    realmSession.getParticipants().add(p);
                }
            }
        });

        setContentView(R.layout.activity_home);

        listView = findViewById(R.id.sessionListView);
        navToSessionDetails = findViewById(R.id.navToSessionDetails);
        navToAddParticipant = findViewById(R.id.navToAddParticipant);
        navToPlay = findViewById(R.id.navToPlay);

        RealmResults<Session> results = realm.where(Session.class).findAll();
        sessionList = new ArrayList<>(realm.copyFromRealm(results));
        SessionAdapter adapter = new SessionAdapter(this, sessionList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            Intent intent = new Intent(HomeActivity.this, SessionDetailsActivity.class);
            intent.putExtra("sessionId", sessionList.get(position).getId());
            startActivity(intent);
        });

        navToSessionDetails.setOnClickListener(v -> {
            if (!sessionList.isEmpty()) {
                Intent i = new Intent(this, SessionDetailsActivity.class);
                i.putExtra("sessionId", sessionList.get(0).getId());
                startActivity(i);
            }
        });

        navToAddParticipant.setOnClickListener(v -> {
            if (!sessionList.isEmpty()) {
                Intent i = new Intent(this, com.jsojitra220201.ApplicatinProjecTest.AddParticipantActivity.class);
                i.putExtra("sessionId", sessionList.get(0).getId());
                startActivity(i);
            }
        });

        navToPlay.setOnClickListener(v -> {
            Intent i = new Intent(this, PlayActivity.class);
            startActivity(i);
        });
    }

    private List<Session> loadTestSessionsFromJson() {
        List<Session> sessions = new ArrayList<>();
        Gson gson = new Gson();
        String json = "{\n" +
                "  \"Sessions\": [\n" +
                "    {\"sportName\": \"Basketball\", \"numberOfParticipants\": 20, \"timeFrame\": \"3:00PM - 5:00PM\", \"gymNumber\": 1},\n" +
                "    {\"sportName\": \"Badminton\", \"numberOfParticipants\": 16, \"timeFrame\": \"5:00PM - 6:30PM\", \"gymNumber\": 2},\n" +
                "    {\"sportName\": \"Volleyball\", \"numberOfParticipants\": 15, \"timeFrame\": \"6:30PM - 8:30PM\", \"gymNumber\": 1},\n" +
                "    {\"sportName\": \"Pickleball\", \"numberOfParticipants\": 10, \"timeFrame\": \"7:00PM - 8:30PM\", \"gymNumber\": 2},\n" +
                "    {\"sportName\": \"Dodgeball\", \"numberOfParticipants\": 25, \"timeFrame\": \"4:30PM - 6:00PM\", \"gymNumber\": 3}\n" +
                "  ]\n" +
                "}";

        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
        JsonArray jsonArray = jsonObject.getAsJsonArray("Sessions");
        for (JsonElement element : jsonArray) {
            Session session = gson.fromJson(element, Session.class);
            sessions.add(session);
        }
        return sessions;
    }
}
