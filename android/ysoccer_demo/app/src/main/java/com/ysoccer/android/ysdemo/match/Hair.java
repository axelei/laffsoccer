package com.ysoccer.android.ysdemo.match;

public class Hair {

    static int[][][] map = {
            // row, column, (frmx,frmy,posx,posy)
            // 0
            {{0, 0, 15, 3}, {1, 0, 16, 3}, {2, 0, 14, 4},
                    {3, 0, 13, 3}, {4, 0, 11, 3}, {5, 0, 12, 1},
                    {6, 0, 12, 2}, {7, 0, 15, 1},

                    {0, 0, 15, 3}, {1, 0, 16, 3}, {2, 0, 14, 4},
                    {3, 0, 13, 3}, {4, 0, 11, 3}, {5, 0, 12, 1},
                    {6, 0, 12, 2}, {7, 0, 15, 1},},
            // 1
            {{0, 1, 15, 3}, {1, 1, 16, 3}, {2, 1, 14, 4},
                    {3, 1, 13, 3}, {4, 1, 11, 3}, {5, 1, 12, 1},
                    {6, 1, 12, 2}, {7, 1, 15, 1},

                    {0, 1, 15, 3}, {1, 1, 16, 3}, {2, 1, 14, 4},
                    {3, 1, 13, 3}, {4, 1, 11, 3}, {5, 1, 12, 1},
                    {6, 1, 12, 2}, {7, 1, 15, 1},},
            // 2
            {{0, 2, 15, 3}, {1, 2, 16, 3}, {2, 2, 14, 4},
                    {3, 2, 13, 3}, {4, 2, 11, 3}, {5, 2, 12, 1},
                    {6, 2, 12, 2}, {7, 2, 15, 1},

                    {0, 2, 15, 3}, {1, 2, 16, 3}, {2, 2, 14, 4},
                    {3, 2, 13, 3}, {4, 2, 11, 3}, {5, 2, 12, 1},
                    {6, 2, 12, 2}, {7, 2, 15, 1},},
            // 3
            {{0, 1, 14, 2}, {1, 1, 16, 2}, {2, 1, 14, 3},
                    {3, 1, 13, 2}, {4, 1, 11, 2}, {5, 1, 12, 1},
                    {6, 1, 12, 0}, {7, 1, 14, 0},

                    {0, 1, 14, 2}, {1, 1, 16, 2}, {2, 1, 14, 3},
                    {3, 1, 13, 2}, {4, 1, 11, 2}, {5, 1, 12, 1},
                    {6, 1, 12, 0}, {7, 1, 14, 0},},
            // 4
            {{0, 3, 8, 11}, {1, 3, 10, 9}, {2, 3, 14, 7},
                    {3, 3, 19, 10}, {4, 3, 20, 13}, {5, 3, 16, 14},
                    {6, 3, 13, 19}, {7, 3, 10, 16},

                    {0, 3, 8, 11}, {1, 3, 10, 9}, {2, 3, 14, 7},
                    {3, 3, 19, 10}, {4, 3, 20, 13}, {5, 3, 16, 14},
                    {6, 3, 13, 19}, {7, 3, 10, 16},},
            // 5
            {{0, 1, 15, 4}, {1, 1, 16, 4}, {2, 1, 14, 4},
                    {3, 1, 13, 4}, {4, 1, 11, 4}, {5, 1, 12, 3},
                    {6, 1, 12, 4}, {7, 1, 17, 4},

                    {0, 1, 15, 0}, {1, 1, 16, 0}, {2, 1, 14, 0},
                    {3, 1, 13, 0}, {4, 1, 11, 0}, {5, 1, 12, -1},
                    {6, 1, 12, 0}, {7, 1, 17, 0},},
            // 6
            {{0, 1, 20, 3}, {1, 1, 16, 2}, {2, 1, 12, 3},
                    {3, 1, 11, 3}, {4, 1, 8, 4}, {5, 1, 9, 1},
                    {6, 1, 11, 2}, {7, 1, 21, 1},

                    {0, 1, 20, 3}, {1, 1, 16, 2}, {2, 1, 12, 3},
                    {3, 1, 11, 3}, {4, 1, 8, 4}, {5, 1, 9, 1},
                    {6, 1, 11, 2}, {7, 1, 21, 1},},
            // 7
            {{0, 4, 20, 11}, {1, 4, 17, 19}, {2, 4, 15, 20},
                    {3, 4, 11, 19}, {4, 4, 7, 12}, {5, 4, 7, 6},
                    {6, 4, 13, 4}, {7, 4, 20, 6},

                    {0, 4, 20, 14}, {1, 4, 17, 22}, {2, 4, 15, 23},
                    {3, 4, 11, 22}, {4, 4, 7, 15}, {5, 4, 7, 9},
                    {6, 4, 13, 7}, {7, 4, 20, 11},},
            // 8
            {{0, 8, 16, 2}, {1, 8, 17, 2}, {2, 8, 15, 3},
                    {3, 8, 14, 2}, {4, 8, 12, 2}, {5, 8, 13, 1},
                    {6, 8, 13, 2}, {7, 8, 17, 1},

                    {0, 5, 18, 4}, {1, 5, 12, 4}, {2, 5, 18, 4},
                    {3, 5, 13, 4}, {0, 5, 18, 4}, {1, 5, 12, 4},
                    {2, 5, 21, 3}, {3, 5, 10, 3},},
            // 9
            {{0, 9, 15, 2}, {1, 9, 18, 2}, {2, 9, 15, 4},
                    {3, 9, 13, 3}, {4, 9, 11, 2}, {5, 9, 12, 0},
                    {6, 9, 13, 2}, {7, 9, 17, 0},

                    {0, 5, 20, 3}, {1, 5, 11, 3}, {2, 5, 20, 2},
                    {3, 5, 11, 2}, {0, 5, 20, 7}, {1, 5, 11, 7},
                    {2, 5, 22, 6}, {3, 5, 9, 6},},
            // 10
            {{0, 1, 14, 6}, {1, 1, 13, 6}, {2, 1, 15, 7},
                    {3, 1, 12, 5}, {4, 1, 12, 5}, {5, 1, 12, 4},
                    {6, 1, 13, 5}, {7, 1, 18, 4},

                    {0, 5, 18, -3}, {1, 5, 13, -3}, {2, 5, 20, -4},
                    {3, 5, 11, -4}, {0, 1, 0, 0}, {1, 1, 0, 0},
                    {2, 6, 23, 4}, {3, 6, 8, 4},},
            // 11
            {{0, 1, 13, 4}, {1, 1, 13, 4}, {2, 1, 0, 0},
                    {3, 1, 12, 4}, {4, 1, 11, 4}, {5, 1, 12, 3},
                    {6, 1, 14, 3}, {7, 1, 17, 3},

                    {0, 6, 19, 8}, {1, 6, 12, 8}, {2, 5, 22, 7},
                    {3, 5, 9, 7}, {4, 1, 0, 0}, {5, 1, 0, 0},
                    {2, 6, 23, 8}, {3, 6, 8, 8},},
            // 12
            {{0, 1, 13, 4}, {1, 1, 13, 4}, {2, 1, 0, 0},
                    {3, 1, 12, 4}, {4, 1, 11, 4}, {5, 1, 12, 3},
                    {6, 1, 14, 3}, {7, 1, 17, 3},

                    {0, 7, 23, 17}, {1, 7, 8, 17}, {2, 7, 24, 17},
                    {3, 7, 7, 17}, {0, 7, 0, 0}, {1, 7, 0, 0},
                    {2, 7, 22, 20}, {3, 7, 9, 20},},
            // 13
            {{0, 1, 13, 4}, {1, 1, 13, 4}, {2, 1, 0, 0},
                    {3, 1, 12, 4}, {4, 1, 11, 4}, {5, 1, 12, 3},
                    {6, 1, 14, 3}, {7, 1, 17, 3},

                    {4, 5, 21, 7}, {5, 5, 7, 7}, {2, 6, 23, 5},
                    {3, 6, 6, 5}, {0, 7, 23, 19}, {1, 7, 8, 19},
                    {2, 7, 25, 20}, {3, 7, 6, 20},},
            // 14
            {{2, 1, 14, 4}, {2, 1, 14, 7}, {2, 1, 14, 7},
                    {2, 1, 13, 10}, {4, 1, 0, 0}, {5, 1, 0, 0},
                    {6, 1, 0, 0}, {7, 1, 0, 0},

                    {0, 6, 0, 0}, {1, 6, 0, 0}, {2, 6, 23, 2},
                    {3, 6, 6, 2}, {0, 7, 0, 0}, {1, 7, 0, 0},
                    {2, 7, 21, 20}, {3, 7, 10, 19},},
            // 15
            {{2, 1, 14, 6}, {2, 1, 14, 3}, {2, 1, 14, 5},
                    {6, 1, 13, 7}, {4, 1, 0, 0}, {5, 1, 0, 0},
                    {6, 1, 0, 0}, {7, 1, 0, 0},

                    {0, 6, 0, 0}, {1, 6, 0, 0}, {2, 6, 23, 5},
                    {3, 6, 6, 5}, {4, 1, 0, 0}, {5, 1, 0, 0},
                    {6, 1, 0, 0}, {7, 1, 0, 0},},
            // 16
            {{0, 1, 0, 0}, {1, 1, 0, 0}, {2, 1, 0, 0}, {3, 1, 0, 0},
                    {4, 1, 0, 0}, {5, 1, 0, 0}, {6, 1, 0, 0},
                    {7, 1, 0, 0},

                    {2, 0, 15, 1}, {6, 0, 12, -1}, {2, 1, 12, 4},
                    {3, 1, 0, 0}, {4, 1, 0, 0}, {5, 1, 0, 0},
                    {6, 1, 0, 0}, {7, 1, 0, 0},},
            // 17
            {{0, 1, 0, 0}, {1, 1, 0, 0}, {2, 1, 0, 0}, {3, 1, 0, 0},
                    {4, 1, 0, 0}, {5, 1, 0, 0}, {6, 1, 0, 0},
                    {7, 1, 0, 0},

                    {6, 6, 16, -1}, {7, 6, 13, -2}, {2, 1, 0, 0},
                    {3, 1, 0, 0}, {4, 1, 0, 0}, {5, 1, 0, 0},
                    {6, 1, 0, 0}, {7, 1, 0, 0},},
            // 18
            {{0, 1, 0, 0}, {1, 1, 0, 0}, {2, 1, 0, 0}, {3, 1, 0, 0},
                    {4, 1, 0, 0}, {5, 1, 0, 0}, {6, 1, 0, 0},
                    {7, 1, 0, 0},

                    {6, 7, 16, -1}, {7, 7, 12, -2}, {2, 1, 0, 0},
                    {3, 1, 0, 0}, {4, 1, 0, 0}, {5, 1, 0, 0},
                    {6, 1, 0, 0}, {7, 1, 0, 0},},
            // 19
            {{0, 1, 0, 0}, {1, 1, 0, 0}, {2, 1, 0, 0}, {3, 1, 0, 0},
                    {4, 1, 0, 0}, {5, 1, 0, 0}, {6, 1, 0, 0},
                    {7, 1, 0, 0},

                    {0, 1, 0, 0}, {1, 1, 0, 0}, {2, 1, 0, 0},
                    {3, 1, 0, 0}, {4, 1, 0, 0}, {5, 1, 0, 0},
                    {6, 1, 0, 0}, {7, 1, 0, 0},}};

