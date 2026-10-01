package dev.skyislands;

/**
 * The built-in sky island world types, mirroring the modern (1.21.1) release.
 * Each island type is a distinct world type backed by this mod's void chunk
 * generator, so a running world can be mapped back to its island type from
 * the overworld generator alone.
 */
public enum IslandType {
    CLASSIC("classic"),
    SMALL("oldschool"),
    SINGLE("single");

    private final String id;

    IslandType(String id) {
        this.id = id;
    }

    /**
     * Preset id under the mod namespace. The small island keeps its original
     * "oldschool" id so worlds created before the rename still map back to
     * their generator.
     */
    public String id() {
        return id;
    }

    /** Level-type string this world type answers to on a dedicated server. */
    public String levelType() {
        return "skyislands:" + id;
    }

    /** Parses a server.properties level-type value; null when not ours. */
    public static IslandType fromLevelType(String levelType) {
        if (levelType == null) {
            return null;
        }
        String lowered = levelType.trim().toLowerCase(java.util.Locale.ROOT);
        for (IslandType type : values()) {
            if (lowered.equals(type.levelType())) {
                return type;
            }
        }
        return null;
    }
}
