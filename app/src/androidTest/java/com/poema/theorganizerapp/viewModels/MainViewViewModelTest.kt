package com.poema.theorganizerapp.viewModels

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import com.poema.theorganizerapp.models.EntireCategory
import com.poema.theorganizerapp.models.Video

import org.junit.Before
import org.junit.Test

class MainViewViewModelTest{

    private lateinit var viewModel : MainViewViewModel
    private lateinit var context : Context
    private lateinit var resultList: MutableList<EntireCategory>
    private val groupTitleA = "GroupA"
    private val groupTitleB = "GroupB"
    private val groupTitleC = "GroupC"

    @Before
    fun setup(){
    context = ApplicationProvider.getApplicationContext()
    viewModel =  MainViewViewModel(context)
        var testVideos : MutableList<Video> = mutableListOf()
        testVideos =
            listOf(Video("TitleOfVideo1","Anything","Anything","Anything",groupTitleA,0),
                Video("TitleOfVideo2","Anything","Anything","Anything",groupTitleC,0),
                Video("TitleOfVideo3","Anything","Anything","Anything",groupTitleA,0),
                Video("TitleOfVideo3","Anything","Anything","Anything",groupTitleB,0),
                Video("TitleOfVideo4","Anything","Anything","Anything",groupTitleC,0)).toMutableList()
        resultList = viewModel.doSorting(testVideos)
    }

    @Test
    fun creatingSortedEntireCategoryObjects_test(){  //se loggen!
        for (category in resultList){
            println("!!! ${category.categoryTitle}")
            for(listItem in category.categoryItems){
            println("!!!  ${listItem.title}")
            }
        }
    }
    @Test
    fun checkIfNumberOfExtractedGroupsIsCorrect(){
        assertThat(resultList.size).isEqualTo(3)
    }
    @Test
    fun checkIfCategoryTitlesAreInAlphabeticalOrder(){
        assertThat(resultList[0].categoryTitle < resultList[1].categoryTitle && resultList[1].categoryTitle < resultList[2].categoryTitle).isEqualTo(true)
    }
    @Test
    fun checkIfCategoriesContainCorrectAmountOfVideos(){
        assertThat(resultList[0].categoryItems.size == 2 && resultList[1].categoryItems.size == 1 && resultList[2].categoryItems.size == 2).isEqualTo(true)
    }
}

