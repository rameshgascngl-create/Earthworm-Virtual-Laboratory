package `in`.ramesh.zoology.earthwormlab.ui.atlas

import `in`.ramesh.zoology.earthwormlab.model.EarthwormSystem
import `in`.ramesh.zoology.earthwormlab.preferences.AppLanguage

/**
 * Phase-3B3.1 physiological process definitions.
 *
 * The frozen scientific JSON, atlas SVGs, HQ plates and hotspot geometry are
 * not modified. Where one frozen hotspot represents the four heart pairs, the
 * physiology layer may reuse that hotspot for two separately labelled events:
 * lateral hearts (VII, IX) and latero-oesophageal hearts (XII, XIII).
 *
 * Visual direction cues are schematic teaching cues, not literal particle
 * tracks or traced vessel-wall geometry.
 */
internal enum class BiologicalProcess {
    CUTANEOUS_RESPIRATION,
    BLOOD_CIRCULATION,
}

internal enum class ProcessPhase {
    GAS_EXCHANGE,
    DISTRIBUTION,
    COLLECTION_NETWORK,
}

internal enum class ProcessDirection {
    INWARD,
    OUTWARD,
    BIDIRECTIONAL_EXCHANGE,
    ANTERIOR,
    DORSOVENTRAL_TRANSFER,
    POSTERIOR,
    DISTRIBUTION,
    COLLECTION,
}

internal data class ProcessStage(
    val target: String,
    val phase: ProcessPhase,
    val direction: ProcessDirection,
    val titleEn: String,
    val titleTa: String,
    val mechanismEn: String,
    val mechanismTa: String,
    val cueEn: String,
    val cueTa: String,
) {
    fun title(language: AppLanguage): String =
        if (language == AppLanguage.TAMIL) titleTa else titleEn

    fun mechanism(language: AppLanguage): String =
        if (language == AppLanguage.TAMIL) mechanismTa else mechanismEn

    fun cue(language: AppLanguage): String =
        if (language == AppLanguage.TAMIL) cueTa else cueEn
}

internal data class ProcessDefinition(
    val process: BiologicalProcess,
    val system: EarthwormSystem,
    val stages: List<ProcessStage>,
)

