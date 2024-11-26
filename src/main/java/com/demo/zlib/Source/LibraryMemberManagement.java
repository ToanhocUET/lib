package com.demo.zlib.Source;

import java.util.*;

public class LibraryMemberManagement extends Library {
    private Scanner input = new Scanner(System.in);
    private Library library = new Library();
    private Map<Member, Integer> memberMap = new HashMap<>();
    private static final String header = String.format("%-10s %-20s %-20s %-20s%n", "Order", "ID", "Name", "Contact");

    public Map<Member, Integer> getMemberMap() {
        return memberMap;
    }

    public void setMemberMap(Map<Member, Integer> memberMap) {
        this.memberMap = memberMap;
    }

    // Lấy thành viên bằng ID
    public Member getMemberByID(int memberID) {
        for (Member member : library.members) {
            if (member.getMemberID() == memberID) {
                return member;
            }
        }
        return null;
    }

    // 2. Các phương thức liên quan đến thành viên
    // Thêm thành viên
    public boolean isExistingMember(int id) {
        return (getMemberByID(id) != null);
    }


    public void addMembers() {
        boolean flag = true;
        while (flag) {
            System.out.println("Please enter the information of the new member:");
            System.out.print("Member ID: ");
            while (true) {
                try {
                    int memberID = input.nextInt();
                    input.nextLine();
                    if (isExistingMember(memberID)) {
                        System.out.println("Member already exists!");
                        System.out.println("Action failed.");
                    } else {
                        System.out.print("Member Name: ");
                        String memberName = input.nextLine();
                        System.out.print("Contact Info: ");
                        String contactInfo = input.nextLine();
                        Member member = new Member(memberID, memberName, contactInfo);
                        library.addMember(member);
                    }
                    break;
                } catch (InputMismatchException e) {
                    System.out.println("Please enter an integer ID.");
                    input.nextLine();
                }
            }
            System.out.println("Do you want to add another member? (y/n)");
            String answer = input.nextLine();
            if (answer.equals("y")) {
                flag = true;
            } else {
                flag = false;
            }
        }
    }

    //In ra toàn bộ thành viên
    public void displayAllMembers() {
        if (library.members.isEmpty()) {
            System.out.println("No one care to our fucking library!");
        } else {
            int i = 1;
            System.out.println("There are " + library.getMembers().size() + " members in the library: ");
            System.out.print(header);
            for (Member member : library.members) {
                String order = String.format("%-10s ", i++);
                System.out.println(order + member.toString());
            }
        }
    }

    //Trả về danh sách thành viên theo tiêu chí
    public List<Member> searchMembersListByCriteria(String criteria, String value) {
        List<Member> result = new ArrayList<>();
        switch (criteria) {
            case "ID":
                int memberID = Integer.parseInt(value);
                Member member1 = getMemberByID(memberID);
                if (member1 != null) {
                    result.add(member1);
                }
                break;
            case "Name":
                if (library.members != null) {
                    for (Member member2 : library.members) {
                        if (member2.getName().toLowerCase().contains(value.toLowerCase())) {
                            result.add(member2);
                        }
                    }
                }
                break;
            case "ContactInfo":
                if (library.members != null) {
                    for (Member member3 : library.members) {
                        if (member3.getContactInfo().toLowerCase().contains(value.toLowerCase())) {
                            result.add(member3);
                        }
                    }
                }
                break;
        }
        return result;
    }

    //Tìm kiếm thành viên theo tiêu chí
    //Tìm kiếm theo ID
    public void searchMembersByID(int memberID) {
        List<Member> memberList = searchMembersListByCriteria("ID", String.valueOf(memberID));
        if (memberList.isEmpty()) {
            System.out.println("There is no member with this ID!");
        } else {
            System.out.println(header);
            String order = String.format("%-10s", 1);
            for (Member member : memberList)
                System.out.println(order + " " + member.toString());
        }
    }

    //Tìm kiếm theo tên
    public void searchMembersByName(String name) {
        List<Member> memberList = searchMembersListByCriteria("Name", name);
        if (memberList.isEmpty()) {
            System.out.println("There is no member with name contains: " + name);
        } else {
            int i = 1;
            System.out.println("There are " + memberList.size() + " members with name: " + name);
            System.out.println(header);
            for (Member member : memberList) {
                String order = String.format("%-10s", i);
                System.out.println(order + " " + member.toString());
                memberMap.put(member, i);
                i++;
            }
        }
    }

    //Tìm kiếm theo liên lạc
    public void searchMembersByContact(String contact) {
        List<Member> memberList = searchMembersListByCriteria("ContactInfo", contact);
        if (memberList.isEmpty()) {
            System.out.println("There is no member with contact contains: " + contact);
        } else {
            int i = 1;
            System.out.println("There are " + memberList.size() + " members with contact: " + contact);
            System.out.println(header);
            for (Member member : memberList) {
                String order = String.format("%-10s", i);
                System.out.println(order + " " + member.toString());
                memberMap.put(member, i);
                i++;
            }
        }
    }

    //Phương thức lấy thành viên theo số thứ tự
    public Member getMemberByOrder(Map<Member, Integer> memberMap, int order) {
        for (Map.Entry<Member, Integer> entry : memberMap.entrySet()) {
            if (entry.getValue() == order) {
                return entry.getKey();
            }
        }
        return null;
    }

    // Xóa thành viên
    public void removeMemberByID(int memberID) {
        Member memberToRemove = getMemberByID(memberID);
        if (memberToRemove != null) {
            library.removeMember(memberToRemove);
        } else {
            System.out.println("There is no member with this ID!");
        }
    }

    //Sửa đổi thông tin thành viên
    public void modifyMember(int ID) {
        Member memberToModify = getMemberByID(ID);
        if (memberToModify != null) {
            System.out.println(header);
            String order = String.format("%-10s", 1);
            System.out.println(order + " " + memberToModify.toString());
            System.out.println("Please fill new features for this member: ");
            System.out.print("Enter ID >> ");
            int memberID;
            while (true) {
                String id = input.nextLine();
                try {
                    memberID = Integer.parseInt(id);
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Please enter an integer ID.");
                    input.nextLine();
                }
            }
            System.out.print("Enter name >> ");
            String newName = input.nextLine();
            System.out.print("Enter contact info >> ");
            String newContactInfo = input.nextLine();

            library.updateMemberField(memberID, "Name", newName);
            memberToModify.setName(newName);
            library.updateMemberField(memberID, "Contact", newContactInfo);
            memberToModify.setContactInfo(newContactInfo);
            library.updateMemberField(memberID, "memberID", ID);
            memberToModify.setMemberID(memberID);
            System.out.println("This member has just updated! New information:");
            System.out.println(header);
            System.out.println(order + " " + memberToModify.toString());
        } else {
            System.out.println("There is no member with this ID!");
        }
    }
}
