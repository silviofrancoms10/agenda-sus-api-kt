package br.com.silviofrancoms.agendasusapikt.model

import com.fasterxml.jackson.annotation.JsonBackReference
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.io.Serializable
import java.util.Date

@Entity(name = "endereco")
data class Endereco(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    var cep: String? = null,
    var rua: String? = null,
    var numero: String? = null,
    var complemento: String? = null,
    var bairro: String? = null,
    var cidade: String? = null,
    var uf: String? = null,

    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    @JsonBackReference
    var usuario: Usuario? = null,

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_criacao", updatable = false)
    var dataCriacao: Date? = null,

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_atualizacao")
    var dataAtualizacao: Date? = null

) : Serializable
