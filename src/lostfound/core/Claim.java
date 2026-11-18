package lostfound.core;

import java.util.Set;

public class Claim {
    private final int id, lostId, foundId, score;
    private final String proof, status;

    public Claim(int id, int lostId, int foundId, int score, String proof, String status) {
        this.id = id; this.lostId = lostId; this.foundId = foundId;
        this.score = score; this.proof = proof; this.status = status;
    }
    public int getId() { return id; }
    public int getLostId() { return lostId; }
    public int getFoundId() { return foundId; }
    public int getScore() { return score; }
    public String getProof() { return proof; }
    public String getStatus() { return status; }

    /** A claim is verified when the proof shares at least 2 words with the found item's description. */
    public static boolean verify(String proof, Item found) {
        if (proof == null || proof.trim().isEmpty()) return false;
        Set<String> d = Item.words(found.getDescription());
        int common = 0;
        for (String w : Item.words(proof)) if (d.contains(w)) common++;
        return common >= 2;
    }
}
