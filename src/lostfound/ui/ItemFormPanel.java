package lostfound.ui;

import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import javax.swing.*;
import lostfound.core.*;
import lostfound.db.Repository;

/** Registration form used for both lost items (module 1) and found items (module 2). */
public class ItemFormPanel extends JPanel {
    private final JTextField name = new JTextField(), location = new JTextField(),
            date = new JTextField(LocalDate.now().toString()),
            owner = new JTextField(), contact = new JTextField();
    private final JComboBox<String> category = new JComboBox<>(
            new String[]{"Electronics", "Documents", "Bags", "Keys", "Clothing", "Other"});
    private final JTextArea description = new JTextArea(3, 20);

    public ItemFormPanel(Repository repo, String type, JLabel status) {
        boolean lost = type.equals("LOST");
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Item name"));            form.add(name);
        form.add(new JLabel("Category"));             form.add(category);
        form.add(new JLabel("Description"));          form.add(new JScrollPane(description));
        form.add(new JLabel(lost ? "Last seen at" : "Found at")); form.add(location);
        form.add(new JLabel("Date (yyyy-mm-dd)"));    form.add(date);
        form.add(new JLabel("Your name"));            form.add(owner);
        form.add(new JLabel("Contact (phone/email)")); form.add(contact);
        add(form, BorderLayout.NORTH);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton clear = new JButton("Clear");
        JButton save = new JButton(lost ? "Register lost item" : "Register found item");
        buttons.add(clear);
        buttons.add(save);
        add(buttons, BorderLayout.SOUTH);

        clear.addActionListener(e -> clearFields());
        save.addActionListener(e -> {
            if (name.getText().trim().isEmpty() || location.getText().trim().isEmpty()
                    || owner.getText().trim().isEmpty() || contact.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name, location, your name and contact are required.");
                return;
            }
            try {
                int uid = repo.saveUser(new User(0, owner.getText().trim(), contact.getText().trim()));
                LocalDate d = LocalDate.parse(date.getText().trim());
                String n = name.getText().trim(), c = (String) category.getSelectedItem();
                String desc = description.getText().trim(), loc = location.getText().trim();
                Item it = lost ? new LostItem(0, n, c, desc, loc, d, uid, "OPEN")
                               : new FoundItem(0, n, c, desc, loc, d, uid, "OPEN");
                repo.addItem(it);
                status.setText(" " + (lost ? "Lost" : "Found") + " item #" + it.getId() + " registered");
                clearFields();
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Use the date format yyyy-mm-dd.");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
            }
        });
    }

    private void clearFields() {
        name.setText(""); location.setText(""); description.setText("");
        owner.setText(""); contact.setText(""); date.setText(LocalDate.now().toString());
    }
}
