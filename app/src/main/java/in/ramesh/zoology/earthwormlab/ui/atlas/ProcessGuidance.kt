package `in`.ramesh.zoology.earthwormlab.ui.atlas

import `in`.ramesh.zoology.earthwormlab.model.EarthwormSystem
import `in`.ramesh.zoology.earthwormlab.preferences.AppLanguage

/**
 * Phase-3B3 physiological process engine.
 *
 * Scientific anatomy remains in the frozen repository JSON. This file only
 * supplies process orchestration and concise mechanism statements for the two
 * first validated physiology modules.
 *
 * Visual cues are explicitly schematic. They indicate physiological direction
 * or exchange at the active structure; they are not traced vessel-wall or
 * molecular trajectories.
 */
internal enum class BiologicalProcess {
    CUTANEOUS_RESPIRATION,
    BLOOD_CIRCULATION,
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
    val direction: ProcessDirection,
    val mechanismEn: String,
    val mechanismTa: String,
    val cueEn: String,
    val cueTa: String,
) {
    fun mechanism(language: AppLanguage): String =
        if (language == AppLanguage.TAMIL) mechanismTa else mechanismEn

    fun cue(language: AppLanguage): String =
        if (language == AppLanguage.TAMIL) cueTa else cueEn
}

internal data class ProcessDefinition(
    val process: BiologicalProcess,
    val system: EarthwormSystem,
    val stages: List<ProcessStage>,
) {
    val targets: List<String>
        get() = stages.map { it.target }
}

