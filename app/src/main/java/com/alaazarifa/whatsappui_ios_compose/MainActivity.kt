package com.alaazarifa.whatsappui_ios_compose


import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutSine
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.Spring.DampingRatioLowBouncy
import androidx.compose.animation.core.Spring.StiffnessMedium
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Surface(color = Color.Transparent, modifier = Modifier.background(Color.Transparent)) {
                WhatsAppChat()
            }
        }
    }
}


@Composable
@Preview
fun WhatsAppChat() {
    var isOptionShowing by remember { mutableStateOf(false) }

    Box() {
        DummyChat(
            modifier = Modifier
                .fillMaxSize()
                .blur(20.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.2f))
        )

        WhatsappChatSetup {
            isOptionShowing = it
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun WhatsappChatSetup(onOptionShowing: (Boolean) -> Unit) {

    var showMessageOptions by remember { mutableStateOf(false) }
    var selectedMessageText by remember { mutableStateOf("") }
    var isSender by remember { mutableStateOf(true) }

    SharedTransitionLayout {
        AnimatedContent(
            showMessageOptions,
            label = "basic_transition"
        ) { targetState ->
            onOptionShowing(targetState)

            if (targetState.not()) {
                Chat(this@AnimatedContent) { txt, sender ->
                    selectedMessageText = txt
                    showMessageOptions = true
                    isSender = sender
                }
            } else {
                MessageOptionsView(
                    isSender,
                    text = selectedMessageText,
                    animatedVisibilityScope = this@AnimatedContent
                ) {
                    showMessageOptions = false
                }
            }
        }
    }
}

@Composable
fun DummyChat(
    modifier: Modifier
) {
    Box(
        modifier = modifier
    ) {

        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = "",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(modifier = Modifier.fillMaxSize()) {


            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .wrapContentHeight(), contentAlignment = Alignment.BottomStart
            ) {


                val chatMessages = remember { getChatMessages().reversed() }


                LazyColumn(
                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 15.dp),
                    reverseLayout = true
                ) {
                    items(chatMessages) { message ->
                        when (message) {
                            is ChatMessage.Sender -> DummyChatItemView(
                                isSender = true,
                                text = message.message,
                            )

                            is ChatMessage.Receiver -> DummyChatItemView(
                                isSender = false,
                                text = message.message,
                            )
                        }
                    }
                }


            }


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(vertical = 3.dp, horizontal = 3.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(start = 7.dp)
                        .height(50.dp)
                        .shadow(
                            elevation = 0.5.dp,
                            shape = RoundedCornerShape(30.dp),
                            spotColor = Color(0x80000000)
                        )
                        .background(Color.White, RoundedCornerShape(30.dp)),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically

                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            painterResource(id = R.drawable.ic_emo),
                            contentDescription = "",
                            tint = Color(0xFF5F5F5F),
                            modifier = Modifier
                                .padding(start = 10.dp)
                                .clip(CircleShape)
                                .clickable(
                                    indication = rememberRipple(color = Color(0x0D000000)), // Customize click indication color
                                    interactionSource = remember { MutableInteractionSource() }
                                ) { }
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = "Message",
                            color = Color(0xFF5F5F5F),
                            fontSize = 17.sp
                        )

                    }
                    Row(
                        modifier = Modifier.padding(end = 12.dp)
                    ) { // Wrap the last twoicons in another Row

                        Icon(
                            painterResource(id = R.drawable.ic_att),
                            contentDescription = "",
                            tint = Color(0xFF5F5F5F),
                            modifier = Modifier
                                .padding(end = 12.dp)
                                .clip(CircleShape)
                                .clickable(
                                    indication = rememberRipple(color = Color(0x0D000000)), // Customize click indication color
                                    interactionSource = remember { MutableInteractionSource() }
                                ) { }

                        )

                        Icon(
                            painterResource(id = R.drawable.ic_cam),
                            contentDescription = "",
                            tint = Color(0xFF5F5F5F),
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable(
                                    indication = rememberRipple(color = Color(0x0D000000)), // Customize click indication color
                                    interactionSource = remember { MutableInteractionSource() }
                                ) { }

                        )

                    }
                }

                FloatingActionButton(
                    onClick = { },
                    modifier = Modifier
                        .padding(7.dp)
                        .size(45.dp),
                    shape = CircleShape,
                    containerColor = "#4AB949".toColor(),
                    contentColor = Color.Red,
                ) {
                    Icon(
                        painterResource(id = R.drawable.ic_mic),
                        contentDescription = "",
                        tint = Color.White
                    )
                }
            }

        }


    }
}


