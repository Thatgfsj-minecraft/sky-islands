package dev.skyislands;

/**
 * The built-in sky island world types (1.7.10). Registered by constructing
 * the WorldType — vanilla keeps every instance in WorldType.WORLD_TYPES,
 * which the world creation screen cycles through and which the dedicated
 * server's level-type parsing (WorldType.parseWorldType) matches.
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

    public String typeName() {
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
