package com.ssafy.stab.apis.space.folder

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("api/folder/{folderId}")
    fun getFileList(@Header("Authorization") authorization: String, @Path("folderId") folderId: String): Call<FileListResponse>

    @POST("api/folder")
    fun createFolder(@Header("Authorization") authorization: String, @Body createFolderRequest: CreateFolderRequest): Call<Folder>

    @PATCH("api/folder/rename")
    fun renameFolder(@Header("Authorization") authorization: String, @Body renameFolderRequest: RenameFolderRequest): Call<Void>

    @PATCH("api/folder/relocation")
    fun relocateFolder(@Header("Authorization") authorization: String, @Body renameFolderRequest: RelocateFolderRequest): Call<Void>

    @DELETE("api/folder/{folderId}")
    fun deleteFolder(@Header("Authorization") authorization: String, @Path("folderId") folderId: String): Call<Void>

    @GET("api/folder/name")
    fun searchFile(
        @Header("Authorization") authorization: String,
        @Query("spaceId") spaceId: String,
        @Query("name") name: String)
    : Call<FileListResponse>
}