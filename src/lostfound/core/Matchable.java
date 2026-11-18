package lostfound.core;

/** Anything that can be scored against another item (0-100). */
public interface Matchable {
    int matchScore(Item other);
}