    static int[] codes = {
            // 1 - BALD
            101, // pelato (Collina)
            102, // pelato con pizzetto (Chimenti, Veron)

            // 2 - SHAVED
            201, // rasato (Ronaldo, Bojinov, Seedorf)

            // 3 - THINNING
            301, // calvizia estesa (Ballotta, Lombardo..)
            302, // chierica (Zidane, Di Canio)
            303, // stempiato (Corini)

            // 4 - SHORT
            401, // taglio medio (Giuly)
            402, // dritti (Harte, Amantino)
            403, // spazzola (Manfredini)

            // 5 - SMOOTH
            501, // folti (Cocu) ---> ATTENZIONE, QUESTO CORRISPONDE AL TAGLIO
            // STANDARD DI SWOS
            502, // riga in mezzo (Deco)
            503, // riga in mezzo con sfumatura posteriore (Rothen, Materazzi)
            504, // indietro (O//Shea, Kanchelskis 1996)
            505, // allungati all//indietro (Toni)
            506, // allungati con ciocche (Heinze)
            507, // allungati con elastico (Totti, Baros, Van Der Meyde,
            // Ibrahimovic..)
            508, // lunghi pettinati (Batistuta)
            509, // lunghi con elastico (Caniggia)
            510, // lunghi con elastico nero (Ujfalusi)
            511, // lunghi con elastico colorato (..)
            512, // raccolti all//indietro con codino (Savage, Prso)
            513, // raccolti con codino alto (Camoranesi)
            514, // corti con cerchietto (Frey)
            515, // lunghi con cerchietto (Rossini, Vieri..)
            516, // gonfi (Nedved)

            // 6 - CURLY
            601, // medi (Cisse')
            602, // allungati (Oliveira)
            603, // lunghi (Crespo)
            604, // lunghi con elastico (Ortega)
            605, // lunghi con cerchietto (?)
            606, // raccolti con codino (Ronaldinho)

            // 7 - PIGTAIL
            701, // treccine corte su rasato
            702, // treccioni su rasato
            703, // trecce lunghe (Rio Ferdinand)

            // 8 - SPECIAL
            801, // cresta (Beckham)
            802, // nazi
            803, // mohicano
            804, // West style
            805, // Davids style
            806, // barbone (Tommasi, Xavier)
            807, // Valderrama style
            808, // Divin Codino

    };

