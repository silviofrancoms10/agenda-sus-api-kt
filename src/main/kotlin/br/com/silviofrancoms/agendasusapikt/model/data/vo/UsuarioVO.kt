package br.com.silviofrancoms.agendasusapikt.model.data.vo

import com.fasterxml.jackson.annotation.JsonFormat
import br.com.silviofrancoms.agendasusapikt.model.Endereco
import br.com.silviofrancoms.agendasusapikt.model.Genero
import java.util.*

data class UsuarioVO(
    var id: Long? = null,
    var nomeCompleto: String? = null,
    var cpf: String? = null,
    var cns: String? = null,
    var senha: String? = null,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy", locale = "pt-BR", timezone = "America/Campo_Grande")
    var dataNascimento: Date? = null,
    var genero: Genero? = null,
    var email: String? = null,
    var telefone: String? = null,
    var endereco: Endereco? = null,
    var aceitaTermos: Boolean = false,
    var aceitaNotificacoes: Boolean = false,
    var roles: String? = null,
    var dataCriacao: Date? = null,
    var dataAtualizacao: Date? = null
)