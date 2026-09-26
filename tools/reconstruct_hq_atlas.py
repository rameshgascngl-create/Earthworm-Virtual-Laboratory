#!/usr/bin/env python3
import argparse, base64, hashlib, io, json, re
from pathlib import Path
from PIL import Image, ImageDraw, ImageFilter

BASELINE_SHA="d9b8cca85459764f4169edf4a74aa2e159ea5281968fd49586b15c69176795ed"
HISTORICAL_SHA="5dbc6fe5754891a0c6dd6b5d307be232e35a1d244fcca72788749991cbc8affd"
BASELINE_COMMIT="ac47a83762ae2ecbaded7b077f25702daf2d1fa8"
HISTORICAL_COMMIT="92f3acd2497b95e1930b3acdbe4859403178b426"
EXPECTED_PRESERVED={
 "external":"d439c1953e1e69acab0e26d2f4ca508a9260904b9fc7e9850fe3172847ed5026",
 "circulatory":"76277234651906cf9f2aa5d3e0c7af86a450f845f968b02297c93a61d0004bb0",
 "respiratory":"88e2ad2b73b3210ec4d3d877a55c04673a7de22e49643b843175e2f659da62fb",
 "excretory":"8c56a27dd2262750a2fb5fc4923c4c4ba1a25825bb3ec5e308f37ae5ccec984e",
 "reproductive":"23ef28d2e073a46afe01b026051b5da5d7e8d5ddaa08e6e344151f207a2ef212",
 "nervous":"74bc0bb1272b6a0b444c08d475e633f66b4d49471402e80ed8944f9232d8c242",
 "crosssection":"590cdb9f9d1b227ec8f4b990aa87ec6ea037f33bd5c0d7051a1cface293826ac"}
HISTORICAL_DIGESTIVE_SHA="52ec7092e82b82ec14db508e6c8573ea5b0feb8989db060d4537e66691365506"

def sha(b): return hashlib.sha256(b).hexdigest()

def extract_webp(html,system):
    m=re.search(r'"'+re.escape(system)+r'":\{src:"data:image/webp;base64,([A-Za-z0-9+/=]+)"',html)
    if not m: raise SystemExit("Missing historical atlas image: "+system)
    return base64.b64decode(m.group(1))