internal object ProcessGuidance {
    private val definitions = mapOf(
        EarthwormSystem.RESPIRATORY to ProcessDefinition(
            process = BiologicalProcess.CUTANEOUS_RESPIRATION,
            system = EarthwormSystem.RESPIRATORY,
            stages = listOf(
                ProcessStage(
                    target = "mucus-film",
                    phase = ProcessPhase.GAS_EXCHANGE,
                    direction = ProcessDirection.INWARD,
                    titleEn = "Moist surface film",
                    titleTa = "ஈரமான உடற்பரப்பு படலம்",
                    mechanismEn = "Moisture at the body surface allows respiratory gases to dissolve before diffusion across the integument.",
                    mechanismTa = "உடற்பரப்பின் ஈரப்பதம் சுவாச வாயுக்கள் முதலில் கரைய உதவுகிறது; அதன் பின் அவை உடற்சுவர் வழியாகப் பரவுகின்றன.",
                    cueEn = "O₂ dissolves at the moist surface",
                    cueTa = "O₂ ஈரப்பரப்பில் கரைகிறது",
                ),
                ProcessStage(
                    target = "moist-epidermis",
                    phase = ProcessPhase.GAS_EXCHANGE,
                    direction = ProcessDirection.BIDIRECTIONAL_EXCHANGE,
                    titleEn = "Moist epidermal diffusion barrier",
                    titleTa = "ஈரமான புறத்தோல் பரவல் தடுப்பு",
                    mechanismEn = "Oxygen diffuses inward and carbon dioxide diffuses outward down their respective partial-pressure gradients across the moist body wall.",
                    mechanismTa = "ஈரமான உடற்சுவர் வழியாக ஆக்சிஜன் உட்புறமும் கார்பன் டைஆக்சைடு வெளிப்புறமும் தத்தம் பகுதி அழுத்தச் சரிவுகளின்படி பரவுகின்றன.",
                    cueEn = "O₂ inward · CO₂ outward",
                    cueTa = "O₂ உள்ளே · CO₂ வெளியே",
                ),
                ProcessStage(
                    target = "cutaneous-exchange",
                    phase = ProcessPhase.GAS_EXCHANGE,
                    direction = ProcessDirection.BIDIRECTIONAL_EXCHANGE,
                    titleEn = "Cutaneous gas exchange",
                    titleTa = "தோல் வழி வாயுப் பரிமாற்றம்",
                    mechanismEn = "Gas exchange occurs across the moist cuticle–epidermis–capillary interface and is driven by diffusion gradients.",
                    mechanismTa = "ஈரமான கியூட்டிக்கிள்–புறத்தோல்–நுண்நாள இடைமுகத்தின் குறுக்காக பரவல் சரிவுகளால் வாயுப் பரிமாற்றம் நடைபெறுகிறது.",
                    cueEn = "Diffusion across the body wall",
                    cueTa = "உடற்சுவர் வழி பரவல்",
                ),
                ProcessStage(
                    target = "cutaneous-capillaries",
                    phase = ProcessPhase.GAS_EXCHANGE,
                    direction = ProcessDirection.DISTRIBUTION,
                    titleEn = "Subepidermal capillary transport",
                    titleTa = "புறத்தோலுக்குக் கீழுள்ள நுண்நாளக் கடத்தல்",
                    mechanismEn = "Subepidermal capillaries carry absorbed oxygen away from the skin and return carbon dioxide from tissues toward the respiratory surface.",
                    mechanismTa = "புறத்தோலுக்குக் கீழுள்ள நுண்நாளங்கள் உறிஞ்சப்பட்ட ஆக்சிஜனைத் தோலிலிருந்து உட்புறத் திசுக்களுக்கு எடுத்துச் செல்கின்றன; திசுக்களிலிருந்து கார்பன் டைஆக்சைடை மீண்டும் சுவாசப் பரப்பை நோக்கிக் கொண்டுவருகின்றன.",
                    cueEn = "Blood transports respiratory gases",
                    cueTa = "இரத்தம் சுவாச வாயுக்களை கடத்துகிறது",
                ),
            ),
        ),
        EarthwormSystem.CIRCULATORY to ProcessDefinition(
            process = BiologicalProcess.BLOOD_CIRCULATION,
            system = EarthwormSystem.CIRCULATORY,
            stages = listOf(
                ProcessStage(
                    target = "dorsal-vessel",
                    phase = ProcessPhase.DISTRIBUTION,
                    direction = ProcessDirection.ANTERIOR,
                    titleEn = "Dorsal vessel — anterior propulsion",
                    titleTa = "முதுகுப்புற நாளம் — முன்நோக்கிய செலுத்தல்",
                    mechanismEn = "The contractile dorsal vessel collects blood from segmental regions and propels it toward the anterior end.",
                    mechanismTa = "சுருங்கும் திறனுள்ள முதுகுப்புற இரத்த நாளம் கண்டப் பகுதிகளிலிருந்து இரத்தத்தைச் சேகரித்து முன்முனையை நோக்கி செலுத்துகிறது.",
                    cueEn = "Anterior flow",
                    cueTa = "முன்நோக்கிய ஓட்டம்",
                ),
                ProcessStage(
                    target = "lateral-hearts",
                    phase = ProcessPhase.DISTRIBUTION,
                    direction = ProcessDirection.DORSOVENTRAL_TRANSFER,
                    titleEn = "Lateral hearts — segments VII and IX",
                    titleTa = "பக்கவாட்டு இதயங்கள் — VII மற்றும் IX கண்டங்கள்",
                    mechanismEn = "The lateral hearts in segments VII and IX connect the dorsal and ventral vessels and transfer blood toward the ventral distributing vessel.",
                    mechanismTa = "VII மற்றும் IX கண்டங்களில் உள்ள பக்கவாட்டு இதயங்கள் முதுகுப்புற மற்றும் வயிற்றுப்புற நாளங்களை இணைத்து, இரத்தத்தை வயிற்றுப்புற விநியோக நாளத்தை நோக்கி மாற்றுகின்றன.",
                    cueEn = "Dorsal-to-ventral transfer",
                    cueTa = "முதுகுப்புறத்திலிருந்து வயிற்றுப்புறத்திற்கு மாற்றம்",
                ),
                ProcessStage(
                    target = "lateral-hearts",
                    phase = ProcessPhase.DISTRIBUTION,
                    direction = ProcessDirection.DORSOVENTRAL_TRANSFER,
                    titleEn = "Latero-oesophageal hearts — segments XII and XIII",
                    titleTa = "உணவுக்குழாய் பக்கவாட்டு இதயங்கள் — XII மற்றும் XIII கண்டங்கள்",
                    mechanismEn = "The latero-oesophageal hearts in segments XII and XIII connect the anterior dorsal/supra-oesophageal vascular network with the ventral vessel; they are not identical in vascular connections to the lateral hearts of VII and IX.",
                    mechanismTa = "XII மற்றும் XIII கண்டங்களில் உள்ள உணவுக்குழாய் பக்கவாட்டு இதயங்கள் முன்புற முதுகுப்புற/உணவுக்குழாய் மேல்நாள வலையமைப்பை வயிற்றுப்புற நாளத்துடன் இணைக்கின்றன; VII மற்றும் IX கண்டங்களின் பக்கவாட்டு இதயங்களுடன் இவற்றின் நாள இணைப்புகள் ஒரே மாதிரியானவை அல்ல.",
                    cueEn = "Anterior network to ventral vessel",
                    cueTa = "முன்புற நாள வலையிலிருந்து வயிற்றுப்புற நாளத்திற்கு",
                ),
                ProcessStage(
                    target = "ventral-vessel",
                    phase = ProcessPhase.DISTRIBUTION,
                    direction = ProcessDirection.POSTERIOR,
                    titleEn = "Ventral vessel — posterior distribution",
                    titleTa = "வயிற்றுப்புற நாளம் — பின்நோக்கிய விநியோகம்",
                    mechanismEn = "The ventral vessel distributes blood posteriorly through segmental branches to the body tissues.",
                    mechanismTa = "வயிற்றுப்புற இரத்த நாளம் கண்டக் கிளைகள் வழியாக இரத்தத்தை பின்புறம் நோக்கி உடல் திசுக்களுக்கு விநியோகிக்கிறது.",
                    cueEn = "Posterior distribution",
                    cueTa = "பின்நோக்கிய விநியோகம்",
                ),
                ProcessStage(
                    target = "capillary-networks",
                    phase = ProcessPhase.DISTRIBUTION,
                    direction = ProcessDirection.BIDIRECTIONAL_EXCHANGE,
                    titleEn = "Segmental capillary exchange",
                    titleTa = "கண்டவாரியான நுண்நாளப் பரிமாற்றம்",
                    mechanismEn = "Segmental capillary networks are exchange sites where gases, nutrients and metabolic wastes move between blood and tissues.",
                    mechanismTa = "கண்டவாரியான நுண்நாள வலையமைப்புகளில் இரத்தமும் திசுக்களும் இடையே வாயுக்கள், ஊட்டப்பொருள்கள் மற்றும் மாற்றச்செயல் கழிவுகள் பரிமாறுகின்றன.",
                    cueEn = "Tissue exchange",
                    cueTa = "திசு பரிமாற்றம்",
                ),
                ProcessStage(
                    target = "subneural-vessel",
                    phase = ProcessPhase.COLLECTION_NETWORK,
                    direction = ProcessDirection.COLLECTION,
                    titleEn = "Collecting network — subneural vessel",
                    titleTa = "சேகரிப்பு வலை — நரம்புவடக் கீழ்நாளம்",
                    mechanismEn = "The lesson now switches from distribution to collecting pathways. The subneural vessel collects from the ventral body-wall and nerve-cord region and communicates with the dorsal system through connecting vessels; it is not simply the next vessel in one linear loop.",
                    mechanismTa = "இங்கு கற்றல் வரிசை விநியோகப் பாதையிலிருந்து சேகரிப்பு பாதைகளுக்கு மாறுகிறது. நரம்புவடக் கீழ்நாளம் வயிற்றுப்புற உடற்சுவர் மற்றும் நரம்புவடப் பகுதிகளில் இருந்து இரத்தத்தைச் சேகரித்து இணை நாளங்கள் வழியாக முதுகுப்புற நாள அமைப்புடன் தொடர்பு கொள்கிறது; இது ஒரே நேர்கோட்டுச் சுழற்சியில் அடுத்த நாளம் எனக் கருதக்கூடாது.",
                    cueEn = "Ventral collection network",
                    cueTa = "வயிற்றுப்புறச் சேகரிப்பு வலை",
                ),
                ProcessStage(
                    target = "lateral-oesophageal-vessels",
                    phase = ProcessPhase.COLLECTION_NETWORK,
                    direction = ProcessDirection.COLLECTION,
                    titleEn = "Anterior collecting network — lateral-oesophageal vessels",
                    titleTa = "முன்புறச் சேகரிப்பு வலை — உணவுக்குழாய் பக்கவாட்டு நாளங்கள்",
                    mechanismEn = "The paired lateral-oesophageal vessels form part of an anterior collecting network and communicate with the supra-oesophageal system.",
                    mechanismTa = "இணையான உணவுக்குழாய் பக்கவாட்டு நாளங்கள் முன்புற இரத்தச் சேகரிப்பு வலையின் ஒரு பகுதியாக இருந்து உணவுக்குழாய் மேல்நாள அமைப்புடன் தொடர்பு கொள்கின்றன.",
                    cueEn = "Anterior collection",
                    cueTa = "முன்புறச் சேகரிப்பு",
                ),
                ProcessStage(
                    target = "supra-oesophageal-vessel",
                    phase = ProcessPhase.COLLECTION_NETWORK,
                    direction = ProcessDirection.COLLECTION,
                    titleEn = "Anterior vascular connection — supra-oesophageal vessel",
                    titleTa = "முன்புற நாள இணைப்பு — உணவுக்குழாய் மேல்நாளம்",
                    mechanismEn = "The supra-oesophageal vessel participates in the anterior collecting network and the vascular connections associated with the latero-oesophageal hearts.",
                    mechanismTa = "உணவுக்குழாய் மேல்நாளம் முன்புற இரத்தச் சேகரிப்பு வலையிலும் உணவுக்குழாய் பக்கவாட்டு இதயங்களுடன் தொடர்புடைய நாள இணைப்புகளிலும் பங்கேற்கிறது.",
                    cueEn = "Anterior vascular connection",
                    cueTa = "முன்புற நாள இணைப்பு",
                ),
            ),
        ),
    )

    fun forSystem(system: EarthwormSystem): ProcessDefinition? = definitions[system]
}
