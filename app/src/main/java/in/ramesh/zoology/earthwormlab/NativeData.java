package in.ramesh.zoology.earthwormlab;

import java.util.Arrays;
import java.util.List;

/**
 * Native visual hotspot geometry only.
 * Scientific descriptions remain authoritative in earthworm_content_v138.json.
 */
public final class NativeData {
    public static final class StructureRecord {
        public final String id,name;
        public final float x,y;
        public StructureRecord(String id,String name,float x,float y){
            this.id=id;this.name=name;this.x=x;this.y=y;
        }
    }

    public static final class SystemRecord {
        public final String id,name;
        public final List<StructureRecord> structures;
        public SystemRecord(String id,String name,StructureRecord... structures){
            this.id=id;this.name=name;this.structures=Arrays.asList(structures);
        }
    }

    public static final SystemRecord[] SYSTEMS={
        new SystemRecord("setup","Preparation"),

        new SystemRecord("external","External morphology",
            s("prostomium","Prostomium",.08f,.50f),
            s("mouth","Mouth",.105f,.60f),
            s("spermathecal-pores","Spermathecal pores",.24f,.39f),
            s("dorsal-pores","Dorsal pores",.35f,.33f),
            s("female-pore","Female genital pore",.42f,.62f),
            s("clitellum","Clitellum",.46f,.50f),
            s("male-pores","Male genital pores",.56f,.62f),
            s("setae","Perichaetine setae",.69f,.66f),
            s("anus","Anus",.92f,.50f)),

        new SystemRecord("digestive","Digestive system",
            s("pharynx","Muscular pharynx",.14f,.50f),
            s("oesophagus","Oesophagus",.25f,.50f),
            s("gizzard","Gizzard",.34f,.50f),
            s("stomach","Glandular stomach",.43f,.50f),
            s("intestine","Intestine",.65f,.50f),
            s("intestinal-caeca","Intestinal caeca",.58f,.38f),
            s("typhlosole","Typhlosole",.73f,.48f)),

        new SystemRecord("circulatory","Circulatory system",
            s("dorsal-vessel","Dorsal blood vessel",.58f,.38f),
            s("supra-oesophageal-vessel","Supra-oesophageal vessel",.24f,.32f),
            s("lateral-hearts","Four pairs of hearts",.34f,.50f),
            s("lateral-oesophageal-vessels","Lateral-oesophageal vessels",.24f,.68f),
            s("ventral-vessel","Ventral blood vessel",.58f,.61f),
            s("subneural-vessel","Subneural vessel",.67f,.72f),
            s("capillary-networks","Segmental capillary networks",.80f,.50f)),

        new SystemRecord("respiratory","Cutaneous respiration",
            s("mucus-film","Mucus film",.25f,.31f),
            s("moist-epidermis","Moist epidermis",.40f,.40f),
            s("cutaneous-capillaries","Subepidermal capillaries",.60f,.58f),
            s("cutaneous-exchange","Cutaneous gas exchange",.78f,.36f)),

        new SystemRecord("excretory","Excretory system",
            s("pharyngeal-nephridia","Pharyngeal nephridia",.22f,.43f),
            s("septal-nephridia","Septal nephridia",.53f,.43f),
            s("integumentary-nephridia","Integumentary nephridia",.70f,.62f),
            s("nephridium","Septal nephridium (enlarged)",.84f,.45f)),

        new SystemRecord("reproductive","Reproductive system",
            s("spermathecae","Spermathecae",.27f,.60f),
            s("testes","Testes",.42f,.45f),
            s("seminal-vesicles","Seminal vesicles",.49f,.39f),
            s("ovaries","Ovaries",.57f,.55f),
            s("oviducts","Oviducts",.61f,.50f),
            s("prostate-glands","Prostate glands",.70f,.40f),
            s("vasa-deferentia","Vasa deferentia",.74f,.55f)),

        new SystemRecord("nervous","Nervous system",
            s("cerebral-ganglia","Cerebral ganglia",.18f,.37f),
            s("circum-pharyngeal-connectives","Circumpharyngeal connectives",.23f,.48f),
            s("subpharyngeal-ganglion","Subpharyngeal ganglion",.27f,.58f),
            s("ventral-nerve-cord","Ventral nerve cord",.61f,.62f),
            s("segmental-ganglia","Segmental ganglia",.77f,.62f)),

        new SystemRecord("crosssection","Transverse section",
            s("cs-cuticle","Cuticle",.50f,.15f),
            s("cs-epidermis","Epidermis",.62f,.20f),
            s("cs-circular-muscle","Circular muscle layer",.72f,.29f),
            s("cs-longitudinal-muscle","Longitudinal muscle layer",.77f,.43f),
            s("cs-peritoneum","Peritoneum",.74f,.57f),
            s("cs-coelom","Coelom",.63f,.68f),
            s("cs-gut","Intestinal wall and typhlosole",.50f,.52f),
            s("cs-dorsal-vessel","Dorsal vessel",.50f,.34f),
            s("cs-ventral-vessel","Ventral vessel",.50f,.68f),
            s("cs-nerve-cord","Ventral nerve cord",.50f,.77f),
            s("cs-subneural-vessel","Subneural vessel",.50f,.84f),
            s("cs-setae","Setae",.25f,.62f))
    };

    private static StructureRecord s(String id,String name,float x,float y){
        return new StructureRecord(id,name,x,y);
    }

    public static SystemRecord system(String id){
        for(SystemRecord s:SYSTEMS)if(s.id.equals(id))return s;
        return null;
    }

    private NativeData(){}
}
