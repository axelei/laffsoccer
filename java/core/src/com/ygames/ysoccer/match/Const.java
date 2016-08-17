package com.ygames.ysoccer.match;

public class Const {

    public static int[][] goalsProbability = new int[][]{
            {1000, 0, 0, 0, 0, 0, 0},
            {870, 100, 25, 4, 1, 0, 0},
            {730, 210, 50, 7, 2, 1, 0},
            {510, 320, 140, 20, 6, 4, 0},
            {390, 370, 180, 40, 10, 7, 3},
            {220, 410, 190, 150, 15, 10, 5},
            {130, 390, 240, 200, 18, 15, 7},
            {40, 300, 380, 230, 25, 15, 10},
            {20, 220, 240, 220, 120, 100, 80},
            {10, 150, 190, 190, 170, 150, 140},
            {0, 100, 150, 200, 200, 200, 150}
    };

    public static String[] associations = new String[]{
            "AFG", "AIA", "ALB", "ALG", "AND", "ANG", "ARG", "ARM",
            "ARU", "ASA", "ATG", "AUS", "AUT", "AZE", "BAH", "BAN",
            "BDI", "BEL", "BEN", "BER", "BFA", "BHR", "BHU", "BIH",
            "BLR", "BLZ", "BOL", "BOT", "BRA", "BRB", "BRU", "BUL",
            "CAM", "CAN", "CAY", "CGO", "CHA", "CHI", "CHN", "CIV",
            "CMR", "COD", "COK", "COL", "COM", "CPV", "CRC", "CRO",
            "CTA", "CUB", "CUS", "CUW", "CYP", "CZE", "DEN", "DJI",
            "DMA", "DOM", "ECU", "EGY", "ENG", "EQG", "ERI", "ESP",
            "EST", "ETH", "FIJ", "FIN", "FRA", "FRO", "GAB", "GAM",
            "GEO", "GER", "GHA", "GNB", "GRE", "GRN", "GUA", "GUI",
            "GUM", "GUY", "HAI", "HKG", "HON", "HUN", "IDN", "IND",
            "IRL", "IRN", "IRQ", "ISL", "ISR", "ITA", "JAM", "JOR",
            "JPN", "KAZ", "KEN", "KGZ", "KOR", "KSA", "KUW", "LAO",
            "LBR", "LBY", "LCA", "LES", "LIB", "LIE", "LTU", "LUX",
            "LVA", "MAC", "MAD", "MAR", "MAS", "MDA", "MDV", "MEX",
            "MGL", "MKD", "MLI", "MLT", "MNE", "MOZ", "MRI", "MSR",
            "MTN", "MWI", "MYA", "NAM", "NCA", "NCL", "NED", "NEP",
            "NGA", "NIG", "NIR", "NOR", "NZL", "OMA", "PAK", "PAN",
            "PAR", "PER", "PHI", "PLE", "PNG", "POL", "POR", "PRK",
            "PUR", "QAT", "ROU", "RSA", "RUS", "RWA", "SAM", "SCO",
            "SDN", "SEN", "SEY", "SIN", "SKN", "SLE", "SLV", "SMR",
            "SOL", "SOM", "SRB", "SRI", "SSD", "STP", "SUI", "SUR",
            "SVK", "SVN", "SWE", "SWZ", "SYR", "TAH", "TAN", "TCA",
            "TGA", "THA", "TJK", "TKM", "TLS", "TOG", "TPE", "TRI",
            "TUN", "TUR", "UAE", "UGA", "UKR", "URU", "USA", "UZB",
            "VAN", "VEN", "VGB", "VIE", "VIN", "VIR", "WAL", "YEM",
            "ZAM", "ZIM"
    };

    // teams
    public static final int TEAM_SIZE = 11;
    public static final int BASE_TEAM = 16;
    public static final int FULL_TEAM = 32;

    // tactics
    static final int TACT_DX = 68;
    static final int TACT_DY = 40;
    static final int BALL_ZONES = 35;
}
