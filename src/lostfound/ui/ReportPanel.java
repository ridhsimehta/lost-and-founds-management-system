package lostfound.ui;

import java.awt.*;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import lostfound.core.*;
import lostfound.db.Repository;

/** Module 5: returned items report. */
public class ReportPanel extends JPanel {
    private final Repository repo;
    private final JLabel status;
    private final JLabel total = new JLabel("Returned: 0");
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Claim #", "Item", "Category", "Owner", "Finder", "Proof given"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };

    public ReportPanel(Repository repo, JLabel status) {
        this.repo = repo;
        this.status = status;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        add(new JScrollPane(new JTable(model)), BorderLayout.CENTER);
        JPanel south = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(e -> refresh());
        south.add(refresh);
        south.add(total);
        add(south, BorderLayout.SOUTH);
    }

    public void refresh() {
        model.setRowCount(0);
        try {
            for (Claim c : repo.claims("APPROVED")) {
                Item l = repo.getItem(c.getLostId()), f = repo.getItem(c.getFoundId());
                User owner = repo.getUser(l.getUserId()), finder = repo.getUser(f.getUserId());
                model.addRow(new Object[]{c.getId(), l.getName(), l.getCategory(),
                        owner == null ? "?" : owner.getName(),
                        finder == null ? "?" : finder.getName(), c.getProof()});
            }
            total.setText("Returned: " + model.getRowCount());
        } catch (ItemNotFoundException | SQLException ex) {
            status.setText(" " + ex.getMessage());
        }
    }
}
