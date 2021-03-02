package com.ygames.ysoccer.match;

import java.util.HashMap;
import java.util.Map;

public class Referee {

    Map<Player, PenaltyCard> penaltyCards;

    enum PenaltyCard {YELLOW, RED}

    public Referee() {
        penaltyCards = new HashMap<>();
    }

    void addYellowCard(Player player) {
        if (hasYellowCard(player)) {
            addRedCard(player);
        } else {
            penaltyCards.put(player, PenaltyCard.YELLOW);
        }
    }

    void addRedCard(Player player) {
        penaltyCards.put(player, PenaltyCard.RED);
    }

    boolean hasYellowCard(Player player) {
        return penaltyCards.containsKey(player) && penaltyCards.get(player) == PenaltyCard.YELLOW;
    }

    boolean hasRedCard(Player player) {
        return penaltyCards.containsKey(player) && penaltyCards.get(player) == PenaltyCard.RED;
    }
}
