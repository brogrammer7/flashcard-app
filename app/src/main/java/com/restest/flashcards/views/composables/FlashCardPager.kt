package com.restest.flashcards.views.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.restest.flashcards.data.model.OnlineCard
import com.restest.flashcards.views.navigation.Screen
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FlashCardPager(viewModel: CardsViewModel, navController: NavController) {
    val cards = viewModel.onlineCardsList.collectAsState()
    val deletedCards = viewModel.deletedCardsList.collectAsState()
    val pagerState = rememberPagerState(pageCount = {
        cards.value.flashcards.count()
    })
    val scope = rememberCoroutineScope()
    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(state = pagerState, modifier = Modifier.align(Alignment.TopStart)) { page ->
            FlashCardView(flashCard = cards.value.flashcards[page])
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        if (pagerState.currentPage > 0) {
                            scope.launch {
                                pagerState.scrollToPage(pagerState.currentPage - 1)
                            }
                        }
                    },
                    enabled = pagerState.currentPage > 0,
                    modifier = Modifier.width(150.dp)
                ) {
                    Text("Previous")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = {
                        if (pagerState.currentPage < pagerState.pageCount - 1) {
                            scope.launch {
                                pagerState.scrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    },
                    enabled = pagerState.currentPage < pagerState.pageCount - 1,
                    modifier = Modifier.width(150.dp)
                ) {
                    Text("Next")
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    enabled = cards.value.flashcards.isNotEmpty(),
                    onClick = {
                        val currentCard = cards.value.flashcards[pagerState.currentPage]
                        viewModel.deleteCurrentCard(currentCard?.id)
                    },
                    modifier = Modifier.width(150.dp)
                ) {
                    Text("Delete Card")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    enabled = deletedCards.value.flashcards.isNotEmpty(),
                    onClick = {
                        deletedCards.value.flashcards.lastOrNull()?.id?.let { cardId ->
                            viewModel.restoreLastCard(cardId)
                        }
                    },
                ) {
                    Text("Restore Card")
                }
            }
        }
    }
}


@Composable
private fun FlashCardView(flashCard: OnlineCard?) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = flashCard?.slug ?: "Error retrieving title", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = flashCard?.question ?: "Error retrieving question", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = flashCard?.answer ?: "Error retrieving answer",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .background(color = Color.LightGray, shape = RoundedCornerShape(8.dp))
                .border(2.dp, Color.DarkGray, RoundedCornerShape(8.dp))
                .padding(8.dp)
        )
    }
}

fun NavGraphBuilder.flashCardPagerRoute(navController: NavController) {
    composable(Screen.FlashCards.route) {
        FlashCardPager(viewModel(), navController)
    }
}