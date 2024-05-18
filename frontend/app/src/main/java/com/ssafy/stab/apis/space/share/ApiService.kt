package com.ssafy.stab.apis.space.share

import com.ssafy.stab.apis.space.folder.FileListResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    @GET("api/folder/space/{spaceId}")
    fun getFileListShareSpace(@Header("Authorization") authorization: String, @Path("spaceId") spaceId: String): Call<FileListResponse>

    @GET("api/space/{spaceId}") // 선택한 스페이스 내의 참여자(users) 정보 조회
    fun getShareSpace(@Header("Authorization") authorization: String, @Path("spaceId") spaceId: String): Call<ShareSpace>

    @GET("api/space/list")
    fun getShareSpaceList(@Header("Authorization") authorization: String): Call<List<ShareSpaceList>>


    @POST("api/space")
    fun createShareSpace(@Header("Authorization") authorization: String, @Body createShareSpaceRequest: CreateShareSpaceRequest): Call<ShareSpaceList>

    @POST("api/space/join")
    fun participateShareSpace(@Header("Authorization") authorization: String, @Body participateShareSpaceRequest: ParticipateShareSpaceRequest): Call<Void>

    @DELETE("api/space/{spaceId}")
    fun leaveShareSpace(@Header("Authorization") authorization: String, @Path("spaceId") spaceId: String): Call<Void>

    @PATCH("api/space/rename")
    fun renameShareSpace(@Header("Authorization") authorization: String, @Body renameShareSpaceRequest: RenameShareSpaceRequest): Call<Void>

    @GET("api/space/cover/{spaceId}")
    fun getMarkDown(@Header("Authorization") authorization: String, @Path("spaceId") spaceId: String): Call<MarkdownDataResponse>

    @PUT("api/space/cover")
    fun patchMarkDown(@Header("Authorization") authorization: String, @Body patchRequest: MarkdownDataPatchRequest): Call<Void>
}