@Composable
fun DummyChatItemView(
    isSender: Boolean,
    text: String,
) {


    Box(
        contentAlignment = if (isSender) Alignment.BottomEnd else Alignment.BottomStart,
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = if (isSender) 0.dp else 50.dp, start = if (isSender) 50.dp else 0.dp)
    ) {

        Box(
            modifier = Modifier
                .wrapContentSize()
                .shadow(
                    elevation = 0.5.dp,
                    shape = RoundedCornerShape(17.dp),
                    spotColor = Color(0x68000000),
                    ambientColor = Color(0x68000000)
                )
                .background(
                    if (isSender) "#D9FDD4".toColor() else "#FFFFFF".toColor(),
                    RoundedCornerShape(17.dp)
                )
        ) {

            Column(
                modifier = Modifier.padding(vertical = 7.dp, horizontal = 10.dp),
                horizontalAlignment = Alignment.End
            ) {
                Text(text = text, fontSize = 17.sp, color = Color.Black, lineHeight = 22.sp)

                Spacer(modifier = Modifier.height(5.dp))
                Row(
                    horizontalArrangement = Arrangement.End,
                ) {
                    Text(text = "14:35 PM", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.width(5.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.ic_read),
                        contentDescription = "",
                        modifier = Modifier.size(14.dp),
                        tint = "#34B7F1".toColor()
                    )
                }

            }
        }

    }

    Spacer(modifier = Modifier.height(10.dp))
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.MessageOptionsView(
    isSender: Boolean,
    text: String = "",
    animatedVisibilityScope: AnimatedVisibilityScope,
    onDismiss: () -> Unit = {}
) {


    Column(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        onDismiss()
                    }
                )
            }
            .padding(horizontal = 10.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = if (isSender) Alignment.End else Alignment.Start
    ) {

        EmojoPanel()

        Box(modifier = Modifier.height(IntrinsicSize.Min))

        ChatItemView(
            isSender = isSender,
            text = text,
            animatedVisibilityScope = animatedVisibilityScope
        )

        OptionsMenu(isSender) {
            onDismiss()

        }

    }


}

@Composable
private fun OptionsMenu(isSender: Boolean = true, onSelect: (String) -> Unit) {

    var isVisible by remember { mutableStateOf(false) }

    val animatedProgress by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(
            durationMillis = 200,
            easing = EaseOutSine
        )
    )
    LaunchedEffect(Unit) {
        isVisible = true
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(
                bottom = 20.dp,
                start = if (isSender) 90.dp else 0.dp,
                end = if (isSender) 0.dp else 90.dp
            )
            .graphicsLayer {
                scaleX = animatedProgress
                scaleY = animatedProgress
                translationX = if (isSender) (1f - animatedProgress) * size.width else -(1f - animatedProgress) * size.width
                translationY = -(1f - animatedProgress) * size.height
            },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = "#faf9f9".toColor(),
        ),
    ) {


        val listTitles = listOf("Star", "Reply", "Forward", "Copy", "Info", "Pin", "Delete")
        val listRes =
            listOf(
                R.drawable.ic_star,
                R.drawable.ic_reply,
                R.drawable.ic_forward,
                R.drawable.ic_copy,
                R.drawable.ic_info,
                R.drawable.ic_pin,
                R.drawable.ic_del,
            )

        val list = listTitles.zip(listRes).map { (title, res) -> Message(title, res) }


        var selectedItemIndex by remember { mutableStateOf(-1) }
        var tappedItemIndex by remember { mutableStateOf(-1) }
        val context = LocalContext.current



        Column(
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val index = (offset.y / 47.dp.toPx()).toInt()
                        if (index in list.indices) {
                            tappedItemIndex = index
                            context.vibratePhone()
                            Log.d(">>>", "Tapped item: ${list[index]}")
                            onSelect(list[index].text)

                        }
                    }
                }
                .pointerInput(Unit) {
                    var lastIndex = -1
                    detectDragGestures(
                        onDragStart = { offset ->
                            val index = (offset.y / 47.dp.toPx()).toInt()
                            if (index in list.indices && index != lastIndex) {
                                selectedItemIndex = index
                                lastIndex = index
                                context.vibratePhone()
                            }
                        },
                        onDrag = { change, _ ->
                            val index = (change.position.y / 47.dp.toPx()).toInt()
                            if (index in list.indices && index != lastIndex) {
                                selectedItemIndex = index
                                lastIndex = index
                                context.vibratePhone()
                            }
                        },
                        onDragEnd = {
                            if (selectedItemIndex in list.indices) {
                                Log.d(">>>", "Dragged to item: ${list[selectedItemIndex]}")
                                onSelect(list[selectedItemIndex].text)
                            }
                            selectedItemIndex = -1
                            lastIndex = -1
                        }
                    )
                }
        ) {
            list.forEachIndexed { index, item ->
                MenuItem(
                    message = item,
                    isClicked = index == tappedItemIndex,
                    isSelected = index == selectedItemIndex,
                    isFirst = index == 0,
                    isLast = index == list.lastIndex
                )
            }
        }

    }
}

