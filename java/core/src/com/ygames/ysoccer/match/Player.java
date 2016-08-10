package com.ygames.ysoccer.match;

public class Player {

    public enum Role {GOALKEEPER, RIGHT_BACK, LEFT_BACK, DEFENDER, RIGHT_WINGER, LEFT_WINGER, MIDFIELDER, ATTACKER}

    public static final String[] roleLabels = {
            "ROLES.GOALKEEPER",
            "ROLES.RIGHT_BACK",
            "ROLES.LEFT_BACK",
            "ROLES.DEFENDER",
            "ROLES.RIGHT_WINGER",
            "ROLES.LEFT_WINGER",
            "ROLES.MIDFIELDER",
            "ROLES.ATTACKER"
    };

    public String name;
    public String shirtName;
    public String nationality;
    public Role role;
    public int number;

    public int goals;

    public String getRoleLabel() {
        return roleLabels[role.ordinal()];
    }
}