internal object ProcessGuidance {
    private val definitions = mapOf(
        EarthwormSystem.RESPIRATORY to ProcessDefinition(
            process = BiologicalProcess.CUTANEOUS_RESPIRATION,
            system = EarthwormSystem.RESPIRATORY,
            stages = listOf(
                ProcessStage(
                    target = "mucus-film",
                    direction = ProcessDirection.INWARD,
                    mechanismEn = "A moist surface allows respiratory gases to dissolve before diffusion across the body wall.",
                    mechanismTa = "ஈரமான உடற்பரப்பில் சுவாச வாயுக்கள் முதலில் கரைந்து, பின்னர் உடற்சுவர் வழியாகப் பரவுகின்றன.",
                    cueEn = "O₂ dissolves at moist surface",
                    cueTa = "O₂ ஈரப்பரப்பில் கரைகிறது",
                ),
                ProcessStage(
                    target = "moist-epidermis",
                    direction = ProcessDirection.BIDIRECTIONAL_EXCHANGE,
                    mechanismEn = "Across the moist epidermal barrier, oxygen diffuses inward while carbon dioxide diffuses outward down their respective gradients.",
                    mechanismTa = "ஈரமான புறத்தோல் தடுப்பின் வழியாக ஆக்சிஜன் உட்புறம் பரவுகிறது; கார்பன் டைஆக்சைடு அதற்குரிய சரிவைப் பின்பற்றி வெளியே பரவுகிறது.",
                    cueEn = "O₂ inward · CO₂ outward",
                    cueTa = "O₂ உள்ளே · CO₂ வெளியே",
                ),
                ProcessStage(
                    target = "cutaneous-exchange",
                    direction = ProcessDirection.BIDIRECTIONAL_EXCHANGE,
                    mechanismEn = "Gas exchange occurs across the moist cuticle–epidermis–capillary barrier and is driven by diffusion gradients.",
                    mechanismTa = "ஈரமான கியூட்டிக்கிள்–புறத்தோல்–நுண்நாளத் தடுப்பின் குறுக்காக பரவல் சரிவுகளால் வாயுப் பரிமாற்றம் நடைபெறுகிறது.",
                    cueEn = "Diffusion across body wall",
                    cueTa = "உடற்சுவர் வழி பரவல்",
                ),
                ProcessStage(
                    target = "cutaneous-capillaries",
                    direction = ProcessDirection.DISTRIBUTION,
                    mechanismEn = "Subepidermal capillaries carry absorbed oxygen away from the skin and bring carbon dioxide from tissues back toward the respiratory surface.",
                    mechanismTa = "புறத்தோலுக்குக் கீழுள்ள நுண்நாளங்கள் உறிஞ்சப்பட்ட ஆக்சிஜனை உட்புறத் திசுக்களுக்கு எடுத்துச் செல்கின்றன; திசுக்களில் இருந்து கார்பன் டைஆக்சைடை மீண்டும் தோல் மேற்பரப்பை நோக்கிக் கொண்டுவருகின்றன.",
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
                    direction = ProcessDirection.ANTERIOR,
                    mechanismEn = "The contractile dorsal vessel collects blood from segmental regions and propels it toward the anterior end.",
                    mechanismTa = "சுருங்கும் திறனுள்ள முதுகுப்புற இரத்த நாளம் கண்டப் பகுதிகளிலிருந்து இரத்தத்தைச் சேகரித்து முன்முனையை நோக்கி செலுத்துகிறது.",
                    cueEn = "Anterior flow",
                    cueTa = "முன்நோக்கிய ஓட்டம்",
                ),
                ProcessStage(
                    target = "lateral-hearts",
                    direction = ProcessDirection.DORSOVENTRAL_TRANSFER,
                    mechanismEn = "The heart arches maintain pressure and transfer blood between the major longitudinal vessels toward the ventral distribution pathway.",
                    mechanismTa = "இதய வளைவுகள் அழுத்தத்தைப் பேணி, முக்கிய நீள்நாளங்களுக்கு இடையில் இரத்தத்தை மாற்றி வயிற்றுப்புற விநியோகப் பாதைக்கு செலுத்துகின்றன.",
                    cueEn = "Transfer to ventral pathway",
                    cueTa = "வயிற்றுப்புறப் பாதைக்கு மாற்றம்",
                ),
                ProcessStage(
                    target = "ventral-vessel",
                    direction = ProcessDirection.POSTERIOR,
                    mechanismEn = "The ventral vessel distributes blood posteriorly through segmental branches to the body tissues.",
                    mechanismTa = "வயிற்றுப்புற இரத்த நாளம் கண்டக் கிளைகள் வழியாக இரத்தத்தை பின்புறம் நோக்கி உடல் திசுக்களுக்கு விநியோகிக்கிறது.",
                    cueEn = "Posterior distribution",
                    cueTa = "பின்நோக்கிய விநியோகம்",
                ),
                ProcessStage(
                    target = "capillary-networks",
                    direction = ProcessDirection.BIDIRECTIONAL_EXCHANGE,
                    mechanismEn = "Segmental capillary networks are the exchange sites where gases, nutrients and metabolic wastes move between blood and tissues.",
                    mechanismTa = "கண்டவாரியான நுண்நாள வலையமைப்புகளில் இரத்தமும் திசுக்களும் இடையே வாயுக்கள், ஊட்டப்பொருள்கள் மற்றும் மாற்றச்செயல் கழிவுகள் பரிமாறுகின்றன.",
                    cueEn = "Tissue exchange",
                    cueTa = "திசு பரிமாற்றம்",
                ),
                ProcessStage(
                    target = "subneural-vessel",
                    direction = ProcessDirection.COLLECTION,
                    mechanismEn = "The subneural vessel contributes to collection from the ventral body-wall and nerve-cord region and communicates back toward the dorsal system through connecting vessels.",
                    mechanismTa = "நரம்புவடக் கீழ்நாளம் வயிற்றுப்புற உடற்சுவர் மற்றும் நரம்புவடப் பகுதிகளில் இருந்து இரத்தத்தைச் சேகரித்து இணை நாளங்கள் வழியாக முதுகுப்புற நாள அமைப்புடன் தொடர்பு கொள்கிறது.",
                    cueEn = "Ventral collection",
                    cueTa = "வயிற்றுப்புறச் சேகரிப்பு",
                ),
                ProcessStage(
                    target = "lateral-oesophageal-vessels",
                    direction = ProcessDirection.COLLECTION,
                    mechanismEn = "The paired lateral-oesophageal vessels collect blood from several anterior tissues and communicate with the supra-oesophageal vascular system.",
                    mechanismTa = "இணையான உணவுக்குழாய் பக்கவாட்டு நாளங்கள் பல முன்புறத் திசுக்களிலிருந்து இரத்தத்தைச் சேகரித்து உணவுக்குழாய் மேல்நாள அமைப்புடன் தொடர்பு கொள்கின்றன.",
                    cueEn = "Anterior collecting network",
                    cueTa = "முன்புறச் சேகரிப்பு வலை",
                ),
                ProcessStage(
                    target = "supra-oesophageal-vessel",
                    direction = ProcessDirection.COLLECTION,
                    mechanismEn = "The supra-oesophageal vessel participates in the anterior collecting network and in vascular connections associated with the heart arches.",
                    mechanismTa = "உணவுக்குழாய் மேல்நாளம் முன்புற இரத்தச் சேகரிப்பு வலையிலும் இதய வளைவுகளுடன் தொடர்புடைய நாள இணைப்புகளிலும் பங்கேற்கிறது.",
                    cueEn = "Anterior vascular connection",
                    cueTa = "முன்புற நாள இணைப்பு",
                ),
            ),
        ),
    )

    fun forSystem(system: EarthwormSystem): ProcessDefinition? = definitions[system]
}