@Composable
private fun MenuItem(
    message: Message,
    isClicked: Boolean,
    isSelected: Boolean,
    isFirst: Boolean,
    isLast: Boolean
) {
    val backgroundColor = if (isSelected || isClicked) "#e0e0e0".toColor() else "#faf9f9".toColor()

    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = Modifier
            .fillMaxWidth()
            .height(47.dp)
            .clip(
                RoundedCornerShape(
                    topStart = if (isFirst) 20.dp else 0.dp,
                    topEnd = if (isFirst) 20.dp else 0.dp,
                    bottomStart = if (isLast) 20.dp else 0.dp,
                    bottomEnd = if (isLast) 20.dp else 0.dp
                )
            )
            .background(backgroundColor)

    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 25.dp)
        ) {
            Text(
                text = message.text,
                color = Color.Black,
                fontSize = 15.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 20.dp)
            )

            Icon(
                painter = painterResource(id = message.res), contentDescription = "",
                tint = if (message.res == R.drawable.ic_del) Color.Red else Color.Black,
                modifier = Modifier.size(20.dp)
            )
        }
    }

    if (!isLast) {
        Spacer(
            modifier = Modifier
                .height(1.dp)
                .fillMaxWidth()
                .background("#ebeae8".toColor())
        )
    }
}

@Composable
fun EmojoPanel() {
    var isExpanded by remember { mutableStateOf(false) }
    var selectedEmojiIndex by remember { mutableStateOf(-1) }
    val context = LocalContext.current

    LaunchedEffect(isExpanded) {
        delay(100)
        isExpanded = true
    }

    val animatedWidth = animateFloatAsState(
        targetValue = if (isExpanded) 300f else 55f,
        animationSpec = tween(200, easing = FastOutLinearInEasing)
    )

    val emojis = listOf(
        R.drawable.ic_like,
        R.drawable.ic_love,
        R.drawable.ic_happy,
        R.drawable.ic_shock,
        R.drawable.ic_cry,
        R.drawable.ic_hands
    ).reversed()

    val thresholds = listOf(96f, 136f, 176f, 215f, 251f, 298f)

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {

        Row(
            modifier = Modifier
                .size(animatedWidth.value.dp, 55.dp)
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(35.dp)
                )
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            val index = getEmojiIndexFromOffset(offset.x, emojis.size, size.width)
                            if (index != selectedEmojiIndex) {
                                selectedEmojiIndex = index
                                context.vibratePhone()
                            }
                        },
                        onDrag = { change, _ ->
                            val index =
                                getEmojiIndexFromOffset(change.position.x, emojis.size, size.width)
                            if (index != selectedEmojiIndex) {
                                selectedEmojiIndex = index
                                context.vibratePhone()
                            }
                        },
                        onDragEnd = {
                            if (selectedEmojiIndex in emojis.indices) {
                                println("Selected emoji: ${emojis[selectedEmojiIndex]}")
                                // Handle emoji selection here
                            }
                            selectedEmojiIndex = -1
                        }
                    )
                }
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val index = getEmojiIndexFromOffset(offset.x, emojis.size, size.width)
                        if (index in emojis.indices) {
                            selectedEmojiIndex = index
                            context.vibratePhone()
                            println("Tapped emoji: ${emojis[index]}")
                            // Handle emoji tap here
                        }
                    }
                },
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Spacer(modifier = Modifier.width(10.dp))

            AnimatedVisibility(
                visible = true,
                enter = scaleIn(
                    animationSpec = tween(
                        durationMillis = 100 // specify your desired duration here
                    )
                ),
                exit = scaleOut(
                    animationSpec = tween(
                        durationMillis = 100 // specify your desired duration here
                    )
                )
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .background(Color(0xFFECECEC), CircleShape)
                        .clip(CircleShape)
                        .clickable(
                            indication = rememberRipple(color = Color(0x0D000000)),
                            interactionSource = remember { MutableInteractionSource() }
                        ) { }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "",
                        tint = Color(0xFF8D8D8D),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Row(
                modifier = Modifier
                    .wrapContentWidth()
                    .weight(1f)
            ) {
                emojis.forEachIndexed { index, emoji ->
                    val visible =
                        animatedWidth.value > 55f && animatedWidth.value >= thresholds[index]
                    val scale = remember { Animatable(0f) }

                    LaunchedEffect(visible, selectedEmojiIndex) {
                        if (visible) {
                            val targetScale = if (index == selectedEmojiIndex) 1.55f else 1f
                            scale.animateTo(
                                targetValue = targetScale,
                                animationSpec = spring(
                                    dampingRatio = DampingRatioLowBouncy,
                                    stiffness = StiffnessMedium

                                )
                            )
                        } else {
                            scale.snapTo(0f)
                        }
                    }

                    Image(
                        painter = painterResource(id = emoji),
                        contentDescription = "",
                        modifier = Modifier
                            .size(28.dp)
                            .scale(scale.value),
                    )

                    Spacer(modifier = Modifier.width(13.dp))
                }
            }


//            Spacer(modifier = Modifier.width(5.dp))


        }
    }
    Spacer(modifier = Modifier.height(7.dp))
}

