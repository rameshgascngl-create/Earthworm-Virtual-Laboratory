#!/usr/bin/env python3
import hashlib, json, re
from pathlib import Path

EXPECTED_INPUT_SHA="2f599d1f5709fec5adf74caa65fec0a1e5aa8e61b527171fb2d23c06da207c5f"
MARKER="/* mobile-system-tabs-v1:"
CSS=r'''
    /* mobile-system-tabs-v1: expose the existing system button navigator on phones. */
    @media(max-width:640px){
      .top-nav{display:block;padding:.5rem .65rem;overflow:hidden}
      #systemSelectLabel,#systemSelect{display:none!important}
      #systemTabs{
        display:flex!important;
        flex-direction:row;
        align-items:stretch;
        gap:.5rem;
        width:100%;
        max-height:none;
        overflow-x:auto;
        overflow-y:hidden;
        padding:.15rem .05rem .4rem;
        scroll-snap-type:x proximity;
        scroll-padding-inline:.1rem;
        scrollbar-width:thin;
        -webkit-overflow-scrolling:touch
      }
      #systemTabs .seg-btn{
        flex:0 0 auto;
        width:auto;
        min-width:max-content;
        min-height:46px;
        justify-content:center;
        white-space:nowrap;
        padding:.55rem .78rem;
        border-radius:999px;
        scroll-snap-align:center
      }
      html[data-lang="ta"] #systemTabs .seg-btn{
        white-space:nowrap;
        line-height:1.45
      }
    }
'''

def sha(b): return hashlib.sha256(b).hexdigest()

root=Path(".")
html_path=root/"app/src/main/assets/index.html"
source_manifest_path=root/"SOURCE-MANIFEST.json"
candidate_manifest_path=root/"review/HQ_ATLAS_RELEASE_CANDIDATE_MANIFEST.json"
baseline_path=root/"tests/release-baseline.json"

raw=html_path.read_bytes()
if sha(raw)!=EXPECTED_INPUT_SHA:
    raise SystemExit(f"input candidate hash mismatch: {sha(raw)}")
html=raw.decode("utf-8")
if MARKER in html:
    raise SystemExit("mobile tab patch already present")
anchor="  </style>\n<!-- Bundled font license"
if html.count(anchor)!=1:
    raise SystemExit(f"static stylesheet anchor count={html.count(anchor)}")
before_html=html
html=html.replace(anchor,CSS+anchor,1)
if html.count(MARKER)!=1:
    raise SystemExit("patch marker count invalid")

# Guard against accidental semantic/source changes outside the single CSS insertion.
reverted=html.replace(CSS,"",1)
if reverted!=before_html:
    raise SystemExit("non-CSS mutation detected")

out=html.encode("utf-8")
new_sha=sha(out)
html_path.write_bytes(out)

styles=[m.group(1).encode("utf-8") for m in re.finditer(r"<style\b[^>]*>([\s\S]*?)</style>",html)]
if len(styles)!=1:
    raise SystemExit(f"expected one static style block, got {len(styles)}")
style_sha=sha(styles[0])

sm=json.loads(source_manifest_path.read_text(encoding="utf-8"))
sm["revised"]["bytes"]=len(out)
sm["revised"]["sha256"]=new_sha
sm["browserLayoutStatus"]="Mobile native system dropdown replaced by existing bilingual scrollable system tabs; regression/device QA required"
sm.setdefault("hqAtlasCandidate",{})["candidateSha256"]=new_sha
sm["mobileSystemNavigation"]={
    "inputSha256":EXPECTED_INPUT_SHA,
    "candidateSha256":new_sha,
    "design":"existing systemTabs exposed as horizontally scrollable mobile pill/tab strip",
    "semanticLogicChanged":False
}
source_manifest_path.write_text(json.dumps(sm,ensure_ascii=False,indent=2)+"\n",encoding="utf-8")

cm=json.loads(candidate_manifest_path.read_text(encoding="utf-8"))
cm["candidate"]["html_sha256"]=new_sha
cm["candidate"]["bytes"]=len(out)
cm["candidate"]["ui_revision"]="mobile horizontal system tabs; native system dropdown hidden on <=640px"
cm["mobile_system_navigation"]={
    "input_sha256":EXPECTED_INPUT_SHA,
    "output_sha256":new_sha,
    "scope":"CSS-only mobile presentation change; existing systemTabs/selectSystem/state logic reused",
    "physical_device_qa_required":True
}
candidate_manifest_path.write_text(json.dumps(cm,ensure_ascii=False,indent=2)+"\n",encoding="utf-8")

rb=json.loads(baseline_path.read_text(encoding="utf-8"))
old_style=list(rb["renderingContext"]["styleSHA256"])
if len(old_style)!=1:
    raise SystemExit("unexpected reviewed stylesheet count")
rb["previousMobileNavigationStyleSHA256"]=old_style
rb["renderingContext"]["styleSHA256"]=[style_sha]
rb["renderingContext"]["version"]="1.3.8-hq-atlas-mobile-system-tabs"
rb["mobileSystemNavigationReview"]={
    "scope":"CSS-only <=640px navigation presentation",
    "inputCandidateSha256":EXPECTED_INPUT_SHA,
    "outputCandidateSha256":new_sha,
    "systemSelectionLogic":"unchanged"
}
baseline_path.write_text(json.dumps(rb,ensure_ascii=False,indent=2)+"\n",encoding="utf-8")

print(json.dumps({"html_sha256":new_sha,"bytes":len(out),"style_sha256":style_sha},indent=2))
