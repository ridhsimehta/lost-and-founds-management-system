package lostfound.core;

import java.time.LocalDate;

public class FoundItem extends Item {
    public FoundItem(int id, String name, String category, String description,
                    String location, LocalDate date, int userId, String status) {
        super(id, name, category, description, location, date, userId, status);
    }

    @Override
    public String getType() { return "FOUND"; }
}