fun getEmojiIndexFromOffset(offsetX: Float, emojiCount: Int, totalWidth: Int): Int {
    val emojiWidth = totalWidth / emojiCount
    return (emojiCount - 1) - (offsetX / emojiWidth).toInt().coerceIn(0, emojiCount - 1)
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.Chat(
    animatedVisibilityScope: AnimatedVisibilityScope,
    onLongPress: (String, Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = "",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(modifier = Modifier.fillMaxSize()) {


            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .wrapContentHeight(), contentAlignment = Alignment.BottomStart
            ) {


                val chatMessages = remember { getChatMessages().reversed() }


                LazyColumn(
                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 15.dp),
                    reverseLayout = true
                ) {
                    items(chatMessages) { message ->
                        when (message) {
                            is ChatMessage.Sender -> ChatItemView(
                                isSender = true,
                                text = message.message,
                                animatedVisibilityScope
                            ) {
                                onLongPress(message.message, true)
                            }

                            is ChatMessage.Receiver -> ChatItemView(
                                isSender = false,
                                text = message.message,
                                animatedVisibilityScope = animatedVisibilityScope
                            ) {
                                onLongPress(message.message, false)
                            }
                        }
                    }
                }


            }


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(top = 3.dp, bottom = 20.dp, start = 3.dp, end = 3.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(start = 7.dp)
                        .height(50.dp)
                        .shadow(
                            elevation = 0.5.dp,
                            shape = RoundedCornerShape(30.dp),
                            spotColor = Color(0x80000000)
                        )
                        .background(Color.White, RoundedCornerShape(30.dp)),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically

                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            painterResource(id = R.drawable.ic_emo),
                            contentDescription = "",
                            tint = Color(0xFF5F5F5F),
                            modifier = Modifier
                                .padding(start = 10.dp)
                                .clip(CircleShape)
                                .clickable(
                                    indication = rememberRipple(color = Color(0x0D000000)), // Customize click indication color
                                    interactionSource = remember { MutableInteractionSource() }
                                ) { }
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = "Message",
                            color = Color(0xFF5F5F5F),
                            fontSize = 17.sp
                        )

                    }
                    Row(
                        modifier = Modifier.padding(end = 12.dp)
                    ) {

                        Icon(
                            painterResource(id = R.drawable.ic_att),
                            contentDescription = "",
                            tint = Color(0xFF5F5F5F),
                            modifier = Modifier
                                .padding(end = 12.dp)
                                .clip(CircleShape)
                                .clickable(
                                    indication = rememberRipple(color = Color(0x0D000000)), // Customize click indication color
                                    interactionSource = remember { MutableInteractionSource() }
                                ) { }

                        )

                        Icon(
                            painterResource(id = R.drawable.ic_cam),
                            contentDescription = "",
                            tint = Color(0xFF5F5F5F),
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable(
                                    indication = rememberRipple(color = Color(0x0D000000)), // Customize click indication color
                                    interactionSource = remember { MutableInteractionSource() }
                                ) { }

                        )

                    }
                }

                FloatingActionButton(
                    onClick = { },
                    modifier = Modifier
                        .padding(7.dp)
                        .size(45.dp),
                    shape = CircleShape,
                    containerColor = "#4AB949".toColor(),
                    contentColor = Color.Red,
                ) {
                    Icon(
                        painterResource(id = R.drawable.ic_mic),
                        contentDescription = "",
                        tint = Color.White
                    )
                }
            }

        }


    }
}


