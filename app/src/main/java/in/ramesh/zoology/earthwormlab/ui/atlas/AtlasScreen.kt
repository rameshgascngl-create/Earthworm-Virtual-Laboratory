package `in`.ramesh.zoology.earthwormlab.ui.atlas

import android.content.Context
import android.graphics.drawable.PictureDrawable
import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.caverock.androidsvg.SVG
import `in`.ramesh.zoology.earthwormlab.R
import `in`.ramesh.zoology.earthwormlab.data.ScientificContentRepository
import `in`.ramesh.zoology.earthwormlab.model.AnatomicalStructure
import `in`.ramesh.zoology.earthwormlab.model.EarthwormSystem
import `in`.ramesh.zoology.earthwormlab.model.Hotspot
import `in`.ramesh.zoology.earthwormlab.preferences.AppLanguage

private const val ATLAS_ASPECT_RATIO = 1200f / 560f
private const val HQ_REFERENCE_ASPECT_RATIO = 4f / 3f
private const val MAX_ZOOM = 4f

/**
 * Phase-3A anatomical learning surface.
 *
 * Two visual sources intentionally coexist:
 * 1. INTERACTIVE — the authoritative SVG atlas, with the reviewed 55-structure
 *    dataset and its normalized touch anchors.
 * 2. HQ_REFERENCE — the eight 2048x1536 WebP plates recovered byte-for-byte
 *    from the previous offline wrapped simulator.
 *
 * Hotspots are NOT projected onto the HQ raster plates yet. Their composition
 * differs from the SVG atlas, so reusing the SVG coordinate map would create
 * anatomically false touch targets. Raster calibration remains an explicit
 * later gate. Structure chips and the scientific detail card remain available
 * in reference mode without pretending that the raster has verified hotspots.
 */
@Composable
fun AtlasScreen(
    system: EarthwormSystem,
    language: AppLanguage,
    repository: ScientificContentRepository,
    modifier: Modifier = Modifier,
) {
    require(system != EarthwormSystem.PREPARATION) {
        "Preparation uses PreparationScreen, not AtlasScreen."
    }

    val structures = remember(system, repository) { repository.structuresFor(system) }
    var selectedId by remember(system) { mutableStateOf(structures.firstOrNull()?.id) }
    val selectedStructure = structures.firstOrNull { it.id == selectedId }
    val selectedContent = selectedStructure?.let { repository.contentFor(it.id) }

    var showReference by rememberSaveable(system.dataKey) { mutableStateOf(false) }
    var showLabels by rememberSaveable(system.dataKey) { mutableStateOf(true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp),
    ) {
        Text(
            text = stringResource(R.string.atlas_hint),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FilterChip(
                selected = !showReference,
                onClick = { showReference = false },
                label = { Text(stringResource(R.string.atlas_mode_interactive)) },
            )
            FilterChip(
                selected = showReference,
                onClick = { showReference = true },
                label = { Text(stringResource(R.string.atlas_mode_reference)) },
            )
            if (!showReference) {
                FilterChip(
                    selected = showLabels,
                    onClick = { showLabels = !showLabels },
                    label = {
                        Text(
                            stringResource(
                                if (showLabels) R.string.atlas_labels_visible
                                else R.string.atlas_labels_hidden,
                            )
                        )
                    },
                )
            }
        }

        Text(
            text = stringResource(R.string.atlas_zoom_hint),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )

        if (showReference) {
            ReferenceAtlasPlate(
                system = system,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
            )
            Text(
                text = stringResource(referenceCaptionResource(system)),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            )
            Text(
                text = stringResource(R.string.atlas_reference_notice),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp),
            )
        } else {
            InteractiveAtlasPlate(
                system = system,
                language = language,
                structures = structures,
                repository = repository,
                selectedId = selectedId,
                showLabels = showLabels,
                onStructureSelected = { selectedId = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
            )
        }

        Text(
            text = stringResource(R.string.atlas_structures),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 6.dp),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            structures.forEach { structure ->
                val content = repository.contentFor(structure.id)
                val label = content?.label(language).orEmpty().ifBlank { structure.id }
                FilterChip(
                    selected = structure.id == selectedId,
                    onClick = { selectedId = structure.id },
                    label = { Text(label) },
                )
            }
        }

        if (selectedContent != null) {
            StructureDetailCard(
                content = selectedContent,
                language = language,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            )
        }
    }
}

