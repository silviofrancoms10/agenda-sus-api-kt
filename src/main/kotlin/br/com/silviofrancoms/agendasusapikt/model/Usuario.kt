package br.com.silviofrancoms.agendasusapikt.model

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.JsonManagedReference
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.io.Serializable
import java.util.*

@Entity(name = "usuario")
@JsonIdentityInfo(
    generator = ObjectIdGenerators.PropertyGenerator::class,
    property = "id"
)
data class Usuario(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "nome_completo")
    var nomeCompleto: String? = null,

    @Column(name = "cpf", unique = true)
    var cpf: String? = null,

    @Column(name = "cns", unique = true)
    var cns: String? = null,

    @Column(name = "senha", nullable = false)
    var senha: String? = null,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    @Column(name = "data_nascimento")
    var dataNascimento: Date? = null,

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "genero")
    var genero: Genero? = null,

    @Column(name = "email", unique = true)
    var email: String? = null,

    @Column(name = "telefone")
    var telefone: String? = null,

    @OneToOne(mappedBy = "usuario", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JsonManagedReference("usuario-endereco")
    var endereco: Endereco? = null,

    @Column(name = "aceita_termos")
    var aceitaTermos: Boolean = false,

    @Column(name = "aceita_notificacoes")
    var aceitaNotificacoes: Boolean = false,

    @Column(name = "roles")
    var roles: String = "USUARIO",

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_criacao", updatable = false)
    val dataCriacao: Date? = null,

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_atualizacao")
    val dataAtualizacao: Date? = null

) : Serializable