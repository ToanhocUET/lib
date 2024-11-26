package com.demo.zlib.Source;

import java.util.ArrayList;
import java.util.List;

public class Member {
    private int memberID;
    private String name;
    private String contactInfo;

    public Member(int memberID, String name, String contactInfo) {
        this.memberID = memberID;
        this.name = name;
        this.contactInfo = contactInfo;
    }

    public int getMemberID() {
        return memberID;
    }

    public void setMemberID(int memberID) {
        this.memberID = memberID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    @Override
    public String toString() {
        return String.format("%-20s %-20s %-20s", this.getMemberID(), this.getName(), this.getContactInfo());
    }
}
