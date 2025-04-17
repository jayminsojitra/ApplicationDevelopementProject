package com.jsojitra220201.ApplicatinProjecTest;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Participant extends RealmObject {

    @PrimaryKey
    private String id;

    @Required
    private String firstName;

    private String lastName;
    private String phoneNumber;
    private String membershipNumber;

    // ===== Getters & Setters =====
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getMembershipNumber() { return membershipNumber; }
    public void setMembershipNumber(String membershipNumber) { this.membershipNumber = membershipNumber; }

    @Override
    public String toString() {
        return firstName + " " + lastName + " (" + membershipNumber + ")";
    }
}
