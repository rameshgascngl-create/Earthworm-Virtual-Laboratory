package in.ramesh.zoology.earthwormlab;

import java.util.Arrays;
import java.util.List;

public final class NativeData {
    public static final class StructureRecord {
        public final String id,name,location,function,significance;
        public final float x,y;
        public StructureRecord(String id,String name,String location,String function,String significance,float x,float y){
            this.id=id;this.name=name;this.location=location;this.function=function;this.significance=significance;this.x=x;this.y=y;
        }
    }
    public static final class SystemRecord {
        public final String id,shortName,name,summary;
        public final List<StructureRecord> structures;
        public SystemRecord(String id,String shortName,String name,String summary,StructureRecord... structures){
            this.id=id;this.shortName=shortName;this.name=name;this.summary=summary;this.structures=Arrays.asList(structures);
        }
    }

    public static final SystemRecord[] SYSTEMS = new SystemRecord[]{
        new SystemRecord("external","External","External morphology",
            "Native dorsal-view teaching map for segmentation, clitellum and external apertures.",
            s("prostomium","Prostomium","Anterior lobe overhanging the mouth","Assists burrowing and sensory exploration","Distinguish it from the peristomium",0.10f,0.50f),
            s("peristomium","Peristomium","First true segment surrounding the mouth","Forms the oral region","Segment I surrounds the mouth",0.16f,0.50f),
            s("clitellum","Clitellum","Glandular saddle around segments 14–16 in the standard practical description","Secretes cocoon material during reproduction","A key adult external character",0.46f,0.50f),
            s("female_pore","Female genital pore","Single mid-ventral pore on segment 14","Discharges ova","Do not confuse with paired male pores",0.42f,0.64f),
            s("male_pores","Male genital pores","Paired ventro-lateral pores on segment 18","Open the male reproductive ducts","Important segmental landmark",0.57f,0.62f),
            s("spermathecal_pores","Spermathecal pores","Intersegmental grooves 5/6 to 8/9 in the standard practical account","Receive sperm during copulation","Use groove notation correctly",0.28f,0.38f)),
        new SystemRecord("digestive","Digestive","Digestive system",
            "Native schematic of the straight alimentary canal and its regional specialisations.",
            s("pharynx","Pharynx","Anterior muscular region behind the buccal cavity","Ingests and pumps food","Recognise its muscular wall",0.18f,0.48f),
            s("oesophagus","Oesophagus","Narrow tube posterior to the pharynx","Conducts food posteriorly","A conduit, not the principal grinding chamber",0.28f,0.48f),
            s("gizzard","Gizzard","Thick muscular region around segment 8 in common practical descriptions","Mechanically grinds food","Function is mechanical trituration",0.36f,0.48f),
            s("stomach","Stomach","Region posterior to the gizzard","Chemical digestion and transit","Keep regional nomenclature specimen-aware",0.45f,0.48f),
            s("intestine","Intestine","Long posterior gut","Digestion and absorption","Major absorptive region",0.65f,0.48f),
            s("typhlosole","Typhlosole","Dorsal internal fold of the intestine","Increases absorptive surface area","It is an intestinal fold, not a separate organ",0.72f,0.38f)),
        new SystemRecord("circulatory","Blood","Circulatory system",
            "Closed vascular system represented by principal longitudinal vessels and segmental hearts.",
            s("dorsal_vessel","Dorsal blood vessel","Mid-dorsal longitudinal vessel","Propels blood anteriorly in the standard description","Contractile longitudinal vessel",0.52f,0.37f),
            s("ventral_vessel","Ventral blood vessel","Mid-ventral longitudinal vessel","Distributes blood posteriorly to organs and body wall","Major distributing vessel",0.52f,0.63f),
            s("hearts","Hearts / aortic arches","Paired vascular connections in characteristic anterior segments","Connect principal vessels and maintain circulation","Use segment numbers appropriate to the prescribed specimen",0.34f,0.50f)),
        new SystemRecord("nervous","Nervous","Nervous system",
            "Native map of cerebral ganglia, circumpharyngeal connectives and ventral nerve cord.",
            s("cerebral","Cerebral ganglia","Dorsal to the pharyngeal region","Integrate anterior sensory information","Often termed the brain",0.19f,0.38f),
            s("connectives","Circumpharyngeal connectives","Pass around the pharynx","Link cerebral ganglia with the ventral system","Form the nerve ring with associated ganglia",0.24f,0.50f),
            s("ventral_nerve","Ventral nerve cord","Runs longitudinally along the ventral midline","Coordinates segmental motor and sensory activity","Segmental ganglia are associated with the cord",0.60f,0.64f)),
        new SystemRecord("excretory","Nephridia","Excretory system",
            "Segmentally repeated nephridia perform excretion and osmoregulation.",
            s("septal_nephridia","Septal nephridia","Associated with intersegmental septa in many posterior segments","Excretion and osmoregulation","Keep septal, integumentary and pharyngeal nephridia distinct",0.58f,0.44f),
            s("integumentary_nephridia","Integumentary nephridia","Embedded in the body wall","Excretion to the exterior in the standard account","Do not conflate with septal nephridia",0.68f,0.58f),
            s("pharyngeal_nephridia","Pharyngeal nephridia","Anterior tufts associated with pharyngeal segments","Excretion and ionic regulation","Anterior specialised nephridial group",0.24f,0.42f)),
        new SystemRecord("reproductive","Repro","Reproductive system",
            "Hermaphrodite reproductive anatomy with separate male, female and sperm-storage components.",
            s("testes","Testes","Paired male gonads in anterior reproductive segments","Produce sperm","Differentiate testes from seminal vesicles",0.28f,0.42f),
            s("seminal_vesicles","Seminal vesicles","Large sacs associated with male reproductive region","Support maturation/storage of sperm","Not equivalent to spermathecae",0.36f,0.40f),
            s("spermathecae","Spermathecae","Paired sacs in characteristic anterior segments","Receive and store sperm from a mate","Female sperm-storage organs",0.25f,0.61f),
            s("ovaries","Ovaries","Paired female gonads near segment 13 in standard practical accounts","Produce ova","Lead to short oviducts",0.47f,0.58f),
            s("male_ducts","Vasa deferentia","Paired sperm ducts running posteriorly","Conduct sperm to male pores","Trace ducts separately from prostatic structures",0.58f,0.44f))
    };

    private static StructureRecord s(String id,String name,String location,String function,String significance,float x,float y){
        return new StructureRecord(id,name,location,function,significance,x,y);
    }
    public static SystemRecord system(String id){
        for(SystemRecord s:SYSTEMS) if(s.id.equals(id)) return s;
        return null;
    }
    private NativeData(){}
}
