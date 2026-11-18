package lostfound.core;

import java.time.LocalDate;

public class LostItem extends Item {
    public LostItem(int id, String name, String category, String description,
                    String location, LocalDate date, int userId, String status) {
        super(id, name, category, description, location, date, userId, status);
    }

    @Override
    public String getType() { return "LOST"; }
}
