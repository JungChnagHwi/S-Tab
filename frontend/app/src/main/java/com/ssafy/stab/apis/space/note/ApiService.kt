package com.ssafy.stab.apis.space.note

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @POST("api/note")
    fun createNote(@Header("Authorization") authorization: String, @Body createNoteRequest: CreateNoteRequest): Call<CreateNoteResponse>

    @POST("api/note/pdf")
    fun createPdfNote(@Header("Authorization") authorization: String, @Body createPdfNoteRequest: CreatePdfNoteRequest): Call<CreateNoteResponse>

    @POST("api/note/copy")
    fun copyNote(@Header("Authorization") authorization: String, @Body copyNoteRequest: CopyNoteRequest): Call<CopyNoteResponse>

    @PATCH("api/note/rename")
    fun renameNote(@Header("Authorization") authorization: String, @Body renameNoteRequest: RenameNoteRequest): Call<Void>

    @PATCH("api/note/relocation")
    fun relocateNote(@Header("Authorization") authorization: String, @Body relocateRequest: RelocateRequest): Call<Void>

    @DELETE("api/note/{noteId}")
    fun deleteNote(@Header("Authorization") authorization: String, @Path("noteId") noteId: String): Call<Void>
}