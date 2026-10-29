package gui;

import java.util.ArrayList;
import java.util.List;

public class TicketDataStore {
    // Hold a list of ticket records (each record is an array of Strings)
    private static final List<String[]> tickets = new ArrayList<>();

    // Add initial mock data matching your design
    static {
        tickets.add(new String[]{"TID-0001", "Oct 10, 2026, 10:47AM", "The HDMI Source is not reading", "Pending", "Other", "High", "The HDMI Source is not reading"});
    }

    public static List<String[]> getTickets() {
        return tickets;
    }

    public static void addTicket(String id, String date, String subject, String status, String category, String priority, String description) {
        tickets.add(new String[]{id, date, subject, status, category, priority, description});
    }
}

