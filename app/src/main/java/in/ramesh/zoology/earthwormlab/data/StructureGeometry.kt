package `in`.ramesh.zoology.earthwormlab.data

import `in`.ramesh.zoology.earthwormlab.model.EarthwormSystem
import `in`.ramesh.zoology.earthwormlab.model.Hotspot

/**
 * Ported verbatim (values unchanged) from NativeData.java in
 * native/v2.0.0-alpha1 / native/v2.0.0-alpha2-atlas. All 55 (id, x, y)
 * triples were compared 1:1 against the "data-structure" ids and system
 * groupings extracted directly from the frozen hybrid reference
 * (app/src/main/assets/index.html, sha256 4921509f…) — every id matched,
 * with no additions, omissions, or system reassignments.
 *
 * These are single marker points, not the original SVG hit-region
 * shapes/sizes (see Hotspot.kt doc comment) — restoring true region
 * geometry from the hybrid SVG markup is Phase 2 (atlas migration), not
 * Phase 1 scope.
 */
internal object StructureGeometry {
    private val points: Map<String, Pair<Float, Float>> = mapOf(
        // external
        "prostomium" to (.08f to .50f),
        "mouth" to (.105f to .60f),
        "spermathecal-pores" to (.24f to .39f),
        "dorsal-pores" to (.35f to .33f),
        "female-pore" to (.42f to .62f),
        "clitellum" to (.46f to .50f),
        "male-pores" to (.56f to .62f),
        "setae" to (.69f to .66f),
        "anus" to (.92f to .50f),
        // digestive
        "pharynx" to (.14f to .50f),
        "oesophagus" to (.25f to .50f),
        "gizzard" to (.34f to .50f),
        "stomach" to (.43f to .50f),
        "intestine" to (.65f to .50f),
        "intestinal-caeca" to (.58f to .38f),
        "typhlosole" to (.73f to .48f),
        // circulatory
        "dorsal-vessel" to (.58f to .38f),
        "supra-oesophageal-vessel" to (.24f to .32f),
        "lateral-hearts" to (.34f to .50f),
        "lateral-oesophageal-vessels" to (.24f to .68f),
        "ventral-vessel" to (.58f to .61f),
        "subneural-vessel" to (.67f to .72f),
        "capillary-networks" to (.80f to .50f),
        // respiratory
        "mucus-film" to (.25f to .31f),
        "moist-epidermis" to (.40f to .40f),
        "cutaneous-capillaries" to (.60f to .58f),
        "cutaneous-exchange" to (.78f to .36f),
        // excretory
        "pharyngeal-nephridia" to (.22f to .43f),
        "septal-nephridia" to (.53f to .43f),
        "integumentary-nephridia" to (.70f to .62f),
        "nephridium" to (.84f to .45f),
        // reproductive
        "spermathecae" to (.27f to .60f),
        "testes" to (.42f to .45f),
        "seminal-vesicles" to (.49f to .39f),
        "ovaries" to (.57f to .55f),
        "oviducts" to (.61f to .50f),
        "prostate-glands" to (.70f to .40f),
        "vasa-deferentia" to (.74f to .55f),
        // nervous
        "cerebral-ganglia" to (.18f to .37f),
        "circum-pharyngeal-connectives" to (.23f to .48f),
        "subpharyngeal-ganglion" to (.27f to .58f),
        "ventral-nerve-cord" to (.61f to .62f),
        "segmental-ganglia" to (.77f to .62f),
        // crosssection
        "cs-cuticle" to (.50f to .15f),
        "cs-epidermis" to (.62f to .20f),
        "cs-circular-muscle" to (.72f to .29f),
        "cs-longitudinal-muscle" to (.77f to .43f),
        "cs-peritoneum" to (.74f to .57f),
        "cs-coelom" to (.63f to .68f),
        "cs-gut" to (.50f to .52f),
        "cs-dorsal-vessel" to (.50f to .34f),
        "cs-ventral-vessel" to (.50f to .68f),
        "cs-nerve-cord" to (.50f to .77f),
        "cs-subneural-vessel" to (.50f to .84f),
        "cs-setae" to (.25f to .62f),
    )

    fun pointFor(id: String): Hotspot.Point? =
        points[id]?.let { (x, y) -> Hotspot.Point(x, y) }

    /** 55 in the frozen reference; assert this at startup in debug builds
     * if the bundled JSON's structure count ever drifts from this map. */
    val expectedCount: Int = points.size
}
