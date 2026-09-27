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
    DIGESTIVE_JOURNEY,
    NEPHRIDIAL_EXCRETION,
}

internal enum class ProcessPhase {
    GAS_EXCHANGE,
    DISTRIBUTION,
    COLLECTION_NETWORK,
    INGESTION_TRANSPORT,
    MECHANICAL_DIGESTION,
    CHEMICAL_DIGESTION,
    INTESTINAL_PROCESSING,
    NEPHRIDIAL_PROCESSING,
    EXCRETORY_ROUTE_COMPARISON,
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
    PROCESSING,
    ABSORPTION,
    FILTRATION,
    ENTERONEPHRIC,
    EXONEPHRIC,
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
        EarthwormSystem.DIGESTIVE to ProcessDefinition(
            process = BiologicalProcess.DIGESTIVE_JOURNEY,
            system = EarthwormSystem.DIGESTIVE,
            stages = listOf(
                ProcessStage(
                    target = "pharynx",
                    phase = ProcessPhase.INGESTION_TRANSPORT,
                    direction = ProcessDirection.POSTERIOR,
                    titleEn = "Muscular pharynx — ingestion pump",
                    titleTa = "தசைமிகு தொண்டை — உணவு உள்ளிழுக்கும் இயக்கப்பகுதி",
                    mechanismEn = "Muscular pharyngeal action draws soil and organic matter inward; pharyngeal gland secretion is added as the ingested material enters the foregut.",
                    mechanismTa = "தொண்டையின் தசை இயக்கம் மண்ணையும் கரிம உணவுத் துகள்களையும் உள்ளிழுக்கிறது; உட்புகும் பொருளுடன் தொண்டைச் சுரப்பிகளின் சுரப்பும் சேர்கிறது.",
                    cueEn = "Ingestion and posterior passage begin",
                    cueTa = "உள்ளிழுத்தல் மற்றும் பின்நோக்கிய கடத்தல் தொடங்குகிறது",
                ),
                ProcessStage(
                    target = "oesophagus",
                    phase = ProcessPhase.INGESTION_TRANSPORT,
                    direction = ProcessDirection.POSTERIOR,
                    titleEn = "Oesophagus — conduction",
                    titleTa = "உணவுக்குழாய் — கடத்தல்",
                    mechanismEn = "The narrow oesophagus conducts ingested material from the pharynx toward the gizzard; its chief role here is transport rather than grinding.",
                    mechanismTa = "குறுகிய உணவுக்குழாய் தொண்டையிலிருந்து அரவைப்பையை நோக்கி உட்புகுந்த உணவுப் பொருளை கடத்துகிறது; இங்கு அதன் முக்கிய பணி அரைப்பதல்ல, கடத்தலாகும்.",
                    cueEn = "Food moves posteriorly",
                    cueTa = "உணவு பின்நோக்கி நகர்கிறது",
                ),
                ProcessStage(
                    target = "gizzard",
                    phase = ProcessPhase.MECHANICAL_DIGESTION,
                    direction = ProcessDirection.PROCESSING,
                    titleEn = "Gizzard — mechanical grinding",
                    titleTa = "அரவைப்பை — இயந்திர அரைப்பு",
                    mechanismEn = "The thick muscular gizzard mechanically grinds soil-borne organic particles with ingested grit. Its exact segment position varies among published Metaphire posthuma accounts, so this process lesson emphasizes function rather than one universal segment number.",
                    mechanismTa = "தடித்த தசைச் சுவருடைய அரவைப்பை, உட்புகுந்த மண்துகள்களின் உதவியுடன் கரிம உணவுத் துகள்களை இயந்திரமாக அரைக்கிறது. வெளியிடப்பட்ட Metaphire posthuma பதிவுகளில் அதன் கண்ட அமைவிடம் மாறுபடுவதால், இக்கற்றல் நிகழ்வு ஒரே கண்ட எண்ணை விட அதன் பணியை மையப்படுத்துகிறது.",
                    cueEn = "Mechanical reduction of food particles",
                    cueTa = "உணவுத் துகள்களின் இயந்திரச் சிதைவு",
                ),
                ProcessStage(
                    target = "stomach",
                    phase = ProcessPhase.CHEMICAL_DIGESTION,
                    direction = ProcessDirection.PROCESSING,
                    titleEn = "Glandular stomach region — chemical conditioning",
                    titleTa = "சுரப்புமிகு இரைப்பைப் பகுதி — வேதியியல் மாற்றம்",
                    mechanismEn = "Posterior to the gizzard and before the intestine, glandular secretion continues chemical conditioning and digestion. This is treated as a teaching region rather than a rigid universal segment range.",
                    mechanismTa = "அரவைப்பைக்குப் பின்பும் குடலுக்கு முன்பும் உள்ள சுரப்புமிகு பகுதியின் சுரப்புகள் உணவின் வேதியியல் மாற்றத்தையும் செரிமானத்தையும் தொடர்கின்றன. இதை மாறாத கண்ட வரம்பாக அல்லாமல் கற்றல் பகுதியாகக் கருத வேண்டும்.",
                    cueEn = "Chemical digestion continues",
                    cueTa = "வேதியியல் செரிமானம் தொடர்கிறது",
                ),
                ProcessStage(
                    target = "intestine",
                    phase = ProcessPhase.INTESTINAL_PROCESSING,
                    direction = ProcessDirection.ABSORPTION,
                    titleEn = "Intestine — digestion and absorption",
                    titleTa = "குடல் — செரிமானமும் உறிஞ்சலும்",
                    mechanismEn = "From segment XV onward, the intestine completes digestion and absorbs soluble nutrients. The intestinal region is functionally specialized rather than being a uniform tube.",
                    mechanismTa = "XV-ஆம் கண்டத்திலிருந்து தொடங்கும் குடல் செரிமானத்தை நிறைவு செய்து கரையக்கூடிய ஊட்டப்பொருள்களை உறிஞ்சுகிறது. குடல் முழுவதும் ஒரே மாதிரியான குழாய் அல்ல; செயல்பாட்டு சிறப்பாக்கம் கொண்டது.",
                    cueEn = "Nutrient absorption",
                    cueTa = "ஊட்டப்பொருள் உறிஞ்சல்",
                ),
                ProcessStage(
                    target = "intestinal-caeca",
                    phase = ProcessPhase.INTESTINAL_PROCESSING,
                    direction = ProcessDirection.PROCESSING,
                    titleEn = "Intestinal caeca — digestive specialization",
                    titleTa = "குடல் நீட்சிகள் — செரிமானச் சிறப்பாக்கம்",
                    mechanismEn = "The paired intestinal caeca arise from the intestinal region and increase its secretory and digestive capacity. They are functional specializations of the intestine, not a separate serial chamber through which all food must pass.",
                    mechanismTa = "இணையான குடல் நீட்சிகள் குடல் பகுதியில் தோன்றி அதன் சுரப்பும் செரிமானத் திறனையும் அதிகரிக்கின்றன. அவை எல்லா உணவும் கட்டாயம் கடந்து செல்லும் தனித்த தொடர் அறை அல்ல; குடலின் செயல்பாட்டு சிறப்பாக்கங்களாகும்.",
                    cueEn = "Secretory and digestive support",
                    cueTa = "சுரப்பும் செரிமான உதவியும்",
                ),
                ProcessStage(
                    target = "typhlosole",
                    phase = ProcessPhase.INTESTINAL_PROCESSING,
                    direction = ProcessDirection.ABSORPTION,
                    titleEn = "Typhlosole — increased absorptive surface",
                    titleTa = "டைஃப்ளோசோல் — அதிகரித்த உறிஞ்சும் பரப்பளவு",
                    mechanismEn = "The typhlosole is a dorsal infolding of the intestinal wall that increases absorptive surface area without greatly increasing body diameter. It is a fold into the lumen, not a separate tube.",
                    mechanismTa = "டைஃப்ளோசோல் என்பது குடல் சுவரின் முதுகுப்புற நீள்மடிப்பு; உடல் விட்டத்தை பெரிதாக அதிகரிக்காமல் உறிஞ்சும் பரப்பளவை அதிகரிக்கிறது. இது தனிக் குழாய் அல்ல; குடல் உட்புழைக்குள் நீளும் சுவர்மடிப்பாகும்.",
                    cueEn = "Expanded absorptive surface",
                    cueTa = "உறிஞ்சும் பரப்பளவு அதிகரிப்பு",
                ),
            ),
        ),
        EarthwormSystem.EXCRETORY to ProcessDefinition(
            process = BiologicalProcess.NEPHRIDIAL_EXCRETION,
            system = EarthwormSystem.EXCRETORY,
            stages = listOf(
                ProcessStage(
                    target = "nephridium",
                    phase = ProcessPhase.NEPHRIDIAL_PROCESSING,
                    direction = ProcessDirection.FILTRATION,
                    titleEn = "Septal nephridium — filtration and tubular modification",
                    titleTa = "இடைத்திரை நெஃப்ரிடியம் — வடிகட்டலும் குழல் மாற்றமும்",
                    mechanismEn = "Coelomic fluid enters the enlarged septal nephridium through the nephrostome. Reabsorption and secretion along the coiled tubule modify the fluid, integrating excretion with water-and-salt regulation.",
                    mechanismTa = "உடற்குழித் திரவம் நெஃப்ரோஸ்டோம் வழியாக பெரிதாக்கப்பட்ட இடைத்திரை நெஃப்ரிடியத்திற்குள் நுழைகிறது. சுருண்ட குழலில் நடைபெறும் மீளுறிஞ்சலும் சுரப்பும் கழிவுத் திரவத்தை மாற்றி, கழிவுநீக்கத்தையும் நீர்–உப்பு சமநிலையையும் ஒருங்கிணைக்கின்றன.",
                    cueEn = "Filtration → reabsorption/secretion",
                    cueTa = "வடிகட்டல் → மீளுறிஞ்சல் / சுரப்பு",
                ),
                ProcessStage(
                    target = "septal-nephridia",
                    phase = ProcessPhase.EXCRETORY_ROUTE_COMPARISON,
                    direction = ProcessDirection.ENTERONEPHRIC,
                    titleEn = "Septal nephridia — enteronephric route",
                    titleTa = "இடைத்திரை நெஃப்ரிடியாக்கள் — என்டெரோநெஃப்ரிக் பாதை",
                    mechanismEn = "Numerous septal nephridia filter and modify body fluid. In this species account their ducts ultimately communicate with the gut, so their discharge is enteronephric rather than directly external.",
                    mechanismTa = "பல இடைத்திரை நெஃப்ரிடியாக்கள் உடல் திரவத்தை வடிகட்டி மாற்றுகின்றன. இவ்வினத்தில் அவற்றின் நாளங்கள் இறுதியில் குடலுடன் தொடர்பு கொள்வதால், வெளியேற்றம் நேரடியாக வெளிப்புறத்திற்கு அல்ல; என்டெரோநெஃப்ரிக் பாதையாகும்.",
                    cueEn = "Gut-directed discharge",
                    cueTa = "குடலை நோக்கிய வெளியேற்றம்",
                ),
                ProcessStage(
                    target = "pharyngeal-nephridia",
                    phase = ProcessPhase.EXCRETORY_ROUTE_COMPARISON,
                    direction = ProcessDirection.ENTERONEPHRIC,
                    titleEn = "Pharyngeal nephridia — enteronephric discharge",
                    titleTa = "தொண்டை நெஃப்ரிடியாக்கள் — என்டெரோநெஃப்ரிக் வெளியேற்றம்",
                    mechanismEn = "The three paired pharyngeal nephridial tufts in segments IV–VI discharge through ducts into the buccal cavity and pharynx. Their proximity to the pharynx does not make them digestive glands.",
                    mechanismTa = "IV–VI கண்டங்களில் உள்ள மூன்று இணைத் தொண்டை நெஃப்ரிடியத் தொகுதிகள் தமது குழல்கள் வழியாக வாயறைக்கும் தொண்டைக்கும் கழிவுத் திரவத்தை செலுத்துகின்றன. தொண்டைக்கு அருகில் இருப்பதால் அவை செரிமானச் சுரப்பிகள் ஆகாது.",
                    cueEn = "Internal discharge into foregut region",
                    cueTa = "முன்குடல் பகுதிக்குள் உள்ளக வெளியேற்றம்",
                ),
                ProcessStage(
                    target = "integumentary-nephridia",
                    phase = ProcessPhase.EXCRETORY_ROUTE_COMPARISON,
                    direction = ProcessDirection.EXONEPHRIC,
                    titleEn = "Integumentary nephridia — exonephric discharge",
                    titleTa = "உடற்சுவர் நெஃப்ரிடியாக்கள் — எக்சோநெஃப்ரிக் வெளியேற்றம்",
                    mechanismEn = "Very numerous integumentary nephridia in the body wall discharge excretory fluid directly to the exterior through nephridiopores. This external route distinguishes them from the gut-opening pharyngeal and septal systems.",
                    mechanismTa = "உடற்சுவரில் மிக அதிக எண்ணிக்கையில் உள்ள உடற்சுவர் நெஃப்ரிடியாக்கள் நெஃப்ரிடியத் துளைகள் வழியாக கழிவுத் திரவத்தை நேரடியாக வெளியேற்றுகின்றன. இந்த வெளிப்புற வெளியேற்றப் பாதை, குடலுக்குள் திறக்கும் தொண்டை மற்றும் இடைத்திரை நெஃப்ரிடியாக்களிலிருந்து இவற்றை வேறுபடுத்துகிறது.",
                    cueEn = "Direct external discharge",
                    cueTa = "நேரடி வெளிப்புற வெளியேற்றம்",
                ),
            ),
        ),
    )

    fun forSystem(system: EarthwormSystem): ProcessDefinition? = definitions[system]
}
