package br.com.silviofrancoms.agendasusapikt.model.data.vo

data class NovaConsultaVO(
    val especialidade: Int,
    val descricao: String?,
    val localAtendimento: String,
    val preferenciaHorario: Int?
)