@Composable
private fun InteractiveAtlasPlate(
    system: EarthwormSystem,
    language: AppLanguage,
    structures: List<AnatomicalStructure>,
    repository: ScientificContentRepository,
    selectedId: String?,
    showLabels: Boolean,
    onStructureSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val rawResId = atlasResource(system, language)
    val context = LocalContext.current
    val svg = remember(rawResId, context, showLabels) {
        loadAtlasSvg(context, rawResId, showLabels)
    }

    var scale by remember(system, language) { mutableFloatStateOf(1f) }
    var pan by remember(system, language) { mutableStateOf(Offset.Zero) }
    val transformState = rememberTransformableState { zoomChange, panChange, _ ->
        val nextScale = (scale * zoomChange).coerceIn(1f, MAX_ZOOM)
        scale = nextScale
        pan = if (nextScale <= 1f) Offset.Zero else pan + panChange
    }

    Card(modifier = modifier) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(ATLAS_ASPECT_RATIO)
                .background(Color(0xFF071B1A))
                .pointerInput(system, language) {
                    detectTapGestures(
                        onDoubleTap = {
                            scale = 1f
                            pan = Offset.Zero
                        },
                    )
                },
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = pan.x,
                        translationY = pan.y,
                    )
                    .transformable(transformState),
            ) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { viewContext ->
                        ImageView(viewContext).apply {
                            scaleType = ImageView.ScaleType.FIT_CENTER
                            adjustViewBounds = true
                            setLayerType(View.LAYER_TYPE_SOFTWARE, null)
                        }
                    },
                    update = { imageView ->
                        imageView.setImageDrawable(PictureDrawable(svg.renderToPicture()))
                    },
                )

                structures.forEach { structure ->
                    val (x, y) = structure.hotspot.anchor()
                    val label = repository.contentFor(structure.id)?.label(language).orEmpty()
                        .ifBlank { structure.id }
                    val selected = structure.id == selectedId
                    val touchSize = 44.dp
                    val xOffset = (maxWidth * x - touchSize / 2)
                        .coerceIn(0.dp, (maxWidth - touchSize).coerceAtLeast(0.dp))
                    val yOffset = (maxHeight * y - touchSize / 2)
                        .coerceIn(0.dp, (maxHeight - touchSize).coerceAtLeast(0.dp))

                    Box(
                        modifier = Modifier
                            .size(touchSize)
                            .align(Alignment.TopStart)
                            .offset(x = xOffset, y = yOffset)
                            .semantics { contentDescription = label }
                            .clickable { onStructureSelected(structure.id) },
                        contentAlignment = Alignment.Center,
                    ) {
                        if (selected) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.24f),
                                        shape = CircleShape,
                                    )
                                    .border(
                                        width = 2.dp,
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = CircleShape,
                                    ),
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(if (selected) 18.dp else 13.dp)
                                .background(
                                    color = if (selected) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.tertiary
                                    },
                                    shape = CircleShape,
                                )
                                .border(
                                    width = 2.dp,
                                    color = Color.White,
                                    shape = CircleShape,
                                ),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReferenceAtlasPlate(
    system: EarthwormSystem,
    modifier: Modifier = Modifier,
) {
    var scale by remember(system) { mutableFloatStateOf(1f) }
    var pan by remember(system) { mutableStateOf(Offset.Zero) }
    val transformState = rememberTransformableState { zoomChange, panChange, _ ->
        val nextScale = (scale * zoomChange).coerceIn(1f, MAX_ZOOM)
        scale = nextScale
        pan = if (nextScale <= 1f) Offset.Zero else pan + panChange
    }

    Card(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(HQ_REFERENCE_ASPECT_RATIO)
                .background(Color(0xFF071B1A))
                .pointerInput(system) {
                    detectTapGestures(
                        onDoubleTap = {
                            scale = 1f
                            pan = Offset.Zero
                        },
                    )
                },
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = pan.x,
                        translationY = pan.y,
                    )
                    .transformable(transformState),
            ) {
                Image(
                    painter = painterResource(hqAtlasResource(system)),
                    contentDescription = stringResource(R.string.atlas_reference_content_description),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

@Composable
private fun StructureDetailCard(
    content: ScientificContentRepository.StructureContent,
    language: AppLanguage,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.atlas_selected),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = content.label(language),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(top = 2.dp, bottom = 12.dp),
            )

            DetailField(
                title = stringResource(R.string.atlas_location),
                value = content.location(language),
            )
            DetailField(
                title = stringResource(R.string.atlas_function),
                value = content.function(language),
            )
            DetailField(
                title = stringResource(R.string.atlas_significance),
                value = content.significance(language),
            )
            DetailField(
                title = stringResource(R.string.atlas_correction),
                value = content.correction(language),
            )
        }
    }
}

