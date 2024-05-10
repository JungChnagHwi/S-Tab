package com.ssafy.stab.apis.note

import com.ssafy.stab.data.note.request.NoteRequest
import com.ssafy.stab.data.note.request.PageId
import com.ssafy.stab.data.note.response.NewPage
import com.ssafy.stab.data.note.response.NoteResponse
import com.ssafy.stab.data.note.response.PageListResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @GET("/api/page/{noteId}")
    fun getPageList(
        @Header("Authorization") authorization: String,
        @Path("noteId") noteId: String
    ): Call<PageListResponse>

    @POST("/api/page")
    fun createPage(
        @Header("Authorization") authorization: String,
        @Body beforePageId: PageId
    ): Call<NewPage>
}