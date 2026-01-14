package com.inttelgo.tecnicos.network
import com.inttelgo.tecnicos.logic.Model.Articulo
import com.inttelgo.tecnicos.logic.Model.DeleteImageResponse
import com.inttelgo.tecnicos.logic.Model.Request.AddInventaryInstalacionRequest
import com.inttelgo.tecnicos.logic.Model.Request.ChangeStatusProcesosRequest
import com.inttelgo.tecnicos.logic.Model.Request.LoginRequest
import com.inttelgo.tecnicos.logic.Model.Request.UbicationRequest
import com.inttelgo.tecnicos.logic.Model.Response.AddInventaryInstalacionResponse
import com.inttelgo.tecnicos.logic.Model.Response.ArticuloInstResponse
import com.inttelgo.tecnicos.logic.Model.Response.BarriosResponse
import com.inttelgo.tecnicos.logic.Model.Response.CreateEvidenciaInstalationResponse
import com.inttelgo.tecnicos.logic.Model.Response.EvidenciasInstalationResponse
import com.inttelgo.tecnicos.logic.Model.Response.FinishInstalationResponse
import com.inttelgo.tecnicos.logic.Model.Response.FinishObservacionResponse
import com.inttelgo.tecnicos.logic.Model.Response.LoginResponse
import com.inttelgo.tecnicos.logic.Model.Response.MessageResponse
import com.inttelgo.tecnicos.logic.Model.Response.ObsTareaEvidenciaResponse
import com.inttelgo.tecnicos.logic.Model.Response.ObsTareaResponse
import com.inttelgo.tecnicos.logic.Model.Response.ObsTicketEvidenciaResponse
import com.inttelgo.tecnicos.logic.Model.Response.ObsTicketResponse
import com.inttelgo.tecnicos.logic.Model.Response.ObservacionResponse
import com.inttelgo.tecnicos.logic.Model.Response.ProcessWithFiltersResponse
import com.inttelgo.tecnicos.logic.Model.Response.SoporteWithFiltersResponse
import com.inttelgo.tecnicos.logic.Model.Response.TareaResponse
import com.inttelgo.tecnicos.logic.Model.Response.TareaWithFiltersResponse
import com.inttelgo.tecnicos.logic.Model.Response.TicketResponse
import com.inttelgo.tecnicos.logic.Model.Response.UbicationResponse
import com.inttelgo.tecnicos.logic.Model.UpdateProfileRequest
import com.inttelgo.tecnicos.logic.Model.UserProfileResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("usuario/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @GET("instalacion/procesos")
    suspend fun processWithFilter(
        @Query("filters") filters: String,
        @Query("pagination") pagination: Int,
        @Query("limit") limit: Int,
        @Query("sorting") sorting: String
    ): Response<ProcessWithFiltersResponse>

    @PUT("instalacion/estado")
    suspend fun processChangeStatus(@Body() request: ChangeStatusProcesosRequest): Response<MessageResponse>

    @GET("articulo/instalacion/{id}")
    suspend fun getArticulosInstalacion(@Path("id") id: String): Response<ArticuloInstResponse>

    @GET("articulo/tarea/{id}")
    suspend fun getArticulosTarea(@Path("id") id: String): Response<ArticuloInstResponse>

    @POST("articulo/instalacion")
    suspend fun agregarInventarioInstalacion(@Body addInventaryInstalacionRequest: AddInventaryInstalacionRequest): Response<AddInventaryInstalacionResponse>

    @POST("usuario/ubicacion")
    suspend fun ubication(@Body ubicationRequest: UbicationRequest): Response<UbicationResponse>

    @GET("ticket/search")
    suspend fun ticketsWithFilter(
        @Query("filters") filters: String,
        @Query("pagination") pagination: Int,
        @Query("limit") limit: Int,
        @Query("sorting") sorting: String
    ): Response<SoporteWithFiltersResponse>

    @GET("tarea/search")
    suspend fun tareasWithFilter(
        @Query("form") form: String,
        @Query("area") area: Int,
    ): Response<TareaWithFiltersResponse>

    @GET("barrio")
    suspend fun consultAllNeighborhoods(): Response<BarriosResponse>

    @GET("ticket/{id}")
    suspend fun ticketByID(@Path("id") id: String):Response<TicketResponse>
    @GET("tarea/{id}")
    suspend fun tareaByID(@Path("id") id: String): Response<TareaResponse>

    @GET("observaciones/ticket/search/{id}")
    suspend fun consultObsWitFilterAndId(@Path("id") id : String, @Query("form") form: String): Response<ObsTicketResponse>

    @GET("observaciones/tarea/search/{id}")
    suspend fun consultObsTareaWitFilterAndId(@Path("id") id : String, @Query("form") form: String): Response<ObsTareaResponse>
    @GET("observaciones/ticket/media/{id}")
    suspend fun consultByObsTicket(@Path("id") id: String ): Response<ObsTicketEvidenciaResponse>

    @GET("observaciones/tarea/media/{id}")
    suspend fun consultByObsTarea (@Path("id") id: String ): Response<ObsTareaEvidenciaResponse>
    @Multipart
    @POST("observaciones/ticket")
    suspend fun createObsTicket(
        @Part("id") id: RequestBody,
        @Part("observacion") observacion: RequestBody,
        @Part("latitud") latitud: RequestBody? = null,
        @Part("longitud") longitud: RequestBody? = null,
        @Part media: List<MultipartBody.Part>? = null
    ): Response<ObservacionResponse>

    @Multipart
    @POST("observaciones/ticket/finish")
    suspend fun finishObsTicket(
        @Part("id") id: RequestBody,
        @Part("observacion") observacion: RequestBody,
        @Part("latitud") latitud: RequestBody? = null,
        @Part("longitud") longitud: RequestBody? = null,
        @Part media: List<MultipartBody.Part>? = null
    ): Response<FinishObservacionResponse>

    @Multipart
    @POST("observaciones/tarea")
    suspend fun createObsTarea(
        @Part("id") id: RequestBody,
        @Part("observacion") observacion: RequestBody,
        @Part("latitud") latitud: RequestBody? = null,
        @Part("longitud") longitud: RequestBody? = null,
        @Part media: List<MultipartBody.Part>? = null
    ): Response<ObservacionResponse>

    @GET("instalacion/media/{id}")
    suspend fun consultEvicenciasInstalation(@Path("id") id: String): Response<EvidenciasInstalationResponse>

    @Multipart
    @POST("instalacion/media/{id}")
    suspend fun createEvidenciaInstalation(
        @Path("id") id: String,
        @Part("latitud") latitud: RequestBody? = null,
        @Part("longitud") longitud: RequestBody? = null,
        @Part media: List<MultipartBody.Part>? = null
    ): Response<CreateEvidenciaInstalationResponse>

    @Multipart
    @POST("observaciones/tarea/finish")
    suspend fun finishObsTarea(
        @Part("id") id: RequestBody,
        @Part("observacion") observacion: RequestBody,
        @Part("latitud") latitud: RequestBody? = null,
        @Part("longitud") longitud: RequestBody? = null,
        @Part("articulos") articulos: String? = null,
        @Part media: List<MultipartBody.Part>? = null
    ): Response<FinishObservacionResponse>

    @POST("instalacion/media/delete/{id}")
    suspend fun deleteImage(@Path("id") id: String): Response<DeleteImageResponse>
    @Multipart
    @POST("instalacion/finish")
    suspend fun finishTicket(
        @Part("id") id: RequestBody,
        @Part("observacion") observacion: RequestBody,
        @Part media: List<MultipartBody.Part>? = null,
        @Part("articulos") articulos: String? = null
    ): Response<FinishInstalationResponse>

    @GET("usuario/verifyAuth")
    suspend fun getUserProfile(): Response<UserProfileResponse>

    @PUT("user/profile")
    suspend fun updateUserProfile(
        @Body request: UpdateProfileRequest
    ): Response<UserProfileResponse>
}