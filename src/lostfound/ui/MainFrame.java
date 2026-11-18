package lostfound.ui;

import java.awt.*;
import javax.swing.*;
import lostfound.core.MatchingService;
import lostfound.db.Repository;

public class MainFrame extends JFrame {
    public MainFrame(Repository repo) {
        super("Lost and Found Management System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 560);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Lost and Found Management System");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
        JPanel north = new JPanel(new FlowLayout(FlowLayout.LEFT));
        north.add(title);
        add(north, BorderLayout.NORTH);

        JLabel status = new JLabel(" Ready");
        add(status, BorderLayout.SOUTH);

        MatchesPanel matches = new MatchesPanel(repo, status);
        ReportPanel report = new ReportPanel(repo, status);
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("1. Lost item", new ItemFormPanel(repo, "LOST", status));
        tabs.addTab("2. Found item", new ItemFormPanel(repo, "FOUND", status));
        tabs.addTab("3-4. Matches and claims", matches);
        tabs.addTab("5. Returned report", report);
        tabs.addChangeListener(e -> { matches.refresh(); report.refresh(); });
        add(tabs, BorderLayout.CENTER);

        MatchingService matcher = new MatchingService(repo, () -> SwingUtilities.invokeLater(() -> {
            matches.refresh();
            status.setText(" Matcher found a new possible match");
        }));
        Thread t = new Thread(matcher, "matcher");
        t.setDaemon(true);
        t.start();
    }
}
