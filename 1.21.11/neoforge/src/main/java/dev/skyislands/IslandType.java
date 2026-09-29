package dev.skyislands;

/**
 * The built-in sky island world types. Each is a distinct world preset backed
 * by its own noise settings entry, so a running world can be mapped back to
 * its island type from the overworld generator's noise settings key alone.
 */
public enum IslandType {
    CLASSIC("classic"),
    OLDSCHOOL("oldschool"),
    ARCHIPELAGO("archipelago"),
    SINGLE("single");

    private final String id;

    IslandType(String id) {
        this.id = id;
    }

    /** Preset and noise settings path under the mod namespace. */
    public String id() {
        return id;
    }
}
