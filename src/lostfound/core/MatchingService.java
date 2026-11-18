package lostfound.core;

import java.sql.SQLException;
import lostfound.db.Repository;

/** Runs in a background thread and records suggested matches as claims. */
public class MatchingService implements Runnable {
    public static final int THRESHOLD = 50;
    private final Repository repo;
    private final Runnable onNewMatch;
    private volatile boolean running = true;

    public MatchingService(Repository repo, Runnable onNewMatch) {
        this.repo = repo; this.onNewMatch = onNewMatch;
    }

    public void stop() { running = false; }

    @Override
    public void run() {
        while (running) {
            try {
                if (runOnce() > 0) onNewMatch.run();
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                return;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public int runOnce() throws SQLException {
        int created = 0;
        for (Item lost : repo.items("LOST", "OPEN"))
            for (Item found : repo.items("FOUND", "OPEN")) {
                int score = lost.matchScore(found);
                if (score >= THRESHOLD && !repo.claimExists(lost.getId(), found.getId())) {
                    repo.addClaim(lost.getId(), found.getId(), score);
                    created++;
                }
            }
        return created;
    }
}
