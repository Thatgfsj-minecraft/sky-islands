package dev.skyislands;

/**
 * The built-in sky island world types, mirroring the modern (1.21.1)
 * release. On Forge 1.16.5 the level-type id is the world type's registry
 * name under this mod's namespace ("skyislands:classic" etc.), parsed by
 * Forge's DimensionGeneratorSettings patch into ForgeRegistries.WORLD_TYPES.
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
     * Registry-name path under the mod namespace. The small island keeps its
     * original "oldschool" id so worlds created before the rename still
     * resolve their generator.
     */
    public String id() {
        return id;
    }

    /** level-type string this world type answers to on a dedicated server. */
    public String levelType() {
        return "skyislands:" + id;
    }
}
