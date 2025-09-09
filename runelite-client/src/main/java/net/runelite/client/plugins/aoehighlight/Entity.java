package net.runelite.client.plugins.aoehighlight;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;


public enum Entity {
/*
 * Projectiles
 */
    /*
     * Enemies
     */
    LIZARDMAN_SHAMAN_AOE(1293, 0, 5, 1, null, true),
    DEMONIC_GORILLAS(856,0,1,1, null, true),
    /*
     * Bosses
     */
    ARAXXOR_POISON(2924, 0, 1, 1, null, true),
    VORKATH_BOMB(1481, 0, 3, 1, null, true),
    VORKATH_POISON_POOL(1483, 0, 1, 1, null, true),
    VORKATH_SPAWN(1484, 0, 1, 1, null, true),
    VORKATH_TICK_FIRE(1482, 0, 1, 1, null, true),
    GALVEK_MINE(1495, 0, 3, 1, null, true),
    GALVEK_BOMB(1491, 0, 3, 1, null, true),
    WINTERTODT_SNOW_FALL(501, 0, 3, 1, null, true),
    CORPOREAL_BEAST_SPERM(315, 0, 1, 1, null, true),
    CERBERUS_LAVA(1247, 0,1,1, null, true),
    CORPOREAL_BEAST_DARK_CORE(319, 0, 3, 1, null, true),
    GROTESQUE_FALLING_ROCK(1435, 0, 3, 1, null, true),
    GROTESQUE_FREEZE_BALL(1445, 0, 3, 1, null, true),
    ZULRAH_POISON(1045,0,3,1,null,true),
    ZULRAH_RANGED(1044, 0, 1, 1, null, true),
    /*
     * Raids
     */
    ICE_DEMON_RANGED_AOE(1324, 0, 3, 1, null, true),
    ICE_DEMON_ICE_BARRAGE_AOE(366, 0, 3, 1, null, true),
    VASA_AWAKEN_AOE(1327, 0, 3, 1, null, true),
    VASA_RANGED_AOE(1329, 0, 3, 1, null, true),
    TEKTON_METEOR_AOE(660, 0, 3, 1, null, true),
    OLM_FALLING_CRYSTAL(1357, 0, 3, 1, null, true),
    OLM_BURNING(-1, 0, 3, 1, null, true),
    OLM_POISON(1354, 0, 1, 1, null, true),
    OLM_CHOSEN_CRYSTAL(1352, 0, 1, 1, null, true),
    WARDENS_LIGHTNING_BOX(2225, 0, 7, 1, null, true),
    WARDENS_DIRT(2210, 0, 1, 1, null, true),
    KEPHRI_KAMIKAZE(2147, 0, 3, 1, null, true),
    KEPHRI_DUNG_BIG(2266, 0, 3, 1, null, true),
    ZEBAK_WATER(2173, 0, 1, 1, null, true),
    ZEBAK_POISON_MINI(1555, 0,1, 1, null, true),
    ZEBAK_POISON_BIG(2194,0,1, 1, null, true),
    BABA_SARC_DISCHARGE(2246,0,1, 1, null, true),
    VERZIK_P2_BOMB(1583, 0, 1, 1, null, true),
    VERZIK_SPIDERWEB(1601, 0, 1, 1, null, true),
    WARDEN_SKULLS(2226, 0, 1, 1, null, true),
    /*
     * Wilderness
     */
    CRAZY_ARCHAEOLOGIST_AOE(1260, 0, 3, 2, null, true),
    VETION_LIGHTNING_BLUE(2346,1200,3,2,null, true),
    VETION_LIGHTNING_ORANGE(2347,1200,3,2,null, true),
    CHAOS_FANATIC(551, 0, 1, 1, null, true),
    VENENATIS(2360,0,5,1,null,true),
/*
 * Graphics Objects
 */
    /*
     * Enemies
     */
    /*
     * Bosses
     */
    GROTESQUE_ENERGY_BEAMS(0, 5400, 3, 2, null, true),
    HYDRA_LIGHTNING(1666, 600, 1, 2, null, true),
    VARDORVIS(2510,1800,1,2,Color.BLUE, true),
    SIRE_MIASMA(1275,3600,1,2,null,true),
    MUSPAH_1(2335, 1200, 1, 2, null, true),
    MUSPAH_2(2324, 600, 1, 2, null, true),
    MUSPAH_3(2326,1200,1,2,null,true),
    SOL_BEAMS(0, 2400, 1, 2, null, false),
    SOL_SPLAT(2698, 1800, 1, 2, null, true),
    /*
     * Raids
     */
    KEPHRI_PUZZLE_ROCKS(317,3600,1,2,null,true),
    BABA_ROCKS(2250, 3600, 1, 2, Color.BLUE, true),
    BABA_MIDDLE_SHADOW(1448, 3000, 5, 2, null, true),
    BABA_OUTER_SHADOW(2111, 3000, 1, 2, null, true),
    BABA_ROCKS_FAST(2251, 2400, 1, 2, Color.BLUE, true),
    WARDENS_TILEFLIP(0, 1200, 1, 2, null, false),
    WARDENS_LIGHTNING(1446, 1200, 1, 2, null, true),
    MAIDEN_BLOOD(1579, 6000, 1, 2, Color.GREEN, true),
    BLOAT_FEET(0,1200, 1, 2, Color.YELLOW, true)

        ;
    /*
     * Wilderness
     */
    private int id;
    private final int lifeTime;
    private final int size;
    private final int type;
    private final Color color;
    private final boolean showOutline;
    private static final Map<Integer, Entity> Entities = new HashMap<>();

    Entity(int id, int lifeTime, int size, int type, Color color, boolean showOutline) {
        this.id = id;
        this.lifeTime = lifeTime;
        this.size = size;
        this.type = type;
        this.color = color;
        this.showOutline = showOutline;
    }

    public static Entity getEntity(int id) {
        return Entities.get(id);
    }

    public int getId() {
        return id;
    }
    public int getLifeTime() {
        return lifeTime;
    }

    public int getSize() {
        return size;
    }

    public int getType() {
        return type;
    }
    public Color getColor() {return color;}
    public boolean showOutline() {return showOutline;}

    private void assignIDs(int... Ids) {
        for (int id : Ids) {
            Entities.put(id, this);
        }
    }
    private void assignIDRange(int startId, int endId) {
        this.id = startId;
        for (int i = startId; i <= endId; i++) {
            Entities.put(i, this);
        }
    }

    static {
        // Define Multiple Non-Contiguous IDs
        GROTESQUE_ENERGY_BEAMS.assignIDs(1416, 1424);

        // Define Ranges of IDs
        WARDENS_TILEFLIP.assignIDRange(2220, 2223);
        BLOAT_FEET.assignIDRange(1570, 1573);
        SOL_BEAMS.assignIDRange(2689, 2692);

        // Populate List
        for (Entity entity : values()) {
            Entities.put(entity.id, entity);
        }
    }
}

