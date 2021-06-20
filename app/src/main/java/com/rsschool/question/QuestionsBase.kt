package com.rsschool.question
//Вопросы с варинтами ответов и балами. Неправильный ответ 0, правильный 20
class QuestionsBase {
    companion object {
        val questions = arrayOf(
            Question(textQuestion = "What do you call a type of shape that has five sides?",answers = arrayOf<Pair<String, Int>>(
                "Square" to 0,
                "Сircle" to 0,
                "triangle" to 0,
                "rectangle" to 0,
                "Pentagon" to 20,
            )),
            Question(textQuestion = "Who was the first president of the United States of America?",answers = arrayOf<Pair<String, Int>>(
                "Barack Obama" to 0,
                "George Washington" to 20,
                "Vladimir Putin" to 0,
                "Donald Trump" to 0,
                "Joe Biden" to 0,
            )),
            Question(textQuestion = "Which is the coldest location in the earth?",answers = arrayOf<Pair<String, Int>>(
                "East Antarctica" to 20,
                "Africa" to 0,
                "Belarus" to 0,
                "Russia" to 0,
                "Atlantis" to 0,
            )),
            Question(textQuestion = "In meters, how long is an Olympic swimming pool?",answers = arrayOf<Pair<String, Int>>(
                "1 meter" to 0,
                "15 metres" to 0,
                "500 metres" to 0,
                "1000 metres" to 0,
                "50 metres" to 20,
            )),
            Question(textQuestion = "Which two parts of the body continue to grow for your entire life?",answers = arrayOf<Pair<String, Int>>(
                "Nose and Ears" to 20,
                "Brain" to 0,
                "Left Legs" to 0,
                "Hands" to 0,
                "Right Legs" to 0
            )),
        )
    }

}