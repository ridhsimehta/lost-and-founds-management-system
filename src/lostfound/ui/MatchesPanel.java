package lostfound.ui;

import java.awt.*;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import lostfound.core.*;
import lostfound.db.Repository;

/** Module 3 (automatic matching results) and module 4 (claim verification). */
public class MatchesPanel extends JPanel {
    private final Repository repo;
    private final JLabel status;
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Claim #", "Lost item", "Lost by", "Found item", "Found at", "Score %"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);

    public MatchesPanel(Repository repo, JLabel status) {
        this.repo = repo;
        this.status = status;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        add(new JLabel("Suggested matches (found by the background matcher). Select one and verify the claim."),
                BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refresh = new JButton("Refresh"), verify = new JButton("Verify claim...");
        south.add(refresh);
        south.add(verify);
        add(south, BorderLayout.SOUTH);
        refresh.addActionListener(e -> refresh());
        verify.addActionListener(e -> verifySelected());
    }

    private String who(Item it) throws SQLException {
        User u = repo.getUser(it.getUserId());
        return u == null ? "?" : u.getName();
    }

    public void refresh() {
        model.setRowCount(0);
        try {
            for (Claim c : repo.claims("SUGGESTED")) {
                Item l = repo.getItem(c.getLostId()), f = repo.getItem(c.getFoundId());
                if (!"OPEN".equals(l.getStatus()) || !"OPEN".equals(f.getStatus())) continue;
                model.addRow(new Object[]{c.getId(), l.getName(), who(l), f.getName(), f.getLocation(), c.getScore()});
            }
        } catch (ItemNotFoundException | SQLException ex) {
            status.setText(" " + ex.getMessage());
        }
    }

    private void verifySelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a match first.");
            return;
        }
        int claimId = (Integer) model.getValueAt(row, 0);
        String proof = JOptionPane.showInputDialog(this,
                "Describe a unique detail of the item (colour, marks, contents):");
        if (proof == null) return;
        try {
            Claim claim = null;
            for (Claim c : repo.claims("SUGGESTED")) if (c.getId() == claimId) claim = c;
            if (claim == null) throw new ItemNotFoundException("Claim #" + claimId + " was not found");
            Item found = repo.getItem(claim.getFoundId());
            boolean ok = Claim.verify(proof, found);
            repo.updateClaim(claimId, proof, ok ? "APPROVED" : "REJECTED");
            if (ok) {
                repo.setItemStatus(claim.getLostId(), "RETURNED");
                repo.setItemStatus(claim.getFoundId(), "RETURNED");
            }
            status.setText(ok ? " Claim approved: item returned" : " Claim rejected: proof did not match");
            refresh();
        } catch (ItemNotFoundException | SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }
}
