package br.com.silviofrancoms.agendasusapikt.model.data.vo

import br.com.silviofrancoms.agendasusapikt.model.Usuario
import java.util.*

data class EnderecoVO(

    var id: Long? = null,
    var cep: String? = null,
    var rua: String? = null,
    var numero: String? = null,
    var complemento: String? = null,
    var bairro: String? = null,
    var cidade: String? = null,
    var uf: String? = null,
    var usuario: Usuario? = null,
    var dataCriacao: Date? = null,
    var dataAtualizacao: Date? = null

)
