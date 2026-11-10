package com.example.ui.child

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.Story
import com.example.ui.components.GlassCard
import com.example.ui.theme.*

@Composable
fun StoryReaderScreen(
    story: Story,
    onCompleteStory: (String, Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Current state: 0 .. chapters.size - 1 for chapters, chapters.size for Quiz
    var currentStep by remember { mutableStateOf(0) }
    var selectedChoiceIndex by remember { mutableStateOf<Int?>(null) }
    var selectedQuizOption by remember { mutableStateOf<Int?>(null) }
    var quizSubmitted by remember { mutableStateOf(false) }

    val totalChapters = story.chapters.size
    val isQuizStep = currentStep >= totalChapters

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(LavenderBackground)
            .testTag("story_reader_screen"),
        contentPadding = PaddingValues(bottom = 40.dp)
    ) {
        // Top Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("story_reader_back_btn")
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextDark)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = story.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextDark,
                        maxLines = 1
                    )
                    Text(
                        text = if (isQuizStep) "Final Knowledge Challenge 🧠" else "Chapter ${currentStep + 1} of $totalChapters",
                        style = MaterialTheme.typography.bodySmall,
                        color = PrimaryPurple,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Cover / Illustration Banner
        item {
            val cover = story.coverDrawableRes ?: R.drawable.story_wise_saver_art
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .height(180.dp)
                    .clip(RoundedCornerShape(22.dp))
            ) {
                Image(
                    painter = painterResource(id = cover),
                    contentDescription = story.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        // Content: Either Chapter Reading or Quiz
        if (!isQuizStep) {
            val chapter = story.chapters[currentStep]

            item {
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Chapter ${chapter.chapterNumber}: ${chapter.title}",
                        style = MaterialTheme.typography.titleLarge,
                        color = PrimaryPurple,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FredokaFontFamily
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = chapter.storyText,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextDark,
                        lineHeight = 26.sp
                    )

                    // Interactive choices if any
                    if (chapter.choices.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(18.dp))
                        Text(
                            text = "What would you do? 🤔",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        chapter.choices.forEachIndexed { idx, choice ->
                            val isSelected = selectedChoiceIndex == idx
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) PrimaryPurple else Color(0xFFE5E7EB),
                                        shape = RoundedCornerShape(14.dp)
                                    )
                                    .background(if (isSelected) Color(0xFFF3E8FF) else Color.White)
                                    .clickable { selectedChoiceIndex = idx }
                                    .padding(12.dp)
                                    .testTag("story_choice_$idx")
                            ) {
                                Column {
                                    Text(
                                        text = choice.text,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = TextDark
                                    )
                                    if (isSelected) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "💡 ${choice.feedback}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF6D28D9),
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Navigation Buttons
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (currentStep > 0) {
                        OutlinedButton(
                            onClick = {
                                currentStep--
                                selectedChoiceIndex = null
                            },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("← Previous")
                        }
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }

                    Button(
                        onClick = {
                            currentStep++
                            selectedChoiceIndex = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("story_next_chapter_btn")
                    ) {
                        Text(
                            text = if (currentStep + 1 == totalChapters) "Take the Quiz 🧠 →" else "Next Chapter →",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        } else {
            // Quiz Step
            val quiz = story.quiz
            item {
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Story Quiz: Test What You Learned! 🌟",
                        style = MaterialTheme.typography.titleLarge,
                        color = PrimaryPurple,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FredokaFontFamily
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = quiz.question,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    quiz.options.forEachIndexed { idx, option ->
                        val isSelected = selectedQuizOption == idx
                        val isCorrect = idx == quiz.correctIndex

                        val (bg, borderCol) = when {
                            !quizSubmitted -> if (isSelected) Pair(Color(0xFFEDE9FE), PrimaryPurple) else Pair(Color.White, Color(0xFFE5E7EB))
                            isCorrect -> Pair(Color(0xFFD1FAE5), Color(0xFF10B981))
                            isSelected && !isCorrect -> Pair(Color(0xFFFEE2E2), Color(0xFFEF4444))
                            else -> Pair(Color.White, Color(0xFFE5E7EB))
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .border(1.5.dp, borderCol, RoundedCornerShape(14.dp))
                                .background(bg)
                                .clickable(enabled = !quizSubmitted) { selectedQuizOption = idx }
                                .padding(12.dp)
                                .testTag("quiz_option_$idx")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(borderCol.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${('A'.code + idx).toChar()}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = borderCol
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = option,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextDark,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    if (quizSubmitted) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF0FDF4), RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Lesson Explanation:",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = Color(0xFF047857),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = quiz.explanation,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF065F46)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    if (!quizSubmitted) {
                        Button(
                            onClick = {
                                if (selectedQuizOption != null) {
                                    quizSubmitted = true
                                    onCompleteStory(story.id, story.xpReward)
                                }
                            },
                            enabled = selectedQuizOption != null,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("submit_quiz_btn")
                        ) {
                            Text("Submit Answer & Earn 50 XP 🌟", fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Button(
                            onClick = onBack,
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("story_finish_btn")
                        ) {
                            Text("Back to Stories", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
