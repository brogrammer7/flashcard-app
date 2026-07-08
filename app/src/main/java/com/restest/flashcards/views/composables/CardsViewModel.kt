package com.restest.flashcards.views.composables

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.restest.flashcards.data.model.CardResponse
import com.restest.flashcards.data.model.FlashCard
import com.restest.flashcards.data.repository.CardsLocalRepository
import com.restest.flashcards.network.ResApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CardsViewModel(
    private val repository: CardsLocalRepository = CardsLocalRepository.instance,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) :
    ViewModel() {
    private val _cardList =
        MutableStateFlow(listOf<FlashCard>())
    val cardList = _cardList.asStateFlow()

    private val _onlineCardsList = MutableStateFlow(CardResponse())
    val onlineCardsList: StateFlow<CardResponse> = _onlineCardsList.asStateFlow()

    init {
        collectRepositoryEvents()
        getFlashCards()
        getOnlineCards()
    }

    fun getOnlineCards() {
        viewModelScope.launch {
            try {
                val response = ResApi.retrofitService.getFlashCards()

                if (response.isSuccessful) {
                    response.body()?.let { cardResponse ->
                        _onlineCardsList.value = cardResponse
                    }
                }
            } catch (e: Exception) {

            }
        }
    }

    private fun collectRepositoryEvents() {
        viewModelScope.launch(dispatcher) {
            repository.events.collect { event ->
                when (event) {
                    is CardsLocalRepository.Event.Success -> {
                        _cardList.value = event.cards
                    }

                    is CardsLocalRepository.Event.CardAdded -> {}

                    is CardsLocalRepository.Event.Error, CardsLocalRepository.Event.Idle -> {}
                }
            }
        }
    }

    private fun getFlashCards() {
        repository.requestCards()
    }

}