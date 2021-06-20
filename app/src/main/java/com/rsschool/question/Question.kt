package com.rsschool.question

import java.io.Serializable
//Объект для вопросов с варинтами ответов и балами.
data class Question(val textQuestion:String,val answers:Array<Pair<String,Int>>):Serializable
