package `in`.ramesh.zoology.earthwormlab.model

/**
 * All coordinates are fractions of the plate's intrinsic width/height in
 * [0f, 1f] — never phone pixels — so a hotspot survives zoom, pan, device
 * rotation and differing screen densities without re-authoring.
 *
 * The Java-native branches (native/v2.0.0-alpha1, alpha2-atlas) only ever
 * recorded a single (x, y) marker point per structure, not the original
 * SVG hit-region shape/size from the hybrid reference. [Point] preserves
 * that simplification as a legitimate, smaller case rather than forcing
 * every structure into an artificial single-point [Rect]; callers that
 * need a tappable area around a [Point] should apply a fixed minimum
 * touch-target radius at render time, not bake one into the data.
 */
sealed interface Hotspot {

    /** A single anchor point, e.g. a pin or label leader target. */
    data class Point(
        val xFraction: Float,
        val yFraction: Float,
    ) : Hotspot

    /** An axis-aligned region, e.g. ported from an SVG <rect>/<ellipse> bounding box. */
    data class Rect(
        val xFraction: Float,
        val yFraction: Float,
        val widthFraction: Float,
        val heightFraction: Float,
    ) : Hotspot

    /** An irregular region, e.g. ported from an SVG <path>. Points describe a
     * closed polygon in plate-fraction space, in order. */
    data class Polygon(
        val points: List<Pair<Float, Float>>,
    ) : Hotspot
}
