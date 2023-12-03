package com.hfut.schedule.logic.datamodel

data class Library(val total : Int,val content : List<content>)

data class content (val author: String,
                    val callNo: String,
                    val pubYear: String,
                    val publisher: String,
                    val title: String)
