package lostfound.core;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public abstract class Item implements Matchable {
    private int id;
    private final String name, category, description, location;
    private final LocalDate date;
    private final int userId;
    private String status;

    protected Item(int id, String name, String category, String description,
                   String location, LocalDate date, int userId, String status) {
        this.id = id; this.name = name; this.category = category;
        this.description = description; this.location = location;
        this.date = date; this.userId = userId; this.status = status;
    }

    public abstract String getType();

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
    public LocalDate getDate() { return date; }
    public int getUserId() { return userId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    /** Splits text into lower-case words longer than 2 characters. */
    public static Set<String> words(String s) {
        Set<String> out = new HashSet<>();
        if (s != null)
            for (String w : s.toLowerCase().split("[^a-z0-9]+"))
                if (w.length() > 2) out.add(w);
        return out;
    }

    /** String comparison scoring: category 30, name 40/25, location 10, description words up to 20. */
    @Override
    public int matchScore(Item o) {
        int s = 0;
        if (category.equalsIgnoreCase(o.category)) s += 30;
        String a = name.toLowerCase(), b = o.name.toLowerCase();
        if (a.equals(b)) s += 40;
        else if (a.contains(b) || b.contains(a)) s += 25;
        if (location.equalsIgnoreCase(o.location)) s += 10;
        Set<String> wa = words(description), wb = words(o.description);
        int common = 0;
        for (String w : wa) if (wb.contains(w)) common++;
        s += Math.min(20, common * 5);
        return Math.min(s, 100);
    }

    @Override
    public String toString() { return name + " (" + category + ", " + location + ")"; }
}
