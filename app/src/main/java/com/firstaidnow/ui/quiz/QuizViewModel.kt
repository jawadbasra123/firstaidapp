package com.firstaidnow.ui.quiz

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.firstaidnow.FirstAidNowApp
import com.firstaidnow.data.local.entity.QuizQuestion
import kotlinx.coroutines.launch

class QuizViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as FirstAidNowApp).repository

    private val _questions = MutableLiveData<List<QuizQuestion>>()
    val questions: LiveData<List<QuizQuestion>> = _questions

    private val _currentIndex = MutableLiveData(0)
    val currentIndex: LiveData<Int> = _currentIndex

    private val _score = MutableLiveData(0)
    val score: LiveData<Int> = _score

    private val _quizFinished = MutableLiveData(false)
    val quizFinished: LiveData<Boolean> = _quizFinished

    private val _selectedAnswer = MutableLiveData<String?>(null)
    val selectedAnswer: LiveData<String?> = _selectedAnswer

    private val _answerChecked = MutableLiveData(false)
    val answerChecked: LiveData<Boolean> = _answerChecked

    fun loadQuiz(count: Int = 10) {
        _score.value = 0
        _currentIndex.value = 0
        _quizFinished.value = false
        _selectedAnswer.value = null
        _answerChecked.value = false

        viewModelScope.launch {
            val loaded = repository.getRandomQuizQuestions(count)
            _questions.value = loaded
        }
    }

    fun selectAnswer(answer: String) {
        if (_answerChecked.value == true) return
        _selectedAnswer.value = answer
    }

    fun checkAnswer() {
        val questions = _questions.value ?: return
        val index = _currentIndex.value ?: return
        val selected = _selectedAnswer.value ?: return

        if (index >= questions.size) return

        val correct = questions[index].correctAnswer
        if (selected == correct) {
            _score.value = (_score.value ?: 0) + 1
        }
        _answerChecked.value = true
    }

    fun nextQuestion() {
        val questions = _questions.value ?: return
        val nextIndex = (_currentIndex.value ?: 0) + 1

        if (nextIndex >= questions.size) {
            _quizFinished.value = true
        } else {
            _currentIndex.value = nextIndex
            _selectedAnswer.value = null
            _answerChecked.value = false
        }
    }

    fun getCurrentQuestion(): QuizQuestion? {
        val index = _currentIndex.value ?: return null
        val questions = _questions.value ?: return null
        return if (index < questions.size) questions[index] else null
    }
}