@Composable
private fun DetailField(
    title: String,
    value: String,
) {
    if (value.isBlank()) return
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
    )
    Text(
        text = value,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(top = 2.dp, bottom = 10.dp),
    )
}

private fun ScientificContentRepository.StructureContent.label(language: AppLanguage): String =
    if (language == AppLanguage.TAMIL) ta else en

private fun ScientificContentRepository.StructureContent.location(language: AppLanguage): String =
    if (language == AppLanguage.TAMIL) locTa else locEn

private fun ScientificContentRepository.StructureContent.function(language: AppLanguage): String =
    if (language == AppLanguage.TAMIL) fnTa else fnEn

private fun ScientificContentRepository.StructureContent.significance(language: AppLanguage): String =
    if (language == AppLanguage.TAMIL) sigTa else sigEn

private fun ScientificContentRepository.StructureContent.correction(language: AppLanguage): String =
    if (language == AppLanguage.TAMIL) fixTa else fixEn

private fun Hotspot.anchor(): Pair<Float, Float> = when (this) {
    is Hotspot.Point -> xFraction to yFraction
    is Hotspot.Rect -> (xFraction + widthFraction / 2f) to (yFraction + heightFraction / 2f)
    is Hotspot.Polygon -> {
        if (points.isEmpty()) {
            0.5f to 0.5f
        } else {
            points.map { it.first }.average().toFloat() to
                points.map { it.second }.average().toFloat()
        }
    }
}

private fun loadAtlasSvg(
    context: Context,
    @RawRes rawResId: Int,
    showLabels: Boolean,
): SVG {
    val source = context.resources.openRawResource(rawResId)
        .bufferedReader(Charsets.UTF_8)
        .use { it.readText() }

    val labelVisibilityRule = if (showLabels) {
        ""
    } else {
        ".organ-label,.micro-label,.orientation-note{display:none!important}"
    }

    val styled = source.replaceFirst(
        "<defs>",
        """<defs>
<style type="text/css"><![CDATA[
.hit{fill:transparent;stroke:transparent}
.layer{display:none}
.layer.active{display:inline}
.external-view{display:none}
.external-view.active{display:inline}
.segment-line{stroke:#f0c6a8;stroke-width:1;opacity:.28}
.segment-shadow{stroke:#2a1210;stroke-width:1.4;opacity:.4}
.organ-label{font-size:15px;font-weight:bold;fill:#f6fff9;stroke:#061515;stroke-width:3;stroke-linejoin:round}
.micro-label{font-size:11px;fill:#e7f5ef;stroke:#061515;stroke-width:2}
.orientation-note{font-size:13px;font-weight:bold;fill:#fff3c5;stroke:#061515;stroke-width:3}
.flap{fill:#835d4f;stroke:#c18b74;stroke-width:2;opacity:.44}
.surface-detail{pointer-events:none}
$labelVisibilityRule
]]></style>"""
    )
    return SVG.getFromString(styled)
}

