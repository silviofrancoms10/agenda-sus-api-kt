package br.com.silviofrancoms.agendasusapikt.model.data.vo

import br.com.silviofrancoms.agendasusapikt.model.Especialidade
import br.com.silviofrancoms.agendasusapikt.model.PreferenciaHorario
import br.com.silviofrancoms.agendasusapikt.model.StatusConsulta
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

data class ConsultaVO(
    var id: Long? = null,

    @JsonProperty("pacienteId")
    var pacienteId: Long? = null,

    @JsonProperty("medicoId")
    var medicoId: Long? = null,

    var especialidade: Especialidade? = null, // Tipo Enum
    var descricao: String? = null,
    var localAtendimento: String? = null,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm")
    var dataHoraConsulta: LocalDateTime? = null,

    var preferenciaHorario: PreferenciaHorario? = null, // Tipo Enum
    var status: StatusConsulta? = null // Tipo Enum
)