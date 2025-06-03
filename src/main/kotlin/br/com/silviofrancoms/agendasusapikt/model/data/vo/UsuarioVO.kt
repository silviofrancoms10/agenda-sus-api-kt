package br.com.silviofrancoms.agendasusapikt.model.data.vo

import br.com.silviofrancoms.agendasusapikt.model.Endereco
import br.com.silviofrancoms.agendasusapikt.model.Genero
import java.util.*

data class UsuarioVO(

    val id: Long? = null,
    var nomeCompleto: String? = null,
    var cpf: String? = null,
    var cns: String? = null,
    var senha: String? = null,
    var dataNascimento: Date? = null,
    var genero: Genero? = null,
    var email: String? = null,
    var telefone: String? = null,
    var endereco: Endereco? = null,
    var aceitaTermos: Boolean = false,
    var aceitaNotificacoes: Boolean = false,
    val dataCriacao: Date? = null,
    val dataAtualizacao: Date? = null

)