@RawRes
private fun atlasResource(
    system: EarthwormSystem,
    language: AppLanguage,
): Int = when (system) {
    EarthwormSystem.EXTERNAL ->
        if (language == AppLanguage.TAMIL) R.raw.atlas_external_ta else R.raw.atlas_external_en
    EarthwormSystem.DIGESTIVE ->
        if (language == AppLanguage.TAMIL) R.raw.atlas_digestive_ta else R.raw.atlas_digestive_en
    EarthwormSystem.CIRCULATORY ->
        if (language == AppLanguage.TAMIL) R.raw.atlas_circulatory_ta else R.raw.atlas_circulatory_en
    EarthwormSystem.RESPIRATORY ->
        if (language == AppLanguage.TAMIL) R.raw.atlas_respiratory_ta else R.raw.atlas_respiratory_en
    EarthwormSystem.EXCRETORY ->
        if (language == AppLanguage.TAMIL) R.raw.atlas_excretory_ta else R.raw.atlas_excretory_en
    EarthwormSystem.REPRODUCTIVE ->
        if (language == AppLanguage.TAMIL) R.raw.atlas_reproductive_ta else R.raw.atlas_reproductive_en
    EarthwormSystem.NERVOUS ->
        if (language == AppLanguage.TAMIL) R.raw.atlas_nervous_ta else R.raw.atlas_nervous_en
    EarthwormSystem.TRANSVERSE_SECTION ->
        if (language == AppLanguage.TAMIL) R.raw.atlas_crosssection_ta else R.raw.atlas_crosssection_en
    EarthwormSystem.PREPARATION -> error("Preparation has no anatomical atlas resource.")
}

@DrawableRes
private fun hqAtlasResource(system: EarthwormSystem): Int = when (system) {
    EarthwormSystem.EXTERNAL -> R.drawable.hq_external
    EarthwormSystem.DIGESTIVE -> R.drawable.hq_digestive
    EarthwormSystem.CIRCULATORY -> R.drawable.hq_circulatory
    EarthwormSystem.RESPIRATORY -> R.drawable.hq_respiratory
    EarthwormSystem.EXCRETORY -> R.drawable.hq_excretory
    EarthwormSystem.REPRODUCTIVE -> R.drawable.hq_reproductive
    EarthwormSystem.NERVOUS -> R.drawable.hq_nervous
    EarthwormSystem.TRANSVERSE_SECTION -> R.drawable.hq_crosssection
    EarthwormSystem.PREPARATION -> error("Preparation has no HQ anatomical reference plate.")
}

@StringRes
private fun referenceCaptionResource(system: EarthwormSystem): Int = when (system) {
    EarthwormSystem.EXTERNAL -> R.string.atlas_reference_caption_external
    EarthwormSystem.DIGESTIVE -> R.string.atlas_reference_caption_digestive
    EarthwormSystem.CIRCULATORY -> R.string.atlas_reference_caption_circulatory
    EarthwormSystem.RESPIRATORY -> R.string.atlas_reference_caption_respiratory
    EarthwormSystem.EXCRETORY -> R.string.atlas_reference_caption_excretory
    EarthwormSystem.REPRODUCTIVE -> R.string.atlas_reference_caption_reproductive
    EarthwormSystem.NERVOUS -> R.string.atlas_reference_caption_nervous
    EarthwormSystem.TRANSVERSE_SECTION -> R.string.atlas_reference_caption_crosssection
    EarthwormSystem.PREPARATION -> error("Preparation has no HQ anatomical reference caption.")
}
