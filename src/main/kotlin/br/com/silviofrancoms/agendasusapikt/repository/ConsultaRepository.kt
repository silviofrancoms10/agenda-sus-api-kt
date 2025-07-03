package br.com.silviofrancoms.agendasusapikt.repository

import br.com.silviofrancoms.agendasusapikt.model.Consulta
import br.com.silviofrancoms.agendasusapikt.model.data.vo.ConsultaVO
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ConsultaRepository: JpaRepository<Consulta, Long> {

    @Query("""
        SELECT NEW br.com.silviofrancoms.agendasusapikt.model.data.vo.ConsultaVO(
            c.id, 
            c.paciente.id, 
            c.medico.id,
            c.especialidade, 
            c.descricao, 
            c.localAtendimento, 
            c.dataHoraConsulta, 
            c.preferenciaHorario, 
            c.status
        ) 
        FROM consulta c 
        WHERE c.id = :id
    """)
    fun findConsultaVOById(id: Long): ConsultaVO?

    // Projeção para buscar todas as ConsultaVOs
    @Query("""
        SELECT NEW br.com.silviofrancoms.agendasusapikt.model.data.vo.ConsultaVO(
            c.id, 
            c.paciente.id, 
            c.medico.id,
            c.especialidade, 
            c.descricao, 
            c.localAtendimento, 
            c.dataHoraConsulta, 
            c.preferenciaHorario, 
            c.status
        ) 
        FROM consulta c
    """)
    fun findAllConsultaVOs(): List<ConsultaVO>

    // Projeção para buscar todas as ConsultaVOs de um paciente específico
    @Query("""
        SELECT NEW br.com.silviofrancoms.agendasusapikt.model.data.vo.ConsultaVO(
            c.id, 
            c.paciente.id, 
            c.medico.id,
            c.especialidade, 
            c.descricao, 
            c.localAtendimento, 
            c.dataHoraConsulta, 
            c.preferenciaHorario, 
            c.status
        ) 
        FROM consulta c 
        WHERE c.paciente.id = :pacienteId
    """)
    fun findAllConsultaVOsByPacienteId(pacienteId: Long): List<ConsultaVO>

}