package dev.skyislands;

/**
 * The built-in sky island world types (1.12.2 side). Registered by simply
 * constructing the WorldType — vanilla auto-adds every instance to
 * WorldType.WORLD_TYPES, which the world creation screen cycles through and
 * which the dedicated server's level-type parsing (WorldType.byName) matches.
 */
public enum IslandType {
    CLASSIC("sky_classic"),
    SMALL("sky_oldschool"),
    SINGLE("sky_single");

    private final String name;
    private final SkyIslandsWorldType worldType;

    IslandType(String name) {
        this.name = name;
        this.worldType = new SkyIslandsWorldType(this);
    }

    public String name_() {
        return name;
    }

    public SkyIslandsWorldType worldType() {
        return worldType;
    }

    /** Finds the island type for a world's terrain type; null when not ours. */
    public static IslandType fromWorldType(net.minecraft.world.WorldType type) {
        for (IslandType candidate : values()) {
            if (candidate.worldType == type) {
                return candidate;
            }
        }
        return null;
    }
}