def generate_digestive():
    W,H=2048,1536
    bg=Image.new("RGB",(W,H),(3,27,25))
    glow=Image.radial_gradient("L").resize((W,H),Image.Resampling.BICUBIC)
    bg=Image.composite(Image.new("RGB",(W,H),(11,81,73)),bg,glow.point(lambda x:int(x*.42)))
    im=bg.convert("RGBA"); d=ImageDraw.Draw(im,"RGBA")
    sh=Image.new("RGBA",(W,H),(0,0,0,0)); sd=ImageDraw.Draw(sh,"RGBA")
    sd.rounded_rectangle((70,420,1980,1115),radius=240,fill=(0,0,0,115))
    im=Image.alpha_composite(im,sh.filter(ImageFilter.GaussianBlur(34))); d=ImageDraw.Draw(im,"RGBA")
    d.rounded_rectangle((65,380,1985,1080),radius=230,fill=(115,49,40,255),outline=(228,138,111,255),width=8)
    d.rounded_rectangle((110,430,1940,1030),radius=190,fill=(71,35,31,255),outline=(173,96,75,255),width=5)
    for x in range(135,1930,55):
        d.line((x,410,x,465),fill=(235,155,126,150),width=3); d.line((x,995,x,1050),fill=(235,155,126,150),width=3)
    sh=Image.new("RGBA",(W,H),(0,0,0,0)); sd=ImageDraw.Draw(sh,"RGBA")
    sd.line((155,750,1880,750),fill=(0,0,0,130),width=170)
    im=Image.alpha_composite(im,sh.filter(ImageFilter.GaussianBlur(22))); d=ImageDraw.Draw(im,"RGBA")
    def tube(points,width,fill,outline=None,ow=0):
        if outline and ow:
            d.line(points,fill=outline,width=width+ow*2,joint="curve"); r=(width+ow*2)//2
            for x,y in (points[0],points[-1]): d.ellipse((x-r,y-r,x+r,y+r),fill=outline)
        d.line(points,fill=fill,width=width,joint="curve"); r=width//2
        for x,y in (points[0],points[-1]): d.ellipse((x-r,y-r,x+r,y+r),fill=fill)
    d.ellipse((145,625,410,875),fill=(235,157,119,255),outline=(255,211,172,225),width=5)
    for k in range(18):
        yy=650+k*12; d.line((180,yy,375,int(750+(yy-750)*.58)),fill=(126,63,51,120),width=2)
    tube([(375,750),(470,747),(550,748)],70,(220,147,112,255),(255,208,172,220),4)
    d.ellipse((520,610,730,890),fill=(150,61,52,255),outline=(255,185,149,255),width=8)
    for off in range(-75,76,20):
        d.arc((545+off//5,625+abs(off)//7,705-off//5,875-abs(off)//7),30,330,fill=(224,126,105,150),width=4)
    tube([(715,750),(770,750)],72,(222,142,105,255),(255,206,166,200),3)
    stomach=[(760,680),(835,640),(980,645),(1110,690),(1160,750),(1110,810),(980,855),(835,860),(760,820)]
    d.polygon(stomach,fill=(214,131,96,255),outline=(252,184,145,255))
    for x in range(800,1120,26): d.arc((x,675,x+70,825),80,280,fill=(245,184,146,100),width=2)
    tube([(1125,750),(1290,750),(1460,750),(1660,750),(1880,750)],120,(197,114,81,255),(255,184,142,220),5)
    # Paired simple smooth caeca, arising later in the intestinal region and extending anteriorly.
    # No segment numbers or labels are baked into this raster.
    for pts in ([(1295,720),(1250,685),(1200,650),(1140,625),(1080,612)],
                [(1295,780),(1250,815),(1200,850),(1140,875),(1080,888)]):
        tube(pts,54,(232,183,116,255),(255,218,159,220),4)
    for y in (720,780): d.ellipse((1265,y-34,1325,y+34),fill=(220,157,99,255),outline=(255,214,159,230),width=3)
    d.rounded_rectangle((1480,665,1825,835),radius=62,fill=(81,41,31,255),outline=(255,170,128,255),width=5)
    for x in range(1510,1800,24): d.line((x,687,x,813),fill=(190,105,77,150),width=3)
    fold=[(1490,704),(1550,695),(1620,700),(1690,694),(1765,703),(1815,700)]
    d.line(fold,fill=(244,187,137,255),width=26,joint="curve")
    d.line([(x,y+28) for x,y in fold],fill=(128,65,46,180),width=7,joint="curve")
    d.line((390,730,510,730),fill=(255,224,198,90),width=10); d.line((1145,720,1840,720),fill=(255,224,198,75),width=9)
    d.rounded_rectangle((35,35,2013,1501),radius=40,outline=(78,156,145,120),width=3)
    out=io.BytesIO(); im.convert("RGB").save(out,"WEBP",quality=96,method=6); return out.getvalue()

def uri(b): return "data:image/webp;base64,"+base64.b64encode(b).decode("ascii")
def before(text,anchor,insertion):
    if text.count(anchor)!=1: raise SystemExit("Anchor not unique: "+anchor[:70])
    return text.replace(anchor,insertion+anchor,1)

def main():
    p=argparse.ArgumentParser(); p.add_argument("--historical",required=True); p.add_argument("--target",required=True); p.add_argument("--manifest",required=True); p.add_argument("--source-manifest",required=True); a=p.parse_args()
    tp=Path(a.target); hp=Path(a.historical); baseb=tp.read_bytes(); histb=hp.read_bytes()
    if sha(baseb)!=BASELINE_SHA: raise SystemExit("Target is not hardened v1.3.8 baseline")
    if sha(histb)!=HISTORICAL_SHA: raise SystemExit("Historical HQ candidate hash mismatch")
    html=baseb.decode(); hist=histb.decode()
    # Authorised species-specific corrections for the release-authoritative line.
    # Hotspot geometry and organ drawings remain unchanged; only application-rendered
    # labels/explanations are corrected to the cited Metaphire posthuma specimen evidence.
    corrections=[
      ('Gizzard VIII†','Gizzard'),
      ('அரவைப்பை VIII†','அரவைப்பை'),
      ('Intestinal caeca XXVI†','Intestinal caeca XXVII'),
      ('குடல் நீட்சிகள் XXVI†','குடல் சீக்காக்கள் XXVII'),
      ('"Gizzard VIII":"அரவைப்பை VIII"','"Gizzard":"அரவைப்பை"'),
      ('"Intestinal caeca XXVI":"குடல் நீட்சிகள் XXVI"','"Intestinal caeca XXVII":"குடல் சீக்காக்கள் XXVII"'),
      ('"Gizzard VIII": {"lines": ["அரவைப்பை VIII"], "size": 16}','"Gizzard": {"lines": ["அரவைப்பை"], "size": 16}'),
      ('"Intestinal caeca XXVI": {"lines": ["குடல் நீட்சிகள் XXVI"], "size": 16}','"Intestinal caeca XXVII": {"lines": ["குடல் சீக்காக்கள் XXVII"], "size": 16}'),
      ('"Gizzard VIII†": {"lines": ["அரவைப்பை VIII†"], "size": 16}','"Gizzard": {"lines": ["அரவைப்பை"], "size": 16}'),
      ('"Intestinal caeca XXVI†": {"lines": ["குடல் நீட்சிகள் XXVI†"], "size": 16, "y": 169}','"Intestinal caeca XXVII": {"lines": ["குடல் சீக்காக்கள் XXVII"], "size": 16, "y": 169}'),
      ('A thick, muscular chamber in segment VIII.','A thick, muscular chamber occupying segments IX–X in identified Metaphire posthuma specimens.'),
      ('VIII-ஆம் கண்டத்தில் அமைந்த தடித்த தசைமிகு அறை.','அடையாளம் உறுதிசெய்யப்பட்ட Metaphire posthuma மாதிரிகளில் IX–X கண்டங்களை ஆக்கிரமிக்கும் தடித்த தசைமிகு அறை.'),
      ('A paired, forwardly directed outgrowth arising near segment XXVI.','Paired, simple, smooth, forwardly directed intestinal outgrowths arising at segment XXVII and extending anteriorly to about XXV in identified Metaphire posthuma specimens.'),
      ('XXVI-ஆம் கண்டத்திற்கு அருகில் தோன்றி முன்நோக்கி நீளும் ஓர் இணை குடல் நீட்சிகள்.','அடையாளம் உறுதிசெய்யப்பட்ட Metaphire posthuma மாதிரிகளில் XXVII-ஆம் கண்டத்தில் தோன்றி சுமார் XXV வரை முன்நோக்கி நீளும் இணையான, எளிய, வழுவழுப்பான குடல் சீக்காக்கள்.'),
      ('STRUCTURES.gizzard.fixEn="Identify by its thick grinding wall; verify segment boundaries in the actual specimen."','STRUCTURES.gizzard.fixEn="Identify the gizzard by its thick grinding wall. This release uses the specimen-level IX–X position reported for Metaphire posthuma; older teaching accounts may use different conventional numbering."'),
      ('STRUCTURES.gizzard.fixTa="தடித்த அரைக்கும் சுவரால் அடையாளம் காண்க; மாதிரியில் கண்ட எல்லைகளைச் சரிபார்க்கவும்."','STRUCTURES.gizzard.fixTa="தடித்த அரைக்கும் சுவரால் அரவைப்பையை அடையாளம் காண்க. இப்பதிப்பில் Metaphire posthuma மாதிரி ஆய்வுச் சான்றின்படி IX–X கண்ட அமைவிடம் பயன்படுத்தப்படுகிறது; சில பழைய பாட விளக்கங்களில் வேறு மரபு எண்கள் காணப்படலாம்."'),
      ('these plates retain conventional teaching positions. Bantaowong et al. (2011), p. 58, report caeca XXVII, gizzard IX–X and ventral clitellar setae in identified specimens. Confirm the institutional account before using segment-number items in an examination.','this release uses specimen-level Metaphire posthuma positions for the corrected digestive teaching content. Bantaowong et al. (2011), p. 58, report caeca XXVII, gizzard IX–X and retained ventral clitellar setae in identified specimens. Some institutional manuals retain older conventional numbering; confirm local examination requirements.'),
      ('வரைபடங்கள் பாட மரபின் அமைவிடங்களைப் பின்பற்றுகின்றன. Bantaowong மற்றும் குழுவினர் (2011), பக். 58: குடல் நீட்சிகள் XXVII; அரவைப்பை IX–X; கிளைட்டெல்லத்தின் வயிற்றுப்புறத்தில் சீட்டாக்கள். தேர்விற்கு முன் நிறுவனப் பாட விளக்கத்தை உறுதிப்படுத்தவும்.','திருத்தப்பட்ட செரிமானப் பகுதி இப்பதிப்பில் Metaphire posthuma மாதிரி ஆய்வுச் சான்றின் அமைவிடங்களைப் பின்பற்றுகிறது. Bantaowong மற்றும் குழுவினர் (2011), பக். 58: குடல் சீக்காக்கள் XXVII; அரவைப்பை IX–X; கிளைட்டெல்லத்தின் வயிற்றுப்புறத்தில் சீட்டாக்கள் நீடிக்கலாம். சில நிறுவனப் பாடநூல்களில் பழைய மரபு எண்கள் இருக்கலாம்; தேர்விற்கு உள்ளூர் பாட விளக்கத்தை உறுதிப்படுத்தவும்.')
    ]
    for old,new in corrections:
        if old not in html:
            raise SystemExit("Scientific-correction source text not found: "+old[:100])
        html=html.replace(old,new)
    invariants=['const APP_VERSION="1.3.8", STORAGE_KEY="earthwormDissection.v1";',
      'function pauseLaboratory(){demo.playing=false;clearProcedureTimer();stopSpeech();saveState();renderProcedure()}',
      'window.addEventListener("pagehide",pauseLaboratory)','pause:pauseLaboratory']
    for x in invariants:
        if x not in html: raise SystemExit("Missing hardened invariant: "+x[:80])
    if "HQ_ATLAS" in html or "hqAtlasPanel" in html: raise SystemExit("Baseline already has atlas integration")

    plate={}
    for k,v in EXPECTED_PRESERVED.items():
        b=extract_webp(hist,k)
        if sha(b)!=v: raise SystemExit("Preserved plate hash mismatch: "+k)
        plate[k]=b
    old=extract_webp(hist,"digestive")
    if sha(old)!=HISTORICAL_DIGESTIVE_SHA: raise SystemExit("Historical digestive hash mismatch")
    plate["digestive"]=generate_digestive()

    meta={
    "external":["External morphology reference plate","புற அமைப்பியல் குறிப்புப் படம்","High-resolution dorsal and ventral external morphology teaching plate of Metaphire posthuma.","Metaphire posthuma மண்புழுவின் முதுகுப்புற மற்றும் வயிற்றுப்புற புற அமைப்பியலைக் காட்டும் உயர்தர கற்பித்தல் படம்.","Dorsal and ventral teaching views. Use the interactive structure text for species-level evidence and terminology.","முதுகுப்புறமும் வயிற்றுப்புறமும் காட்டும் கற்பித்தல் தோற்றங்கள். இனச்சார் சான்றுகளுக்கும் சொல்லாக்கத்திற்கும் தொடர்பாடும் அமைப்பு உரையைப் பயன்படுத்தவும்.",True],
    "digestive":["Digestive system — species-aware reference plate","செரிமான மண்டலம் — இனச்சார் குறிப்புப் படம்","Label-free high-resolution dorsal teaching plate of the Metaphire posthuma digestive tract with paired simple smooth intestinal caeca.","இரட்டையான எளிய, வழுவழுப்பான குடல் சீக்காக்களுடன் Metaphire posthuma மண்புழுவின் செரிமானப் பாதையை முதுகுப்புறமாகக் காட்டும் பெயரிடப்படாத உயர்தர கற்பித்தல் படம்.","Label-free reconstruction. The paired intestinal caeca are simple and smooth; species-specific segment evidence remains in the interactive text rather than being baked into the raster.","பெயரிடப்படாத மறுவடிவமைப்பு. இணைக் குடல் சீக்காக்கள் எளிமையான, வழுவழுப்பான பைகளாகக் காட்டப்பட்டுள்ளன; இனச்சார் கண்டச் சான்றுகள் படத்தில் நிரந்தரமாகப் பதிக்கப்படாமல் தொடர்பாடும் உரையில் வழங்கப்படுகின்றன.",True],
    "circulatory":["Circulatory system reference plate","இரத்த ஓட்ட மண்டலக் குறிப்புப் படம்","High-resolution teaching plate of major earthworm blood vessels and heart arches.","மண்புழுவின் முக்கிய இரத்த நாளங்களையும் இதய வளைவுகளையும் காட்டும் உயர்தர கற்பித்தல் படம்.","Teaching schematic of major vessels and heart arches. Use the interactive evidence notes before treating segment assignments as specimen-level facts.","முக்கிய இரத்த நாளங்களும் இதய வளைவுகளும் காட்டும் கற்பித்தல் திட்டப்படம். கண்ட அமைவிடங்களை மாதிரி-நிலை உண்மைகளாகக் கொள்ளும் முன் தொடர்பாடும் சான்றுக் குறிப்புகளைப் பார்க்கவும்.",True],
    "respiratory":["Cutaneous gas exchange reference plate","தோல் வழி வாயுப் பரிமாற்றக் குறிப்புப் படம்","High-resolution teaching plate of cutaneous gas exchange across moist earthworm skin and subepidermal capillaries.","ஈரமான மண்புழுத் தோல் மற்றும் புறத்தோலடித் தந்துகிகள் வழியிலான வாயுப் பரிமாற்றத்தைக் காட்டும் உயர்தர கற்பித்தல் படம்.","Tissue thickness is intentionally enlarged to explain diffusion across the moist body surface.","ஈரமான உடற்பரப்பின் வழியிலான பரவலை விளக்குவதற்காக திசு அடுக்குகளின் தடிமன் திட்டமிட்டு பெரிதாக்கப்பட்டுள்ளது.",False],
    "excretory":["Excretory system reference plate","கழிவு நீக்க மண்டலக் குறிப்புப் படம்","High-resolution teaching plate of pharyngeal, septal and integumentary nephridia in an earthworm.","மண்புழுவின் தொண்டை, இடைச்சுவர் மற்றும் புறத்தோல் நெஃப்ரிடியாக்களை காட்டும் உயர்தர கற்பித்தல் படம்.","Teaching schematic distinguishing the three principal nephridial categories and an enlarged septal nephridial unit.","மூன்று முக்கிய நெஃப்ரிடியா வகைகளையும் பெரிதாக்கப்பட்ட இடைச்சுவர் நெஃப்ரிடியல் அலகையும் வேறுபடுத்திக் காட்டும் கற்பித்தல் திட்டப்படம்.",False],
    "reproductive":["Reproductive system reference plate","இனப்பெருக்க மண்டலக் குறிப்புப் படம்","High-resolution dorsal teaching plate of paired and median reproductive organs of Metaphire posthuma.","Metaphire posthuma மண்புழுவின் இணை மற்றும் நடுக்கோட்டு இனப்பெருக்க உறுப்புகளை முதுகுப்புறமாகக் காட்டும் உயர்தர கற்பித்தல் படம்.","Dorsal teaching schematic. Species-level segment evidence remains available in the interactive structure descriptions.","முதுகுப்புற கற்பித்தல் திட்டப்படம். இனச்சார் கண்ட அமைவிடச் சான்றுகள் தொடர்பாடும் அமைப்பு விளக்கங்களில் வழங்கப்படுகின்றன.",True],
    "nervous":["Nervous system reference plate","நரம்பு மண்டலக் குறிப்புப் படம்","High-resolution teaching plate of cerebral ganglia, circumpharyngeal connectives, subpharyngeal ganglion and ventral nerve cord.","மூளை நரம்புத் திரள்கள், தொண்டையைச் சுற்றிய இணைப்புகள், தொண்டைக்கீழ் நரம்புத் திரள் மற்றும் வயிற்றுப்புற நரம்புவடம் ஆகியவற்றைக் காட்டும் உயர்தர கற்பித்தல் படம்.","Teaching schematic of the anterior nerve ring and ganglionated ventral nerve cord.","முன்புற நரம்பு வளையத்தையும் நரம்புத் திரள்களைக் கொண்ட வயிற்றுப்புற நரம்புவடத்தையும் காட்டும் கற்பித்தல் திட்டப்படம்.",False],
    "crosssection":["Transverse-section reference plate","குறுக்குவெட்டுக் குறிப்புப் படம்","High-resolution teaching transverse section through the intestinal region of an earthworm.","மண்புழுவின் குடற்பகுதி வழியாக எடுத்த உயர்தர கற்பித்தல் குறுக்குவெட்டுப் படம்.","Intestinal-region teaching section. Tissue thickness and spaces are exaggerated for clarity.","குடற்பகுதியின் கற்பித்தல் குறுக்குவெட்டு. தெளிவிற்காக திசு அடுக்குகளும் இடைவெளிகளும் பெரிதாக்கப்பட்டுள்ளன.",False]}
    order=["external","digestive","circulatory","respiratory","excretory","reproductive","nervous","crosssection"]; atlas={}
    for k in order:
        m=meta[k]; atlas[k]={"src":uri(plate[k]),"enTitle":m[0],"taTitle":m[1],"enAlt":m[2],"taAlt":m[3],"enCaption":m[4],"taCaption":m[5],"caution":m[6],"width":2048,"height":1536}
    html=before(html,'    const TOOLS={','    const HQ_ATLAS='+json.dumps(atlas,ensure_ascii=False,separators=(",",":"))+';\n')

    panel='''\n      <section id="hqAtlasPanel" class="hq-atlas-panel" aria-labelledby="hqAtlasTitle" hidden>
        <div class="hq-atlas-head"><div class="hq-atlas-title-wrap"><div id="hqAtlasKicker" class="hq-atlas-kicker">High-resolution anatomical atlas</div><h3 id="hqAtlasTitle" class="hq-atlas-title">Reference plate</h3><div id="hqAtlasMeta" class="hq-atlas-meta">Offline embedded reference · interactive SVG remains active above</div></div><button id="hqAtlasOpenBtn" class="ghost-btn small" type="button">⛶ Open full-resolution plate</button></div>
        <figure class="hq-atlas-figure"><img id="hqAtlasImage" class="hq-atlas-image" alt="" loading="lazy" decoding="async"/><figcaption id="hqAtlasCaption" class="hq-atlas-caption"></figcaption></figure>
      </section>\n'''
    html=before(html,'      <section id="procedurePanel" class="procedure-panel"',panel)
    dialog='''\n  <dialog id="hqAtlasDialog" class="hq-atlas-dialog" aria-labelledby="hqAtlasDialogTitle"><div class="dialog-head"><h2 id="hqAtlasDialogTitle">High-resolution anatomical atlas</h2><button id="hqAtlasDialogClose" class="close-x" type="button" aria-label="Close">×</button></div><div class="dialog-body"><img id="hqAtlasDialogImage" alt=""/></div></dialog>\n\n'''
    html=before(html,'  <script>\n',dialog)

    css=".hq-atlas-panel{border-top:1px solid var(--line);background:linear-gradient(180deg,#071d1b,#0a2522);padding:.85rem .9rem 1rem}.hq-atlas-panel[hidden]{display:none}.hq-atlas-head{display:flex;align-items:flex-start;justify-content:space-between;gap:.75rem;flex-wrap:wrap;margin-bottom:.65rem}.hq-atlas-title-wrap{min-width:0}.hq-atlas-kicker{font-size:.68rem;letter-spacing:.12em;text-transform:uppercase;color:var(--teal);font-weight:850;margin-bottom:.18rem}.hq-atlas-title{margin:0;font-size:1rem;color:#fff}.hq-atlas-meta{font-size:.72rem;color:var(--muted);margin-top:.2rem}.hq-atlas-figure{margin:0;border:1px solid #3e6962;border-radius:.9rem;overflow:hidden;background:#031513;box-shadow:0 16px 34px #0006}.hq-atlas-image{display:block;width:100%;height:auto;aspect-ratio:4/3;object-fit:contain;background:#032421;cursor:zoom-in;image-rendering:auto}.hq-atlas-caption{margin:0;padding:.7rem .8rem;color:#d9eee8;background:#071b19;border-top:1px solid var(--line);font-size:.78rem;line-height:1.5}.hq-atlas-caption strong{color:var(--gold)}.hq-atlas-caution{display:block;margin-top:.35rem;color:#ffe0a3}.hq-atlas-dialog{max-width:min(96vw,2100px);width:96vw;max-height:94vh}.hq-atlas-dialog .dialog-body{padding:.55rem;overflow:auto;max-height:calc(94vh - 64px);background:#02110f;overscroll-behavior:contain}.hq-atlas-dialog img{display:block;width:2048px;height:1536px;max-width:none;margin:auto;background:#032421;touch-action:pan-x pan-y}@media(max-width:640px){.hq-atlas-panel{padding:.65rem}.hq-atlas-head{align-items:stretch}.hq-atlas-head .ghost-btn{width:100%}.hq-atlas-caption{font-size:.76rem}}@media print{.hq-atlas-panel,.hq-atlas-dialog{display:none!important}}"
    funcs='''    function installHQAtlasStyles(){if(document.getElementById("hqAtlasStyles"))return;const s=document.createElement("style");s.id="hqAtlasStyles";s.textContent='''+json.dumps(css)+''';document.head.append(s)}
    function renderHQAtlas(){const panel=$("#hqAtlasPanel"),item=HQ_ATLAS[state.system],ta=state.lang==="ta";if(!panel)return;if(!item){panel.hidden=true;$("#hqAtlasImage")?.removeAttribute("src");return}panel.hidden=false;$("#hqAtlasKicker").textContent=ta?"உயர்தர உடற்கூறு அட்லஸ்":"High-resolution anatomical atlas";$("#hqAtlasTitle").textContent=ta?item.taTitle:item.enTitle;$("#hqAtlasMeta").textContent=item.width+" × "+item.height+" · "+(ta?"இணையமற்ற உட்பொதிக்கப்பட்ட WebP · மேலுள்ள தொடர்பாடும் வரைபடம் தொடர்ந்து செயலில் உள்ளது":"offline embedded WebP · interactive diagram remains active above");const img=$("#hqAtlasImage");img.src=item.src;img.alt=ta?item.taAlt:item.enAlt;const cap=$("#hqAtlasCaption");cap.textContent="";const lead=document.createElement("strong");lead.textContent=ta?"குறிப்பு: ":"Reference: ";cap.append(lead,document.createTextNode(ta?item.taCaption:item.enCaption));if(item.caution){const c=document.createElement("span");c.className="hq-atlas-caution";c.textContent=ta?"⚠ கண்ட அமைவிடங்களைத் தேர்வுக் கூற்றாகப் பயன்படுத்தும் முன் தொடர்பாடும் சான்றுக் குறிப்பைச் சரிபார்க்கவும்.":"⚠ Before using segment positions as examination facts, check the evidence-aware interactive text.";cap.append(c)}$("#hqAtlasOpenBtn").textContent=ta?"⛶ முழுத் தீர்மானப் படத்தைத் திற":"⛶ Open full-resolution plate"}
    function bindHQAtlas(){const open=$("#hqAtlasOpenBtn"),img=$("#hqAtlasImage"),dlg=$("#hqAtlasDialog"),close=$("#hqAtlasDialogClose"),dlgImg=$("#hqAtlasDialogImage");if(!open||!img||!dlg||!close||!dlgImg)return;const show=()=>{const item=HQ_ATLAS[state.system];if(!item)return;const ta=state.lang==="ta";dlgImg.src=item.src;dlgImg.alt=ta?item.taAlt:item.enAlt;$("#hqAtlasDialogTitle").textContent=ta?item.taTitle:item.enTitle;if(typeof dlg.showModal==="function")dlg.showModal();else dlg.setAttribute("open","")};open.addEventListener("click",show);img.addEventListener("click",show);img.addEventListener("keydown",e=>{if(e.key==="Enter"||e.key===" "){e.preventDefault();show()}});img.tabIndex=0;img.setAttribute("role","button");close.addEventListener("click",()=>safeDialogClose(dlg))}

'''
    html=before(html,'    function renderEnhancements(){',funcs)
    marker='firstRender=false;renderEnhancements();if(selectedId'
    if html.count(marker)!=1: raise SystemExit("renderAll insertion point not unique")
    html=html.replace(marker,'firstRender=false;renderEnhancements();renderHQAtlas();if(selectedId',1)
    init='initSegments();bind();bindEnhancements();if(!storageAvailable())'
    if html.count(init)!=1: raise SystemExit("initialization anchor not unique")
    html=html.replace(init,'initSegments();bind();installHQAtlasStyles();bindEnhancements();bindHQAtlas();if(!storageAvailable())',1)
    for x in invariants:
        if x not in html: raise SystemExit("Hardened invariant changed: "+x[:80])
    if html.count("const HQ_ATLAS=")!=1 or html.count('id="hqAtlasPanel"')!=1 or html.count('id="hqAtlasDialog"')!=1: raise SystemExit("Atlas multiplicity error")

    out=html.encode(); tp.write_bytes(out); csha=sha(out)
    pmanifest={}
    for k in order:
        b=plate[k]; pmanifest[k]={"width":2048,"height":1536,"bytes":len(b),"sha256":sha(b),"provenance":"reconstructed-label-free" if k=="digestive" else "preserved-byte-for-byte-from-historical-HQ-candidate","scientific_status":"requires final plate/device sign-off" if k=="digestive" else "reviewed-for-reuse; final device sign-off pending"}
    manifest={"status":"RELEASE-AUTHORITATIVE CANDIDATE — QA REQUIRED","baseline":{"commit":BASELINE_COMMIT,"html_sha256":BASELINE_SHA,"version":"1.3.8"},"historical_candidate":{"commit":HISTORICAL_COMMIT,"html_sha256":HISTORICAL_SHA,"status":"FROZEN — DO NOT RELEASE"},"candidate":{"html_sha256":csha,"bytes":len(out),"app_version_during_reconstruction":"1.3.8","planned_production_version_after_all_gates":"1.3.9 / 10309"},"plates":pmanifest,"digestive_reconstruction":{"historical_rejected_sha256":HISTORICAL_DIGESTIVE_SHA,"rules":["label-free raster","paired simple smooth caeca","no baked disputed segment numbers","species-aware segment evidence remains in application-rendered text"],"generator":"tools/reconstruct_hq_atlas.py","pillow_version":Image.__version__},"preserved_contracts":["EarthwormApp.pause -> pauseLaboratory","pagehide -> pauseLaboratory","procedure timer cancellation","speech cancellation","storage key earthwormDissection.v1","Back contract","interactive SVG/hotspots","guided learning","assessments","bilingual content","offline CSP"]}
    mp=Path(a.manifest); mp.parent.mkdir(parents=True,exist_ok=True); mp.write_text(json.dumps(manifest,ensure_ascii=False,indent=2)+"\n")
    sp=Path(a.source_manifest); sm=json.loads(sp.read_text()); sm["revised"]["bytes"]=len(out);sm["revised"]["sha256"]=csha;sm["browserLayoutStatus"]="HQ atlas reconstruction candidate on isolated branch; browser/Android/device QA required before version promotion";sm["hqAtlasCandidate"]={"historicalSha256":HISTORICAL_SHA,"candidateSha256":csha,"plateManifest":a.manifest};sp.write_text(json.dumps(sm,ensure_ascii=False,indent=2)+"\n")
    print(json.dumps({"candidate_sha256":csha,"bytes":len(out),"plates":pmanifest},indent=2))

if __name__=="__main__": main()