@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalFoundationApi::class)
@Composable
fun SharedTransitionScope.ChatItemView(
    isSender: Boolean,
    text: String,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onLongPress: (String) -> Unit = {}
) {

    val context = LocalContext.current

    Box(
        contentAlignment = if (isSender) Alignment.BottomEnd else Alignment.BottomStart,
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = if (isSender) 0.dp else 50.dp, start = if (isSender) 50.dp else 0.dp)
            .sharedBounds(
                rememberSharedContentState(
                    key = "message/$text"
                ),
                animatedVisibilityScope = animatedVisibilityScope,
                enter = scaleIn(
                    animationSpec = tween(
                        durationMillis = 200
                    )
                ),
                exit = scaleOut(
                    animationSpec = tween(
                        durationMillis = 200
                    )
                ),
                resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
            )
    ) {

        Box(
            modifier = Modifier
                .wrapContentSize()
                .shadow(
                    elevation = 0.5.dp,
                    shape = RoundedCornerShape(17.dp),
                    spotColor = Color(0x68000000),
                    ambientColor = Color(0x68000000)
                )
                .background(
                    if (isSender) "#D9FDD4".toColor() else "#FFFFFF".toColor(),
                    RoundedCornerShape(17.dp)
                )
                .combinedClickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(
                        color = Color.Gray.copy(alpha = 0.3f),
                        bounded = true
                    ),
                    onClick = { },
                    onLongClick = {
                        context.vibratePhone()
                        onLongPress(text)
                    }
                )
        ) {

            Column(
                modifier = Modifier.padding(vertical = 7.dp, horizontal = 10.dp),
                horizontalAlignment = Alignment.End
            ) {
                Text(text = text, fontSize = 17.sp, color = Color.Black, lineHeight = 22.sp)

                Spacer(modifier = Modifier.height(5.dp))
                Row(
                    horizontalArrangement = Arrangement.End,
                ) {
                    Text(text = "14:35 PM", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.width(5.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.ic_read),
                        contentDescription = "",
                        modifier = Modifier.size(14.dp),
                        tint = "#34B7F1".toColor()
                    )
                }

            }
        }

    }

    Spacer(modifier = Modifier.height(10.dp))
}


data class Message(val text: String, val res: Int)

fun String.toColor(): Color {
    val hex = this.removePrefix("#")
    return when (hex.length) {
        6 -> Color(
            red = hex.substring(0, 2).toInt(16) / 255f,
            green = hex.substring(2, 4).toInt(16) / 255f,
            blue = hex.substring(4, 6).toInt(16) / 255f,
            alpha = 1f
        )

        8 -> Color(
            red = hex.substring(0, 2).toInt(16) / 255f,
            green = hex.substring(2, 4).toInt(16) / 255f,
            blue = hex.substring(4, 6).toInt(16) / 255f,
            alpha = hex.substring(6, 8).toInt(16) / 255f
        )

        else -> throw IllegalArgumentException("Invalid color hex string")
    }
}

fun Dp.toPx(density: Density): Float {
    return this.value * density.density
}

sealed class ChatMessage {
    data class Sender(val message: String) : ChatMessage()
    data class Receiver(val message: String) : ChatMessage()
}

fun getChatMessages(): List<ChatMessage> {
    val senderMessages = listOf(
        "Hey, how are you? It's been a while.",
        "What's for dinner today? Should we order pizza or burgers?",
        "Did you watch the game last night? It was amazing!",
        "Let's catch up this weekend.",
        "Awesome, see you soon! \uD83E\uDD73",
    )

    val receiverMessages = listOf(
        "Yeah \uD83D\uDE05 I'm doing good tho, thanks!",
        "Let's go for pizza! \uD83C\uDF55\uD83C\uDF55\uD83C\uDF55",
        "Yes, I did! It was indeed amazing!",
        "Absolutely! Let's meet up. \uD83D\uDE4C",
        "Oh don't forget to come with your new Tesla, I wanna take it for spin! \uD83D\uDE0E ",
    )

    val chatMessages = mutableListOf<ChatMessage>()

    val messageCount = minOf(senderMessages.size, receiverMessages.size)

    for (i in 0 until messageCount) {
        chatMessages.add(ChatMessage.Sender(senderMessages[i]))
        chatMessages.add(ChatMessage.Receiver(receiverMessages[i]))
    }

    for (ch in chatMessages) {
        Log.d("ChatScreen", "Message: $ch")
    }

    return chatMessages
}


fun Context.vibratePhone(milliseconds: Long = 20) {
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val vibrationEffect =
            VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE)
        vibrator.vibrate(vibrationEffect)
    } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(milliseconds)
    }
}