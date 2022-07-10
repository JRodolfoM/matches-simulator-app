package br.com.jrmantovani.simulator.domain

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
class Match(
    @SerializedName("descricao")
    val description: String,
    @SerializedName("local")
    val place: Place,
    @SerializedName("mandante")
    val homeTeam: Team,
    @SerializedName("visitante")
    val awayTeam: Team
):Parcelable