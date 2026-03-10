package com.firstaidnow.ui.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.firstaidnow.R
import com.firstaidnow.databinding.FragmentQuizBinding

class QuizFragment : Fragment() {

    private var _binding: FragmentQuizBinding? = null
    private val binding get() = _binding!!
    private val viewModel: QuizViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuizBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnStartQuiz.setOnClickListener {
            viewModel.loadQuiz()
            binding.quizStartContainer.visibility = View.GONE
            binding.quizContainer.visibility = View.VISIBLE
            binding.quizResultContainer.visibility = View.GONE
        }

        binding.btnCheckAnswer.setOnClickListener {
            viewModel.checkAnswer()
        }

        binding.btnNextQuestion.setOnClickListener {
            viewModel.nextQuestion()
        }

        binding.btnRestartQuiz.setOnClickListener {
            viewModel.loadQuiz()
            binding.quizResultContainer.visibility = View.GONE
            binding.quizContainer.visibility = View.VISIBLE
        }

        setupOptionButtons()
        observeViewModel()
    }

    private fun setupOptionButtons() {
        binding.btnOptionA.setOnClickListener { viewModel.selectAnswer("A") }
        binding.btnOptionB.setOnClickListener { viewModel.selectAnswer("B") }
        binding.btnOptionC.setOnClickListener { viewModel.selectAnswer("C") }
        binding.btnOptionD.setOnClickListener { viewModel.selectAnswer("D") }
    }

    private fun observeViewModel() {
        viewModel.questions.observe(viewLifecycleOwner) {
            updateQuestion()
        }

        viewModel.currentIndex.observe(viewLifecycleOwner) {
            updateQuestion()
        }

        viewModel.selectedAnswer.observe(viewLifecycleOwner) { selected ->
            resetButtonStyles()
            selected?.let { highlightSelected(it) }
            binding.btnCheckAnswer.isEnabled = selected != null
        }

        viewModel.answerChecked.observe(viewLifecycleOwner) { checked ->
            if (checked) {
                showAnswerFeedback()
                binding.btnCheckAnswer.visibility = View.GONE
                binding.btnNextQuestion.visibility = View.VISIBLE
            } else {
                binding.btnCheckAnswer.visibility = View.VISIBLE
                binding.btnNextQuestion.visibility = View.GONE
                binding.tvExplanation.visibility = View.GONE
            }
        }

        viewModel.quizFinished.observe(viewLifecycleOwner) { finished ->
            if (finished) {
                binding.quizContainer.visibility = View.GONE
                binding.quizResultContainer.visibility = View.VISIBLE
                val score = viewModel.score.value ?: 0
                val total = viewModel.questions.value?.size ?: 0
                binding.tvFinalScore.text = "$score / $total"
                val percentage = if (total > 0) (score * 100) / total else 0
                binding.tvScoreMessage.text = when {
                    percentage >= 80 -> "Excellent! You're well-prepared for emergencies!"
                    percentage >= 60 -> "Good job! Keep learning to improve your first aid skills."
                    else -> "Keep practicing! First aid knowledge can save lives."
                }
            }
        }
    }

    private fun updateQuestion() {
        val question = viewModel.getCurrentQuestion() ?: return
        val index = viewModel.currentIndex.value ?: 0
        val total = viewModel.questions.value?.size ?: 0

        binding.tvQuestionNumber.text = "Question ${index + 1} of $total"
        binding.tvQuestion.text = question.question
        binding.btnOptionA.text = "A. ${question.optionA}"
        binding.btnOptionB.text = "B. ${question.optionB}"
        binding.btnOptionC.text = "C. ${question.optionC}"
        binding.btnOptionD.text = "D. ${question.optionD}"
        binding.tvScore.text = "Score: ${viewModel.score.value ?: 0}"
    }

    private fun resetButtonStyles() {
        val buttons = listOf(binding.btnOptionA, binding.btnOptionB, binding.btnOptionC, binding.btnOptionD)
        buttons.forEach { btn ->
            btn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.option_default))
            btn.setTextColor(ContextCompat.getColor(requireContext(), R.color.option_text))
            btn.isEnabled = true
        }
    }

    private fun highlightSelected(answer: String) {
        val btn = when (answer) {
            "A" -> binding.btnOptionA
            "B" -> binding.btnOptionB
            "C" -> binding.btnOptionC
            "D" -> binding.btnOptionD
            else -> return
        }
        btn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.option_selected))
        btn.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
    }

    private fun showAnswerFeedback() {
        val question = viewModel.getCurrentQuestion() ?: return
        val selected = viewModel.selectedAnswer.value ?: return
        val correct = question.correctAnswer

        val buttons = listOf(binding.btnOptionA, binding.btnOptionB, binding.btnOptionC, binding.btnOptionD)
        buttons.forEach { it.isEnabled = false }

        // Show correct answer in green
        val correctBtn = when (correct) {
            "A" -> binding.btnOptionA
            "B" -> binding.btnOptionB
            "C" -> binding.btnOptionC
            "D" -> binding.btnOptionD
            else -> return
        }
        correctBtn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.answer_correct))
        correctBtn.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))

        // Show wrong answer in red if selected was wrong
        if (selected != correct) {
            val wrongBtn = when (selected) {
                "A" -> binding.btnOptionA
                "B" -> binding.btnOptionB
                "C" -> binding.btnOptionC
                "D" -> binding.btnOptionD
                else -> return
            }
            wrongBtn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.answer_wrong))
            wrongBtn.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
        }

        binding.tvExplanation.text = question.explanation
        binding.tvExplanation.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
