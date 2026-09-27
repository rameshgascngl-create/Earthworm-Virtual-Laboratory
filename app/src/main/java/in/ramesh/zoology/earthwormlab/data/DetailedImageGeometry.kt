package `in`.ramesh.zoology.earthwormlab.data

import `in`.ramesh.zoology.earthwormlab.model.Hotspot

/**
 * Mode-specific touch anchors for the recovered 2048x1536 HQ WebP reference plates.
 *
 * IMPORTANT: this is additive geometry for the Detailed Image mode only.
 * It does not replace or mutate [StructureGeometry], whose 55 validated
 * interactive-atlas anchors remain authoritative for the SVG atlas.
 *
 * Coordinates are fractions of the HQ image's intrinsic 2048x1536 bounds.
 * They are intentionally isolated here so physical-device QA can adjust a
 * Detailed Image anchor without changing the original scientific dataset.
 */
internal object DetailedImageGeometry {
    private val points: Map<String, Pair<Float, Float>> = mapOf(
        // external — HQ plate contains separate dorsal and ventral surfaces
        "prostomium" to (.055f to .385f),
        "mouth" to (.040f to .725f),
        "spermathecal-pores" to (.315f to .735f),
        "dorsal-pores" to (.620f to .385f),
        "female-pore" to (.395f to .730f),
        "clitellum" to (.395f to .400f),
        "male-pores" to (.565f to .730f),
        "setae" to (.750f to .635f),
        "anus" to (.965f to .400f),

        // digestive
        "pharynx" to (.095f to .475f),
        "oesophagus" to (.200f to .495f),
        "gizzard" to (.305f to .475f),
        "stomach" to (.455f to .505f),
        "intestine" to (.835f to .495f),
        "intestinal-caeca" to (.605f to .455f),
        "typhlosole" to (.805f to .540f),

        // circulatory
        "dorsal-vessel" to (.915f to .325f),
        "supra-oesophageal-vessel" to (.555f to .305f),
        "lateral-hearts" to (.185f to .455f),
        "lateral-oesophageal-vessels" to (.285f to .575f),
        "ventral-vessel" to (.735f to .620f),
        "subneural-vessel" to (.785f to .700f),
        "capillary-networks" to (.745f to .425f),

        // respiratory
        "mucus-film" to (.305f to .380f),
        "moist-epidermis" to (.500f to .505f),
        "cutaneous-capillaries" to (.505f to .665f),
        "cutaneous-exchange" to (.565f to .375f),

        // excretory
        "pharyngeal-nephridia" to (.165f to .335f),
        "septal-nephridia" to (.665f to .310f),
        "integumentary-nephridia" to (.650f to .455f),
        "nephridium" to (.545f to .755f),

        // reproductive
        "spermathecae" to (.160f to .350f),
        "testes" to (.365f to .555f),
        "seminal-vesicles" to (.375f to .355f),
        "ovaries" to (.530f to .365f),
        "oviducts" to (.545f to .555f),
        "prostate-glands" to (.805f to .350f),
        "vasa-deferentia" to (.690f to .520f),

        // nervous
        "cerebral-ganglia" to (.140f to .400f),
        "circum-pharyngeal-connectives" to (.165f to .430f),
        "subpharyngeal-ganglion" to (.200f to .550f),
        "ventral-nerve-cord" to (.505f to .565f),
        "segmental-ganglia" to (.735f to .565f),

        // transverse section
        "cs-cuticle" to (.500f to .095f),
        "cs-epidermis" to (.500f to .155f),
        "cs-circular-muscle" to (.500f to .205f),
        "cs-longitudinal-muscle" to (.500f to .255f),
        "cs-peritoneum" to (.500f to .315f),
        "cs-coelom" to (.390f to .500f),
        "cs-gut" to (.500f to .485f),
        "cs-dorsal-vessel" to (.500f to .270f),
        "cs-ventral-vessel" to (.500f to .650f),
        "cs-nerve-cord" to (.500f to .700f),
        "cs-subneural-vessel" to (.500f to .745f),
        "cs-setae" to (.765f to .815f),
    )

    fun pointFor(id: String): Hotspot.Point? =
        points[id]?.let { (x, y) -> Hotspot.Point(x, y) }

    val expectedCount: Int = points.size
}