    static int type(int code) {
        for (int i = 0; i < codes.length; i++) {
            if (codes[i] == code) {
                return i;
            }
        }
        return 0;
    }

    static int[][] colors = {{0x2A2A2A, 0x1A1A1A, 0x090909}, // 0 - black
            {0xA2A022, 0x81801A, 0x605F11}, // 1 - blond
            {0xA26422, 0x7B4C1A, 0x543411}, // 2 - brown
            {0xE48B00, 0xB26D01, 0x7F4E01}, // 3 - red
            {0xFFFD7E, 0xE5E33F, 0xCAC800}, // 4 - platinum
            {0x7A7A7A, 0x4E4E4E, 0x222222}, // 5 - gray
            {0xD5D5D5, 0xADADAD, 0x848484}, // 6 - white
            {0xFF00A8, 0x722F2F, 0x421A1A}, // 7 - punk1
            {0xFDFB35, 0x808202, 0x595A05}, // 8 - punk2
    };

    static int[][] shavedColor = {{0x000000, 0x000000}, // 0
            {0xA1785F, 0x7D5D4A}, // 1
            {0xBFC768, 0x999F54}, // 2
            {0xC79341, 0x94713B}, // 3
            {0xD6BE97, 0xAB997C}, // 4
            {0x916847, 0x765331}, // 5
            {0x423225, 0x312418}, // 6
    };

    static int[][] shavedTable = {
            // SKIN: pink,black,pale,asiat,arab,mulat,red,free,alien,yoda
            {1, 6, 1, 1, 1, 5, 1, 1, 1, 1}, // 0 - black
            {2, 2, 2, 2, 2, 2, 2, 2, 2, 2}, // 1 - blond
            {5, 6, 5, 5, 5, 6, 5, 5, 5, 5}, // 2 - brown
            {3, 6, 3, 3, 1, 5, 3, 3, 3, 3}, // 3 - red
            {2, 6, 2, 2, 2, 5, 2, 2, 2, 2}, // 4 - platinum
            {1, 6, 1, 1, 1, 5, 1, 1, 1, 1}, // 5 - gray
            {4, 1, 4, 4, 4, 1, 4, 4, 4, 4}, // 6 - white
            {2, 2, 2, 2, 2, 2, 2, 2, 2, 2}, // 7 - punk1
            {5, 6, 5, 5, 5, 6, 5, 5, 5, 5}, // 8 - punk2
    };
};
