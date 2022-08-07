package com.ygames.ysoccer.match;

import java.util.HashMap;
import java.util.Map;

public class Referee {

    Map<Player, PenaltyCard> penaltyCards;

    public enum PenaltyCard {YELLOW, RED, DOUBLE_YELLOW, YELLOW_PLUS_RED}

    public Referee() {
        penaltyCards = new HashMap<>();
    }

    public Map<Player, PenaltyCard> getPenaltyCards() {
        return penaltyCards;
    }

    void addYellowCard(Player player) {
        if (hasYellowCard(player)) {
            penaltyCards.put(player, PenaltyCard.DOUBLE_YELLOW);
        } else {
            penaltyCards.put(player, PenaltyCard.YELLOW);
        }
    }

    void addRedCard(Player player) {
        if (hasYellowCard(player)) {
            penaltyCards.put(player, PenaltyCard.YELLOW_PLUS_RED);
        } else {
            penaltyCards.put(player, PenaltyCard.RED);
        }
    }

    boolean hasYellowCard(Player player) {
        return penaltyCards.containsKey(player) && penaltyCards.get(player) == PenaltyCard.YELLOW;
    }

    boolean isSentOff(Player player) {
        return penaltyCards.containsKey(player) && penaltyCards.get(player) != PenaltyCard.YELLOW;
    }
}
