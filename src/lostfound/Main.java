package lostfound;

import javax.swing.SwingUtilities;
import lostfound.db.Repository;
import lostfound.ui.MainFrame;

public class Main {
    public static void main(String[] args) throws Exception {
        Class.forName("org.sqlite.JDBC");
        Repository repo = new Repository("jdbc:sqlite:lostfound.db");
        SwingUtilities.invokeLater(() -> new MainFrame(repo).setVisible(true));
    }
}
