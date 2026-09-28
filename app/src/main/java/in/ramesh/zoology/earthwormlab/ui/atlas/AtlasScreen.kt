package `in`.ramesh.zoology.earthwormlab.ui.atlas

import android.content.Context
import android.graphics.drawable.PictureDrawable
import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
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
private const val HQ_ASPECT_RATIO = 4f / 3f

/**
 * Phase-2 native anatomical atlas.
 *
 * The plate itself is rendered through AndroidSVG into an Android ImageView;
 * there is no browser runtime, HTML, JavaScript or network dependency. Interaction is
 * provided by Compose using the normalized hotspot coordinates already
 * validated against the frozen reference dataset.
 *
 * Scientific prose is not duplicated here. The detail card reads directly
 * from [ScientificContentRepository], which loads earthworm_content_v138.json.
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
    var selectedId by rememberSaveable(system.dataKey) { mutableStateOf(structures.firstOrNull()?.id) }
    var detailedMode by rememberSaveable(system.dataKey, "atlas-detailed-mode") { mutableStateOf(false) }
    val selectedStructure = structures.firstOrNull { it.id == selectedId }
    val selectedContent = selectedStructure?.let { repository.contentFor(it.id) }
    val narrator = rememberStructureNarrator()
    var audioEnabled by rememberSaveable("atlas-structure-audio") { mutableStateOf(true) }
    val audioLocationCue = stringResource(R.string.atlas_audio_location)
    val audioFunctionCue = stringResource(R.string.atlas_audio_function)
    val audioTeachingCue = stringResource(R.string.atlas_audio_teaching)
    val audioCorrectionCue = stringResource(R.string.atlas_audio_correction)
    val processCue = stringResource(R.string.atlas_audio_process)

    val processDefinition = remember(system) { ProcessGuidance.forSystem(system) }
    val processTitleText = processDefinition?.let { processTitle(it.process) }.orEmpty()
    var processMode by rememberSaveable(system.dataKey, "atlas-process-mode") { mutableStateOf(false) }
    var processPlaying by rememberSaveable(system.dataKey, "atlas-process-playing") { mutableStateOf(false) }
    var processIndex by rememberSaveable(system.dataKey, "atlas-process-index") { mutableStateOf(0) }
    var comparisonVisible by rememberSaveable(system.dataKey, "atlas-nephridial-comparison") { mutableStateOf(false) }
    var processReplayToken by rememberSaveable(system.dataKey, "atlas-process-replay-token") { mutableStateOf(0) }
    var processNarrationStarted by rememberSaveable(system.dataKey, "atlas-process-narration-started") { mutableStateOf(false) }
    var processAwaitingNarration by rememberSaveable(system.dataKey, "atlas-process-awaiting-narration") { mutableStateOf(false) }
    val currentProcessStage = processDefinition?.stages?.let { stages ->
        if (stages.isEmpty()) null else stages[processIndex.coerceIn(0, stages.lastIndex)]
    }

    val selectStructure: (String) -> Unit = { structureId ->
        selectedId = structureId
        if (audioEnabled) {
            repository.contentFor(structureId)?.let { content ->
                narrator.speak(
                    content.briefNarration(
                        language = language,
                        locationCue = audioLocationCue,
                        functionCue = audioFunctionCue,
                    ),
                    language,
                )
            }
        }
    }

    LaunchedEffect(system) {
        processMode = false
        processPlaying = false
        processIndex = 0
        comparisonVisible = false
        processReplayToken = 0
        processNarrationStarted = false
        processAwaitingNarration = false
        narrator.stop()
    }

    LaunchedEffect(language) {
        narrator.stop()
        if (processMode) processReplayToken += 1
    }

    LaunchedEffect(detailedMode) {
        if (detailedMode && processMode) {
            processMode = false
            processPlaying = false
            processNarrationStarted = false
            processAwaitingNarration = false
        }
        narrator.stop()
    }

    LaunchedEffect(
        processMode,
        processIndex,
        processReplayToken,
        language,
        audioEnabled,
        processDefinition,
    ) {
        val definition = processDefinition
        if (processMode && definition != null && definition.stages.isNotEmpty()) {
            val safeIndex = processIndex.coerceIn(0, definition.stages.lastIndex)
            val stage = definition.stages[safeIndex]
            val target = stage.target
            if (structures.any { it.id == target }) {
                selectedId = target
                processNarrationStarted = false
                processAwaitingNarration = false
                if (audioEnabled) {
                    repository.contentFor(target)?.let { content ->
                        processAwaitingNarration = true
                        narrator.speak(
                            content.processNarration(
                                language = language,
                                processCue = processCue,
                                processTitle = processTitleText,
                                stageTitle = stage.title(language),
                                functionCue = audioFunctionCue,
                                mechanism = stage.mechanism(language),
                            ),
                            language,
                        )
                    }
                }
            }
        }
    }

    LaunchedEffect(
        narrator.state,
        processPlaying,
        processMode,
        processAwaitingNarration,
        processNarrationStarted,
        audioEnabled,
        processDefinition,
        processIndex,
    ) {
        if (!processPlaying || !processMode || !audioEnabled) return@LaunchedEffect
        val definition = processDefinition ?: return@LaunchedEffect
        if (definition.stages.isEmpty()) return@LaunchedEffect

        when (narrator.state) {
            NarrationState.SPEAKING -> {
                if (processAwaitingNarration) processNarrationStarted = true
            }
            NarrationState.READY -> {
                if (processAwaitingNarration && processNarrationStarted) {
                    processAwaitingNarration = false
                    processNarrationStarted = false
                    delay(350)
                    processIndex =
                        if (processIndex >= definition.stages.lastIndex) 0 else processIndex + 1
                }
            }
            NarrationState.UNAVAILABLE,
            NarrationState.ERROR -> {
                processAwaitingNarration = false
                processNarrationStarted = false
                delay(2400)
                processIndex =
                    if (processIndex >= definition.stages.lastIndex) 0 else processIndex + 1
            }
            NarrationState.INITIALIZING -> Unit
        }
    }

    LaunchedEffect(processPlaying, processMode, audioEnabled, processDefinition, processIndex) {
        val definition = processDefinition ?: return@LaunchedEffect
        if (
            processPlaying &&
            processMode &&
            !audioEnabled &&
            definition.stages.isNotEmpty()
        ) {
            delay(2400)
            processIndex =
                if (processIndex >= definition.stages.lastIndex) 0 else processIndex + 1
        }
    }

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
                selected = !detailedMode,
                onClick = { detailedMode = false },
                label = { Text(stringResource(R.string.atlas_mode_svg)) },
            )
            FilterChip(
                selected = detailedMode,
                onClick = {
                    if (!processMode) detailedMode = true
                },
                enabled = !processMode,
                label = { Text(stringResource(R.string.atlas_mode_detailed)) },
            )
            FilterChip(
                selected = audioEnabled,
                onClick = {
                    audioEnabled = !audioEnabled
                    if (!audioEnabled) narrator.stop()
                },
                label = { Text(stringResource(R.string.atlas_audio)) },
            )
            if (processDefinition != null) {
                FilterChip(
                    selected = processMode,
                    onClick = {
                        val enabling = !processMode
                        processMode = enabling
                        processPlaying = false
                        processNarrationStarted = false
                        processAwaitingNarration = false
                        narrator.stop()
                        if (enabling) {
                            processIndex = 0
                            processReplayToken += 1
                            detailedMode = false
                        }
                    },
                    label = { Text(stringResource(R.string.atlas_process_mode)) },
                )
            }
        }

        Text(
            text = stringResource(R.string.atlas_mode_caption),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )

        if (audioEnabled && narrator.state != NarrationState.READY) {
            val statusText = when (narrator.state) {
                NarrationState.INITIALIZING -> stringResource(R.string.atlas_audio_initializing)
                NarrationState.SPEAKING -> stringResource(R.string.atlas_audio_speaking)
                NarrationState.UNAVAILABLE -> stringResource(R.string.atlas_audio_unavailable)
                NarrationState.ERROR -> stringResource(R.string.atlas_audio_error)
                NarrationState.READY -> ""
            }
            if (statusText.isNotBlank()) {
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (
                        narrator.state == NarrationState.UNAVAILABLE ||
                        narrator.state == NarrationState.ERROR
                    ) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.primary
                    },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp),
                )
            }
        }

        if (processMode && processDefinition != null && currentProcessStage != null) {
            val currentTarget = currentProcessStage.target
            val currentContent = repository.contentFor(currentTarget)
            val currentLabel = currentProcessStage.title(language)
            val currentFunction = currentContent?.function(language).orEmpty()
            val currentMechanism = currentProcessStage.mechanism(language)
            val currentCue = currentProcessStage.cue(language)
            val currentPhase = processPhaseTitle(currentProcessStage.phase)

            OutlinedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = processTitleText,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = currentPhase,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.padding(top = 6.dp),
                    )
                    Text(
                        text = currentLabel,
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                    if (currentFunction.isNotBlank()) {
                        Text(
                            text = currentFunction,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 4.dp),
                        )
                    }
                    if (currentMechanism.isNotBlank()) {
                        Text(
                            text = currentMechanism,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 6.dp),
                        )
                    }
                    if (currentCue.isNotBlank()) {
                        Text(
                            text = stringResource(R.string.atlas_process_direction, currentCue),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 6.dp),
                        )
                    }
                    Text(
                        text = stringResource(R.string.atlas_process_notice),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 6.dp, bottom = 8.dp),
                    )
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        AssistChip(
                            onClick = {
                                processPlaying = false
                                processNarrationStarted = false
                                processAwaitingNarration = false
                                narrator.stop()
                                processIndex =
                                    if (processIndex <= 0) processDefinition.stages.lastIndex
                                    else processIndex - 1
                                processReplayToken += 1
                            },
                            label = { Text(stringResource(R.string.atlas_process_previous)) },
                        )
                        AssistChip(
                            onClick = {
                                if (processPlaying) {
                                    processPlaying = false
                                    processNarrationStarted = false
                                    processAwaitingNarration = false
                                    narrator.stop()
                                } else {
                                    processPlaying = true
                                    processReplayToken += 1
                                }
                            },
                            label = {
                                Text(
                                    stringResource(
                                        if (processPlaying) R.string.atlas_process_pause
                                        else R.string.atlas_process_play
                                    )
                                )
                            },
                        )
                        AssistChip(
                            onClick = {
                                processPlaying = false
                                processNarrationStarted = false
                                processAwaitingNarration = false
                                narrator.stop()
                                processIndex =
                                    if (processIndex >= processDefinition.stages.lastIndex) 0
                                    else processIndex + 1
                                processReplayToken += 1
                            },
                            label = { Text(stringResource(R.string.atlas_process_next)) },
                        )
                        AssistChip(
                            onClick = {
                                processPlaying = false
                                processNarrationStarted = false
                                processAwaitingNarration = false
                                narrator.stop()
                                processIndex = 0
                                processReplayToken += 1
                            },
                            label = { Text(stringResource(R.string.atlas_process_restart)) },
                        )
                        if (processDefinition.process == BiologicalProcess.NEPHRIDIAL_EXCRETION) {
                            AssistChip(
                                onClick = {
                                    comparisonVisible = !comparisonVisible
                                    processPlaying = false
                                    narrator.stop()
                                },
                                label = {
                                    Text(
                                        stringResource(
                                            if (comparisonVisible) R.string.atlas_nephridial_compare_hide
                                            else R.string.atlas_nephridial_compare_show
                                        )
                                    )
                                },
                            )
                        }
                    }
                }
            }
        }

        if (
            processMode &&
            comparisonVisible &&
            processDefinition?.process == BiologicalProcess.NEPHRIDIAL_EXCRETION
        ) {
            NephridialComparisonCard(
                repository = repository,
                language = language,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            )
        }

        if (detailedMode) {
            DetailedImagePlate(
                system = system,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
            )
        } else {
            AtlasPlate(
                system = system,
                language = language,
                structures = structures,
                repository = repository,
                selectedId = selectedId,
                onStructureSelected = selectStructure,
                processStage = if (processMode) currentProcessStage else null,
                processPlaying = processPlaying,
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
                    onClick = { selectStructure(structure.id) },
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

            if (audioEnabled) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    AssistChip(
                        onClick = {
                            narrator.speak(
                                selectedContent.extendedNarration(
                                    language = language,
                                    teachingCue = audioTeachingCue,
                                    correctionCue = audioCorrectionCue,
                                ),
                                language,
                            )
                        },
                        label = { Text(stringResource(R.string.atlas_audio_more)) },
                    )
                    if (narrator.state == NarrationState.SPEAKING) {
                        AssistChip(
                            onClick = { narrator.stop() },
                            label = { Text(stringResource(R.string.atlas_audio_stop)) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailedImagePlate(
    system: EarthwormSystem,
    modifier: Modifier = Modifier,
) {
    val drawableResId = detailedImageResource(system)

    Card(modifier = modifier) {
        Image(
            painter = painterResource(drawableResId),
            contentDescription = stringResource(R.string.atlas_mode_detailed),
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(HQ_ASPECT_RATIO)
                .background(Color(0xFF071B1A)),
        )
    }
}

@Composable
private fun AtlasPlate(
    system: EarthwormSystem,
    language: AppLanguage,
    structures: List<AnatomicalStructure>,
    repository: ScientificContentRepository,
    selectedId: String?,
    onStructureSelected: (String) -> Unit,
    processStage: ProcessStage?,
    processPlaying: Boolean,
    modifier: Modifier = Modifier,
) {
    val rawResId = atlasResource(system, language)
    val context = LocalContext.current
    var scale by rememberSaveable(system.dataKey, "atlas-scale") { mutableStateOf(1f) }
    var panX by rememberSaveable(system.dataKey, "atlas-pan-x") { mutableStateOf(0f) }
    var panY by rememberSaveable(system.dataKey, "atlas-pan-y") { mutableStateOf(0f) }
    var labelsVisible by rememberSaveable(system.dataKey, "atlas-labels") { mutableStateOf(true) }
    var orientationMode by rememberSaveable(system.dataKey, "atlas-orientation-mode") { mutableStateOf(false) }
    var focusMode by rememberSaveable(system.dataKey, "atlas-focus-mode") { mutableStateOf(false) }
    val semanticZoomTier = semanticZoomTier(scale)
    val processTransition = rememberInfiniteTransition(label = "process-pulse")
    val processPulse by processTransition.animateFloat(
        initialValue = 0.72f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "process-pulse-alpha",
    )

    val svg = remember(
        rawResId,
        context,
        labelsVisible,
        semanticZoomTier,
    ) {
        loadAtlasSvg(
            context = context,
            rawResId = rawResId,
            labelsVisible = labelsVisible,
            semanticZoomTier = semanticZoomTier,
        )
    }

    Card(modifier = modifier) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                FilterChip(
                    selected = labelsVisible,
                    onClick = { labelsVisible = !labelsVisible },
                    label = { Text(stringResource(R.string.atlas_labels)) },
                )
                if (system != EarthwormSystem.RESPIRATORY) {
                    FilterChip(
                        selected = orientationMode,
                        onClick = { orientationMode = !orientationMode },
                        label = { Text(stringResource(R.string.atlas_orientation_mode)) },
                    )
                }
                FilterChip(
                    selected = focusMode,
                    onClick = { focusMode = !focusMode },
                    label = { Text(stringResource(R.string.atlas_focus_mode)) },
                )
                AssistChip(
                    onClick = {},
                    enabled = false,
                    label = {
                        Text(
                            stringResource(
                                when (semanticZoomTier) {
                                    SemanticZoomTier.OVERVIEW -> R.string.atlas_zoom_overview
                                    SemanticZoomTier.DETAIL -> R.string.atlas_zoom_detail
                                }
                            )
                        )
                    },
                )
                AssistChip(
                    onClick = {
                        scale = 1f
                        panX = 0f
                        panY = 0f
                    },
                    label = { Text(stringResource(R.string.atlas_reset_view)) },
                )
            }

            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(ATLAS_ASPECT_RATIO)
                    .background(Color(0xFF071B1A))
                    .clipToBounds()
                    .pointerInput(system) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            val nextScale = (scale * zoom).coerceIn(1f, 4f)
                            if (nextScale <= 1.001f) {
                                scale = 1f
                                panX = 0f
                                panY = 0f
                            } else {
                                scale = nextScale
                                val maxPanX = size.width * (nextScale - 1f) / 2f
                                val maxPanY = size.height * (nextScale - 1f) / 2f
                                panX = (panX + pan.x).coerceIn(-maxPanX, maxPanX)
                                panY = (panY + pan.y).coerceIn(-maxPanY, maxPanY)
                            }
                        }
                    },
            ) {
                val plateWidth = maxWidth
                val plateHeight = maxHeight
                val transformModifier =
                    if (scale <= 1.001f && panX == 0f && panY == 0f) {
                        Modifier
                    } else {
                        Modifier.graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                            translationX = panX
                            translationY = panY
                        }
                    }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .then(transformModifier),
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
                        val markerAlpha =
                            if (focusMode && selectedId != null && !selected) 0.28f else 1f
                        val touchSize = 48.dp
                        val xOffset = (plateWidth * x - touchSize / 2)
                            .coerceIn(0.dp, (plateWidth - touchSize).coerceAtLeast(0.dp))
                        val yOffset = (plateHeight * y - touchSize / 2)
                            .coerceIn(0.dp, (plateHeight - touchSize).coerceAtLeast(0.dp))
                        Box(
                            modifier = Modifier
                                .size(touchSize)
                                .align(Alignment.TopStart)
                                .offset(x = xOffset, y = yOffset)
                                .semantics { contentDescription = label }
                                .clickable { onStructureSelected(structure.id) }
                                .graphicsLayer { alpha = markerAlpha },
                            contentAlignment = Alignment.Center,
                        ) {
                            if (selected && focusMode) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .graphicsLayer {
                                            scaleX = 1f / scale
                                            scaleY = 1f / scale
                                        }
                                        .border(
                                            width = 3.dp,
                                            color = MaterialTheme.colorScheme.tertiary,
                                            shape = CircleShape,
                                        ),
                                )
                            }
                            if (selected) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .background(
                                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
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
                                    .size(if (selected) 12.dp else 10.dp)
                                    .background(
                                        color = if (selected) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            Color.Transparent
                                        },
                                        shape = CircleShape,
                                    )
                                    .border(
                                        width = if (selected) 2.dp else 1.dp,
                                        color = if (selected) {
                                            Color.White
                                        } else {
                                            Color.White.copy(alpha = 0.82f)
                                        },
                                        shape = CircleShape,
                                    ),
                            )
                        }
                    }

                    processStage?.let { stage ->
                        structures.firstOrNull { it.id == stage.target }?.let { active ->
                            val (x, y) = active.hotspot.anchor()
                            val markerSize = 64.dp
                            val xOffset = (plateWidth * x - markerSize / 2)
                                .coerceIn(0.dp, (plateWidth - markerSize).coerceAtLeast(0.dp))
                            val yOffset = (plateHeight * y - markerSize / 2)
                                .coerceIn(0.dp, (plateHeight - markerSize).coerceAtLeast(0.dp))
                            Box(
                                modifier = Modifier
                                    .size(markerSize)
                                    .align(Alignment.TopStart)
                                    .offset(x = xOffset, y = yOffset)
                                    .graphicsLayer {
                                        alpha = if (processPlaying) processPulse else 0.9f
                                        val pulseScale =
                                            if (processPlaying) 0.92f + 0.08f * processPulse else 1f
                                        scaleX = pulseScale / scale
                                        scaleY = pulseScale / scale
                                    }
                                    .border(
                                        width = 3.dp,
                                        color = MaterialTheme.colorScheme.tertiary,
                                        shape = CircleShape,
                                    ),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = processDirectionGlyph(stage.direction),
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.tertiary,
                                )
                            }
                        }
                    }
                }

                if (orientationMode) {
                    SpatialOrientationOverlay(
                        system = system,
                        language = language,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }
    }
}

private enum class SemanticZoomTier {
    OVERVIEW,
    DETAIL,
}

private fun semanticZoomTier(scale: Float): SemanticZoomTier =
    if (scale < 1.7f) SemanticZoomTier.OVERVIEW else SemanticZoomTier.DETAIL

@Composable
private fun SpatialOrientationOverlay(
    system: EarthwormSystem,
    language: AppLanguage,
    modifier: Modifier = Modifier,
) {
    if (system == EarthwormSystem.RESPIRATORY) return

    Box(modifier = modifier.padding(8.dp)) {
        when (system) {
            EarthwormSystem.TRANSVERSE_SECTION -> {
                Text(
                    text = if (language == AppLanguage.TAMIL) "முதுகுப்புறம் ↑" else "Dorsal ↑",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.align(Alignment.TopCenter),
                )
                Text(
                    text = if (language == AppLanguage.TAMIL) "வயிற்றுப்புறம் ↓" else "Ventral ↓",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.align(Alignment.BottomCenter),
                )
            }

            EarthwormSystem.EXTERNAL,
            EarthwormSystem.DIGESTIVE,
            EarthwormSystem.CIRCULATORY,
            EarthwormSystem.EXCRETORY,
            EarthwormSystem.REPRODUCTIVE,
            EarthwormSystem.NERVOUS -> {
                Text(
                    text = if (language == AppLanguage.TAMIL) "← முன்முனை" else "← Anterior",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.align(Alignment.TopStart),
                )
                Text(
                    text = if (language == AppLanguage.TAMIL) "பின்முனை →" else "Posterior →",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.align(Alignment.TopEnd),
                )
                if (system == EarthwormSystem.EXTERNAL) {
                    Text(
                        text = if (language == AppLanguage.TAMIL) {
                            "கண்ட வழிகாட்டி · கிளிட்டெல்லம் XIV–XVI"
                        } else {
                            "Segment guide · clitellum XIV–XVI"
                        },
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.align(Alignment.BottomCenter),
                    )
                }
            }

            EarthwormSystem.RESPIRATORY,
            EarthwormSystem.PREPARATION -> Unit
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

private fun ScientificContentRepository.StructureContent.briefNarration(
    language: AppLanguage,
    locationCue: String,
    functionCue: String,
): String =
    listOf(
        label(language),
        location(language).takeIf { it.isNotBlank() }?.let { "$locationCue: $it" },
        function(language).takeIf { it.isNotBlank() }?.let { "$functionCue: $it" },
    )
        .filterNotNull()
        .map { it.trim().trimEnd('.') }
        .filter { it.isNotBlank() }
        .joinToString(". ", postfix = ".")

private fun ScientificContentRepository.StructureContent.extendedNarration(
    language: AppLanguage,
    teachingCue: String,
    correctionCue: String,
): String =
    listOf(
        significance(language).takeIf { it.isNotBlank() }?.let { "$teachingCue: $it" },
        correction(language).takeIf { it.isNotBlank() }?.let { "$correctionCue: $it" },
    )
        .filterNotNull()
        .map { it.trim().trimEnd('.') }
        .filter { it.isNotBlank() }
        .joinToString(". ", postfix = ".")

private fun ScientificContentRepository.StructureContent.processNarration(
    language: AppLanguage,
    processCue: String,
    processTitle: String,
    stageTitle: String,
    functionCue: String,
    mechanism: String,
): String =
    listOf(
        processCue + ": " + processTitle,
        stageTitle,
        mechanism.takeIf { it.isNotBlank() },
        function(language).takeIf { it.isNotBlank() }?.let { functionCue + ": " + it },
        significance(language).takeIf { it.isNotBlank() },
    )
        .filterNotNull()
        .map { it.trim().trimEnd('.') }
        .filter { it.isNotBlank() }
        .joinToString(". ", postfix = ".")

@Composable
private fun NephridialComparisonCard(
    repository: ScientificContentRepository,
    language: AppLanguage,
    modifier: Modifier = Modifier,
) {
    val comparisonIds = listOf(
        "pharyngeal-nephridia",
        "septal-nephridia",
        "integumentary-nephridia",
    )

    OutlinedCard(modifier = modifier) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = stringResource(R.string.atlas_nephridial_compare_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = stringResource(R.string.atlas_nephridial_compare_intro),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp, bottom = 6.dp),
            )
            comparisonIds.forEachIndexed { index, id ->
                repository.contentFor(id)?.let { content ->
                    Text(
                        text = content.label(language),
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(top = if (index == 0) 4.dp else 12.dp),
                    )
                    Text(
                        text = stringResource(R.string.atlas_location) + ": " + content.location(language),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                    Text(
                        text = stringResource(R.string.atlas_function) + ": " + content.function(language),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                    Text(
                        text = stringResource(R.string.atlas_nephridial_discharge) + ": " +
                            content.significance(language),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun processTitle(process: BiologicalProcess): String = when (process) {
    BiologicalProcess.CUTANEOUS_RESPIRATION ->
        stringResource(R.string.atlas_process_cutaneous_respiration)
    BiologicalProcess.BLOOD_CIRCULATION ->
        stringResource(R.string.atlas_process_blood_circulation)
    BiologicalProcess.DIGESTIVE_JOURNEY ->
        stringResource(R.string.atlas_process_digestive_journey)
    BiologicalProcess.NEPHRIDIAL_EXCRETION ->
        stringResource(R.string.atlas_process_nephridial_excretion)
    BiologicalProcess.NERVOUS_CONDUCTION ->
        stringResource(R.string.atlas_process_nervous_conduction)
}

@Composable
private fun processPhaseTitle(phase: ProcessPhase): String = when (phase) {
    ProcessPhase.GAS_EXCHANGE -> stringResource(R.string.atlas_process_phase_gas_exchange)
    ProcessPhase.DISTRIBUTION -> stringResource(R.string.atlas_process_phase_distribution)
    ProcessPhase.COLLECTION_NETWORK -> stringResource(R.string.atlas_process_phase_collection)
    ProcessPhase.INGESTION_TRANSPORT -> stringResource(R.string.atlas_process_phase_ingestion_transport)
    ProcessPhase.MECHANICAL_DIGESTION -> stringResource(R.string.atlas_process_phase_mechanical_digest)
    ProcessPhase.CHEMICAL_DIGESTION -> stringResource(R.string.atlas_process_phase_chemical_digest)
    ProcessPhase.INTESTINAL_PROCESSING -> stringResource(R.string.atlas_process_phase_intestinal_processing)
    ProcessPhase.NEPHRIDIAL_PROCESSING -> stringResource(R.string.atlas_process_phase_nephridial_processing)
    ProcessPhase.EXCRETORY_ROUTE_COMPARISON -> stringResource(R.string.atlas_process_phase_excretory_routes)
    ProcessPhase.SENSORY_INTEGRATION -> stringResource(R.string.atlas_process_phase_sensory_integration)
    ProcessPhase.NERVE_RING_RELAY -> stringResource(R.string.atlas_process_phase_nerve_ring_relay)
    ProcessPhase.LONGITUDINAL_CONDUCTION -> stringResource(R.string.atlas_process_phase_longitudinal_conduction)
    ProcessPhase.SEGMENTAL_RESPONSE -> stringResource(R.string.atlas_process_phase_segmental_response)
}

private fun processDirectionGlyph(direction: ProcessDirection): String = when (direction) {
    ProcessDirection.INWARD -> "⇢"
    ProcessDirection.OUTWARD -> "⇠"
    ProcessDirection.BIDIRECTIONAL_EXCHANGE -> "⇄"
    ProcessDirection.ANTERIOR -> "←"
    ProcessDirection.DORSOVENTRAL_TRANSFER -> "↓"
    ProcessDirection.POSTERIOR -> "→"
    ProcessDirection.DISTRIBUTION -> "↔"
    ProcessDirection.COLLECTION -> "↺"
    ProcessDirection.PROCESSING -> "↻"
    ProcessDirection.ABSORPTION -> "⇣"
    ProcessDirection.FILTRATION -> "⇥"
    ProcessDirection.ENTERONEPHRIC -> "↣"
    ProcessDirection.EXONEPHRIC -> "↗"
    ProcessDirection.SENSORY_INPUT -> "⇥"
    ProcessDirection.NEURAL_RELAY -> "⇢"
    ProcessDirection.IMPULSE_CONDUCTION -> "⇄"
    ProcessDirection.MOTOR_RESPONSE -> "⇒"
}

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

/**
 * The historical standalone SVG exports intentionally preserve the original
 * anatomical geometry, but their repository audit records that browser CSS
 * was not embedded in those extracted files. AndroidSVG therefore saw the
 * default black SVG text on the dark atlas background during device QA.
 *
 * Inject only the rendering rules that came from the authoritative HQ atlas
 * stylesheet. This does not alter anatomy, labels, hotspot geometry or
 * scientific data; it restores the missing presentation context before the
 * SVG is parsed by the native renderer.
 */
