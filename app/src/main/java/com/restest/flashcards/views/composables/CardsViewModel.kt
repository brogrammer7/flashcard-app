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

    private val _deletedCardsList = MutableStateFlow(CardResponse())
    val deletedCardsList: StateFlow<CardResponse> = _deletedCardsList.asStateFlow()

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

    fun deleteLastCard() {
        val currentList = _onlineCardsList.value.flashcards.toMutableList()
        currentList.removeAt(currentList.lastIndex)
        _onlineCardsList.value = _onlineCardsList.value.copy(
            flashcards = currentList
        )
    }

    fun deleteCurrentCard(cardId: String?) {
        val currentList = _onlineCardsList.value.flashcards.toMutableList()
        val deletedCard = currentList.find { it?.id == cardId }

        currentList.removeAll { it?.id == cardId }

        //Store deleted card(s) in case user presses Restore button
        deletedCard?.let {
            val deletedList = _deletedCardsList.value.flashcards.toMutableList()
            deletedList.add(it)
            _deletedCardsList.value = _deletedCardsList.value.copy(flashcards = deletedList)
        }

        _onlineCardsList.value = _onlineCardsList.value.copy(flashcards = currentList)
    }

    fun restoreLastCard(cardId: String?) {
        if (cardId == null) return

        val deletedList = _deletedCardsList.value.flashcards.toMutableList()
        val cardToRestore = deletedList.find { it?.id == cardId }

        if (cardToRestore != null) {
            deletedList.remove(cardToRestore)
            _deletedCardsList.value = _deletedCardsList.value.copy(flashcards = deletedList)

            //Return a deleted card back to the beginning of the card list
            val onlineList = _onlineCardsList.value.flashcards.toMutableList()
            onlineList.add(0, cardToRestore)
            _onlineCardsList.value = _onlineCardsList.value.copy(flashcards = onlineList)
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