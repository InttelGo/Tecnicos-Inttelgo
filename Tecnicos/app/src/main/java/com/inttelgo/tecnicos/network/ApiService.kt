package com.inttelgo.tecnicos.network
import com.inttelgo.tecnicos.logic.Model.Articulo
import com.inttelgo.tecnicos.logic.Model.ArticuloInstResponse
import com.inttelgo.tecnicos.logic.Model.BarriosResponse
import com.inttelgo.tecnicos.logic.Model.CreateEvidenciaInstalationResponse
import com.inttelgo.tecnicos.logic.Model.DeleteImageResponse
import com.inttelgo.tecnicos.logic.Model.EvidenciasInstalationResponse
import com.inttelgo.tecnicos.logic.Model.FinishInstalationResponse
import com.inttelgo.tecnicos.logic.Model.JornadaResponse
import com.inttelgo.tecnicos.logic.Model.LoginRequest
import com.inttelgo.tecnicos.logic.Model.LoginResponse
import com.inttelgo.tecnicos.logic.Model.ObsInstalacionEvidenciaResponse
import com.inttelgo.tecnicos.logic.Model.ObsInstalacionResponse
import com.inttelgo.tecnicos.logic.Model.ObsTareaEvidenciaResponse
import com.inttelgo.tecnicos.logic.Model.ObsTareaResponse
import com.inttelgo.tecnicos.logic.Model.ObsTicketEvidenciaResponse
import com.inttelgo.tecnicos.logic.Model.ObsTicketResponse
import com.inttelgo.tecnicos.logic.Model.ProcessWithFiltersResponse
import com.inttelgo.tecnicos.logic.Model.ProcesoDetailResponse
import com.inttelgo.tecnicos.logic.Model.Request.AddInventaryInstalacionRequest
import com.inttelgo.tecnicos.logic.Model.Request.FcmTokenRequest
import com.inttelgo.tecnicos.logic.Model.Request.UbicationRequest
import com.inttelgo.tecnicos.logic.Model.Response.AddInventaryInstalacionResponse
import com.inttelgo.tecnicos.logic.Model.Response.FinishObservacionResponse
import com.inttelgo.tecnicos.logic.Model.Response.MessageResponse
import com.inttelgo.tecnicos.logic.Model.Response.ObservacionResponse
import com.inttelgo.tecnicos.logic.Model.Response.UbicationResponse
import com.inttelgo.tecnicos.logic.Model.SoporteWithFiltersResponse
import com.inttelgo.tecnicos.logic.Model.TareaResponse
import com.inttelgo.tecnicos.logic.Model.TareaWithFiltersResponse
import com.inttelgo.tecnicos.logic.Model.TicketResponse
import com.inttelgo.tecnicos.logic.Model.UpdateJornadaRequest
import com.inttelgo.tecnicos.logic.Model.UpdateProfileRequest
import com.inttelgo.tecnicos.logic.Model.UserProfileResponse
import com.inttelgo.tecnicos.logic.Model.updateInstallationBody
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

    @PUT("instalacion/{id}")
    suspend fun update(@Path("id") id:String, @Body request: updateInstallationBody): Response<MessageResponse>

    @GET("articulo/instalacion/{id}")
    suspend fun getArticulosInstalacion(@Path("id") id: String): Response<ArticuloInstResponse>

    @GET("articulo/tarea/{id}")
    suspend fun getArticulosTarea(@Path("id") id: String): Response<ArticuloInstResponse>

    @POST("articulo/instalacion")
    suspend fun agregarInventarioInstalacion(@Body addInventaryInstalacionRequest: AddInventaryInstalacionRequest): Response<AddInventaryInstalacionResponse>

    @POST("usuario/ubicacion")
    suspend fun ubication(@Body ubicationRequest: UbicationRequest): Response<UbicationResponse>

    @POST("usuario/fcm-token")
    suspend fun registerFcmToken(@Body request: FcmTokenRequest): Response<MessageResponse>

    @GET("ticket/search")
    suspend fun ticketsWithFilter(
        @Query("filters") filters: String,
        @Query("pagination") pagination: Int,
        @Query("limit") limit: Int,
        @Query("sorting") sorting: String
    ): Response<SoporteWithFiltersResponse>

    @GET("tarea/search")
    suspend fun tareasWithFilter(
        @Query("filters") filters: String,
        @Query("pagination") pagination: Int,
        @Query("limit") limit: Int,
        @Query("sorting") sorting: String
    ): Response<TareaWithFiltersResponse>

    @GET("barrio")
    suspend fun consultAllNeighborhoods(): Response<BarriosResponse>

    @GET("instalacion/{id}")
    suspend fun instalacionByID(@Path("id") id: String): Response<ProcesoDetailResponse>

    @GET("ticket/{id}")
    suspend fun ticketByID(@Path("id") id: String):Response<TicketResponse>
    @GET("tarea/{id}")
    suspend fun tareaByID(@Path("id") id: String): Response<TareaResponse>

    @GET("ticket/{id}/observacion")
    suspend fun consultObsWitFilterAndId(
        @Path("id") id : String,
        @Query("filters") filters: String,
        @Query("pagination") pagination: Int,
        @Query("limit") limit: Int,
        @Query("sorting") sorting: String
    ): Response<ObsTicketResponse>

    @GET("tarea/{id}/observacion/search")
    suspend fun consultObsTareaWitFilterAndId(
        @Path("id") id : String,
        @Query("filters") filters: String,
        @Query("pagination") pagination: Int,
        @Query("limit") limit: Int,
        @Query("sorting") sorting: String
    ): Response<ObsTareaResponse>

    @GET("instalacion/{id}/observacion/search")
    suspend fun consultObsInstalacionWithFilter(
        @Path("id") id: String,
        @Query("filters") filters: String,
        @Query("pagination") pagination: Int,
        @Query("limit") limit: Int,
        @Query("sorting") sorting: String
    ): Response<ObsInstalacionResponse>

    @GET("ticket/{id_ticket}/observacion/{id}/media")
    suspend fun consultByObsTicket(
        @Path("id_ticket") idTicket: String,
        @Path("id") idObservacion: String
    ): Response<ObsTicketEvidenciaResponse>

    @GET("tarea/{id}/observacion/{obs_id}/media")
    suspend fun consultByObsTarea(
        @Path("id") idTarea: String,
        @Path("obs_id") idObservacion: String
    ): Response<ObsTareaEvidenciaResponse>

    @GET("instalacion/{id}/observacion/{id_observacion}/media")
    suspend fun consultByObsInstalacion(
        @Path("id") id: String,
        @Path("id_observacion") idObservacion: String
    ): Response<ObsInstalacionEvidenciaResponse>
    @Multipart
    @POST("ticket/{id}/observacion")
    suspend fun createObsTicket(
        @Path("id") id: String,
        @Part("observacion") observacion: RequestBody,
        @Part("latitud") latitud: RequestBody? = null,
        @Part("longitud") longitud: RequestBody? = null,
        @Part("es_encargado") esEncargado: RequestBody? = null,
        @Part("nombre_encargado") nombreEncargado: RequestBody? = null,
        @Part("identificacion_encargado") identificacionEncargado: RequestBody? = null,
        @Part("skipProcess") skipProcess: RequestBody? = null,
        @Part media: List<MultipartBody.Part>? = null
    ): Response<ObservacionResponse>

    @Multipart
    @POST("ticket/{id}/observacion/finish")
    suspend fun finishObsTicket(
        @Path("id") id: String,
        @Part("observacion") observacion: RequestBody,
        @Part("latitud") latitud: RequestBody? = null,
        @Part("longitud") longitud: RequestBody? = null,
        @Part("es_encargado") esEncargado: RequestBody? = null,
        @Part("nombre_encargado") nombreEncargado: RequestBody? = null,
        @Part("identificacion_encargado") identificacionEncargado: RequestBody? = null,
        @Part("skipProcess") skipProcess: RequestBody? = null,
        @Part media: List<MultipartBody.Part>? = null
    ): Response<FinishObservacionResponse>

    @Multipart
    @POST("tarea/{id}/observacion")
    suspend fun createObsTarea(
        @Path("id") id: String,
        @Part("observacion") observacion: RequestBody,
        @Part("latitud") latitud: RequestBody? = null,
        @Part("longitud") longitud: RequestBody? = null,
        @Part("es_encargado") esEncargado: RequestBody? = null,
        @Part("nombre_encargado") nombreEncargado: RequestBody? = null,
        @Part("identificacion_encargado") identificacionEncargado: RequestBody? = null,
        @Part("skipProcess") skipProcess: RequestBody? = null,
        @Part media: List<MultipartBody.Part>? = null
    ): Response<ObservacionResponse>

    @Multipart
    @POST("instalacion/{id}/observacion")
    suspend fun createObsInstalacion(
        @Path("id") id: String,
        @Part("observacion") observacion: RequestBody,
        @Part("latitud") latitud: RequestBody? = null,
        @Part("longitud") longitud: RequestBody? = null,
        @Part("skipProcess") skipProcess: RequestBody? = null,
        @Part media: List<MultipartBody.Part>? = null
    ): Response<ObservacionResponse>

    @GET("instalacion/{id}/media")
    suspend fun consultEvicenciasInstalation(@Path("id") id: String): Response<EvidenciasInstalationResponse>

    @Multipart
    @POST("instalacion/media/{id}")
    suspend fun createEvidenciaInstalation(
        @Path("id") id: String,
        @Part("latitud") latitud: RequestBody? = null,
        @Part("longitud") longitud: RequestBody? = null,
        @Part("skipProcess") skipProcess: RequestBody? = null,
        @Part media: List<MultipartBody.Part>? = null
    ): Response<CreateEvidenciaInstalationResponse>

    @Multipart
    @POST("tarea/{id}/observacion/finish")
    suspend fun finishObsTarea(
        @Path("id") id: String,
        @Part("observacion") observacion: RequestBody,
        @Part("latitud") latitud: RequestBody? = null,
        @Part("longitud") longitud: RequestBody? = null,
        @Part("articulos") articulos: String? = null,
        @Part("es_encargado") esEncargado: RequestBody? = null,
        @Part("nombre_encargado") nombreEncargado: RequestBody? = null,
        @Part("identificacion_encargado") identificacionEncargado: RequestBody? = null,
        @Part("skipProcess") skipProcess: RequestBody? = null,
        @Part media: List<MultipartBody.Part>? = null
    ): Response<FinishObservacionResponse>

    @POST("instalacion/media/delete/{id}")
    suspend fun deleteImage(@Path("id") id: String): Response<DeleteImageResponse>
    @Multipart
    @POST("instalacion/{id}/observacion/finish")
    suspend fun finishTicket(
        @Path("id") id: String,
        @Part("latitud") latitud: RequestBody? = null,
        @Part("longitud") longitud: RequestBody? = null,
        @Part("observacion") observacion: RequestBody,
        @Part media: List<MultipartBody.Part>? = null,
        @Part("articulos") articulos: String? = null,
        @Part("es_encargado") esEncargado: RequestBody? = null,
        @Part("nombre_encargado") nombreEncargado: RequestBody? = null,
        @Part("identificacion_encargado") identificacionEncargado: RequestBody? = null,
        @Part("skipProcess") skipProcess: RequestBody? = null
    ): Response<FinishInstalationResponse>

    @GET("usuario/{id}")
    suspend fun getUserProfile(@Path("id") id: String): Response<UserProfileResponse>

    @PUT("usuario/{id}")
    suspend fun updateUserProfile(
        @Path("id") id: String,
        @Body request: UpdateProfileRequest
    ): Response<UserProfileResponse>

    @GET("usuario/{userId}/jornada/dia/{day}")
    suspend fun getJornadaByDay(
        @Path("userId") userId: String,
        @Path("day") day: String
    ): Response<JornadaResponse>

    @PUT("usuario/{userId}/jornada/{id}")
    suspend fun updateJornada(
        @Path("userId") userId: String,
        @Path("id") id: String,
        @Body request: UpdateJornadaRequest
    ): Response<JornadaResponse>
}