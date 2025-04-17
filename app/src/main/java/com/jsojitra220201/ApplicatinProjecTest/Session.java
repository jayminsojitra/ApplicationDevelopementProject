package com.jsojitra220201.ApplicatinProjecTest;

import java.util.ArrayList;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Session extends RealmObject {

    @PrimaryKey
    private String id;

    @Required
    private String sportName;

    private int numberOfParticipants;
    private String timeFrame;
    private int gymNumber;

    private RealmList<Participant> participants = new RealmList<>();

    private long playRoundTimeMillis;
    private long totalSessionTimeMillis;
    private int courtCapacityPerGame;
    private int gamesPerGym;

    // ===== Required Getters & Setters for Realm =====
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSportName() { return sportName; }
    public void setSportName(String sportName) { this.sportName = sportName; }

    public int getNumberOfParticipants() { return numberOfParticipants; }
    public void setNumberOfParticipants(int numberOfParticipants) { this.numberOfParticipants = numberOfParticipants; }

    public String getTimeFrame() { return timeFrame; }
    public void setTimeFrame(String timeFrame) { this.timeFrame = timeFrame; }

    public int getGymNumber() { return gymNumber; }
    public void setGymNumber(int gymNumber) { this.gymNumber = gymNumber; }

    public RealmList<Participant> getParticipants() { return participants; }
    public void setParticipants(RealmList<Participant> participants) { this.participants = participants; }

    public long getPlayRoundTimeMillis() { return playRoundTimeMillis; }
    public long getTotalSessionTimeMillis() { return totalSessionTimeMillis; }
    public int getGamesPerGym() { return gamesPerGym; }

    // ===== Logic Methods =====
    public int getTotalPlayCapacity() {
        return courtCapacityPerGame * gamesPerGym;
    }

    public int getMaxOverbookedSize() {
        return (int) Math.round(getTotalPlayCapacity() * 1.25); // 25% overbook
    }

    public int getAvailableSpots() {
        return numberOfParticipants - participants.size();
    }

    public void configureSportRules() {
        switch (sportName.toLowerCase()) {
            case "basketball":
                gymNumber = 1;
                courtCapacityPerGame = 10;
                gamesPerGym = 2;
                playRoundTimeMillis = 10 * 60 * 1000;
                totalSessionTimeMillis = 2 * 60 * 60 * 1000;
                break;
            case "badminton":
                courtCapacityPerGame = 4;
                gamesPerGym = (gymNumber == 1) ? 6 : 3;
                playRoundTimeMillis = 5 * 60 * 1000;
                totalSessionTimeMillis = 90 * 60 * 1000;
                break;
            case "volleyball":
                gymNumber = 1;
                courtCapacityPerGame = 12;
                gamesPerGym = 3;
                playRoundTimeMillis = 10 * 60 * 1000;
                totalSessionTimeMillis = 2 * 60 * 60 * 1000;
                break;
            case "pickleball":
                gymNumber = 2;
                courtCapacityPerGame = 4;
                gamesPerGym = 3;
                playRoundTimeMillis = 7 * 60 * 1000;
                totalSessionTimeMillis = 90 * 60 * 1000;
                break;
            case "dodgeball":
                gymNumber = 3;
                courtCapacityPerGame = 20;
                gamesPerGym = 1;
                playRoundTimeMillis = 8 * 60 * 1000;
                totalSessionTimeMillis = 90 * 60 * 1000;
                break;
            default:
                courtCapacityPerGame = 10;
                gamesPerGym = 1;
                playRoundTimeMillis = 10 * 60 * 1000;
                totalSessionTimeMillis = 60 * 60 * 1000;
        }
    }

    public ArrayList<Participant> getPlayingParticipants() {
        int capacity = getTotalPlayCapacity();
        ArrayList<Participant> list = new ArrayList<>();
        for (int i = 0; i < Math.min(capacity, participants.size()); i++) {
            list.add(participants.get(i));
        }
        return list;
    }

    public ArrayList<Participant> getSittingParticipants() {
        int capacity = getTotalPlayCapacity();
        ArrayList<Participant> list = new ArrayList<>();
        for (int i = capacity; i < participants.size(); i++) {
            list.add(participants.get(i));
        }
        return list;
    }

    public void addParticipant(Participant p) {
        if (!participants.contains(p)) {
            participants.add(p);
        }
    }

    public void removeParticipant(Participant p) {
        participants.remove(p);
    }
}
