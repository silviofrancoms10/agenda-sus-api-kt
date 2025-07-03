package br.com.silviofrancoms.agendasusapikt.model

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore // <<< ADICIONE ESTE IMPORT
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.io.Serializable
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.Date

@Entity(name = "consulta")
data class Consulta(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    // Relacionamento com o Usuário (Paciente)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    var paciente: Usuario,


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medico_id", nullable = true)
    var medico: Usuario? = null,

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "especialidade", nullable = false)
    var especialidade: Especialidade,

    @Column(name = "descricao")
    var descricao: String? = null,

    @Column(name = "local_atendimento", nullable = false)
    var localAtendimento: String,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm")
    @Column(name = "data_hora_consulta")
    var dataHoraConsulta: LocalDateTime = LocalDateTime.now().plus(3, ChronoUnit.MONTHS),

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "preferencia_horario")
    var preferenciaHorario: PreferenciaHorario? = null,

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status", nullable = false)
    var status: StatusConsulta = StatusConsulta.AGENDADA,

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_criacao", updatable = false)
    val dataCriacao: Date? = null,

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_atualizacao")
    val dataAtualizacao: Date? = null

) : Serializable