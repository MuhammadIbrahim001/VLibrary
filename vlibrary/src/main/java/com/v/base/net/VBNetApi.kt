package com.v.base.net

import retrofit2.http.*

interface VBNetApi {

    @GET
    suspend fun get(@Url url: String): String

    @GET
    suspend fun get(@Url url: String, @QueryMap map: Map<String, @JvmSuppressWildcards Any>): String

    @POST
    suspend fun post(@Url url: String): String

    @POST
    suspend fun post(@Url url: String, @Body map: Map<String, @JvmSuppressWildcards Any>): String

    @DELETE
    suspend fun delete(@Url url: String, @QueryMap map:Map<String,  @JvmSuppressWildcards Any>): String

    @PUT
    fun put(@Url url: String, @Body map: Map<String, @JvmSuppressWildcards Any>): String

}