private fun loadAtlasSvg(
    context: Context,
    @RawRes rawResId: Int,
    labelsVisible: Boolean,
    semanticZoomTier: SemanticZoomTier,
): SVG {
    val source = context.resources.openRawResource(rawResId)
        .bufferedReader(Charsets.UTF_8)
        .use { it.readText() }

    val normalizedLabelSource = normalizeAtlasLabelPresentation(source)

    val styled = normalizedLabelSource.replaceFirst(
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
.organ-label{font-size:15px;font-weight:800;fill:#ffffff;stroke:none}
.micro-label{font-size:11px;font-weight:700;fill:#ffffff;stroke:none}
.orientation-note{font-size:13px;font-weight:800;fill:#fff7d6;stroke:none}
.flap{fill:#835d4f;stroke:#c18b74;stroke-width:2;opacity:.44}
.surface-detail{pointer-events:none}
]]></style>"""
    )

    val withLabelMode = when {
        !labelsVisible -> styled
            .replace("class=\"organ-label\"", "class=\"organ-label\" visibility=\"hidden\"")
            .replace("class=\"micro-label\"", "class=\"micro-label\" visibility=\"hidden\"")
            .replace("class=\"orientation-note\"", "class=\"orientation-note\" visibility=\"hidden\"")
        semanticZoomTier == SemanticZoomTier.OVERVIEW -> styled
            .replace("class=\"micro-label\"", "class=\"micro-label\" visibility=\"hidden\"")
            .replace("class=\"orientation-note\"", "class=\"orientation-note\" visibility=\"hidden\"")
        else -> styled
            .replace("class=\"orientation-note\"", "class=\"orientation-note\" visibility=\"hidden\"")
    }

    return SVG.getFromString(withLabelMode)
}

/**
 * AndroidSVG does not render the exported label halo consistently at phone
 * scale. A dark stroke can intrude into the glyph fill and make the text look
 * muddy. Normalise only label text presentation before parsing: preserve all
 * wording, coordinates, IDs, font sizes and anatomical geometry while keeping
 * the visible glyph itself bright and stroke-free.
 */
private fun normalizeAtlasLabelPresentation(source: String): String {
    val labelOpeningTag = Regex(
        """<text\b[^>]*class="(?:organ-label|micro-label|orientation-note)"[^>]*>"""
    )
    return labelOpeningTag.replace(source) { match ->
        var tag = match.value
        tag = tag.replace(
            Regex("""fill:\s*#[0-9A-Fa-f]{6}\s*;?"""),
            "fill: #ffffff;",
        )
        tag = tag.replace(
            Regex("""stroke:\s*#[0-9A-Fa-f]{6}\s*;?"""),
            "stroke: none;",
        )
        tag = tag.replace(
            Regex("""stroke-width:\s*[0-9.]+\s*;?"""),
            "stroke-width: 0;",
        )
        tag = tag.replace(
            Regex("""stroke-linejoin:\s*[^;"]+\s*;?"""),
            "",
        )
        tag = tag.replace(
            Regex("""font-weight:\s*\d+\s*;?"""),
            if (tag.contains("class=\"micro-label\"")) {
                "font-weight: 700;"
            } else {
                "font-weight: 800;"
            },
        )
        tag.replace(Regex("""\s+paint-order="[^"]*""""), "")
    }
}

@DrawableRes
private fun detailedImageResource(system: EarthwormSystem): Int = when (system) {
    EarthwormSystem.EXTERNAL -> R.drawable.hq_atlas_external
    EarthwormSystem.DIGESTIVE -> R.drawable.hq_atlas_digestive
    EarthwormSystem.CIRCULATORY -> R.drawable.hq_atlas_circulatory
    EarthwormSystem.RESPIRATORY -> R.drawable.hq_atlas_respiratory
    EarthwormSystem.EXCRETORY -> R.drawable.hq_atlas_excretory
    EarthwormSystem.REPRODUCTIVE -> R.drawable.hq_atlas_reproductive
    EarthwormSystem.NERVOUS -> R.drawable.hq_atlas_nervous
    EarthwormSystem.TRANSVERSE_SECTION -> R.drawable.hq_atlas_crosssection
    EarthwormSystem.PREPARATION -> error("Preparation has no detailed anatomical image.")
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
