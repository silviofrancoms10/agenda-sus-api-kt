package br.com.silviofrancoms.agendasusapikt.service

import br.com.silviofrancoms.agendasusapikt.model.data.vo.NovaConsultaVO
import br.com.silviofrancoms.agendasusapikt.model.data.vo.ConsultaVO
import br.com.silviofrancoms.agendasusapikt.model.Consulta
import br.com.silviofrancoms.agendasusapikt.model.Especialidade
import br.com.silviofrancoms.agendasusapikt.model.PreferenciaHorario
import br.com.silviofrancoms.agendasusapikt.model.StatusConsulta
import br.com.silviofrancoms.agendasusapikt.model.Usuario
import br.com.silviofrancoms.agendasusapikt.repository.ConsultaRepository
import br.com.silviofrancoms.agendasusapikt.repository.UsuarioRepository
import br.com.silviofrancoms.agendasusapikt.exception.ResourceNotFoundException
import br.com.silviofrancoms.agendasusapikt.mapper.DozerMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit // <-- Adicione esta importação!
import java.util.logging.Logger

@Service
class ConsultaService @Autowired constructor(
    private val consultaRepository: ConsultaRepository,
    private val usuarioRepository: UsuarioRepository
) {

    private val logger = Logger.getLogger(ConsultaService::class.java.name)

    /**
     * Cria uma nova solicitação de consulta.
     * Mapeia NovaConsultaVO para Entidade, salva, e mapeia a Entidade salva para ConsultaVO usando DozerMapper.
     * Define a dataHoraConsulta para 3 meses a partir da data e hora atuais.
     */
    @Transactional
    fun criarSolicitacaoConsulta(pacienteId: Long, requestVO: NovaConsultaVO): ConsultaVO {
        logger.info("Iniciando criação de solicitação de consulta para paciente ID: $pacienteId")

        val paciente: Usuario = usuarioRepository.findById(pacienteId)
            .orElseThrow { ResourceNotFoundException("Paciente com ID $pacienteId não encontrado.") }

        val especialidadeEnum = Especialidade.toEnum(requestVO.especialidade)
            ?: throw IllegalArgumentException("Código de especialidade inválido: ${requestVO.especialidade}")

        val preferenciaHorarioEnum = requestVO.preferenciaHorario?.let {
            PreferenciaHorario.toEnum(it) ?: throw IllegalArgumentException("Código de preferência de horário inválido: $it")
        }

        // --- ALTERAÇÃO AQUI: Definindo dataHoraConsulta para 3 meses à frente ---
        // Calcula a data e hora atual e adiciona 3 meses.
        val dataHoraAgendadaPadrao = LocalDateTime.now().plus(3, ChronoUnit.MONTHS)
        // Se quiser definir uma hora específica (ex: 9h da manhã) para essa data padrão, pode usar:
        // val dataHoraAgendadaPadrao = LocalDateTime.now().plus(3, ChronoUnit.MONTHS).withHour(9).withMinute(0).withSecond(0)
        // -----------------------------------------------------------------------

        val consultaEntity = Consulta(
            paciente = paciente,
            medico = null,
            especialidade = especialidadeEnum,
            descricao = requestVO.descricao,
            localAtendimento = requestVO.localAtendimento,
            dataHoraConsulta = dataHoraAgendadaPadrao, // <-- Agora está preenchido com a data padrão!
            preferenciaHorario = preferenciaHorarioEnum,
            status = StatusConsulta.AGENDADA
        )

        val savedConsulta = consultaRepository.save(consultaEntity)
        logger.info("Solicitação de consulta ID ${savedConsulta.id} criada com sucesso.")

        val retornoVO = DozerMapper.parseObject(savedConsulta, ConsultaVO::class.java)
        retornoVO.pacienteId = savedConsulta.paciente.id
        retornoVO.medicoId = savedConsulta.medico?.id

        return retornoVO
    }

    /**
     * Busca uma ConsultaVO específica por ID usando projeção (mantido como está).
     */
    @Transactional(readOnly = true)
    fun findById(id: Long): ConsultaVO {
        logger.info("Buscando ConsultaVO com ID $id usando projeção.")
        return consultaRepository.findConsultaVOById(id)
            ?: throw ResourceNotFoundException("Nenhum registro de consulta encontrado para este ID ($id)!")
    }

    /**
     * Busca todas as ConsultaVOs usando projeção (mantido como está).
     */
    @Transactional(readOnly = true)
    fun findAll(): List<ConsultaVO> {
        logger.info("Buscando todas as ConsultaVOs usando projeção.")
        return consultaRepository.findAllConsultaVOs()
    }

    /**
     * Busca todas as ConsultaVOs associadas a um ID de paciente específico usando projeção (mantido como está).
     */
    @Transactional(readOnly = true)
    fun findAllByPacienteId(pacienteId: Long): List<ConsultaVO> {
        logger.info("Buscando todas as ConsultaVOs para o paciente ID: $pacienteId usando projeção.")
        usuarioRepository.findById(pacienteId)
            .orElseThrow { ResourceNotFoundException("Paciente com ID $pacienteId não encontrado.") }

        return consultaRepository.findAllConsultaVOsByPacienteId(pacienteId)
    }

    /**
     * Atualiza uma consulta existente.
     * Busca a entidade, atualiza os campos com base no ConsultaVO de entrada, salva, e retorna o VO mapeado.
     */
    @Transactional
    fun updateConsultaByAttendant(consultaVO: ConsultaVO): ConsultaVO {
        logger.info("Atualizando consulta ID ${consultaVO.id} pelo atendente.")
        val entity = consultaRepository.findById(consultaVO.id!!)
            .orElseThrow { ResourceNotFoundException("Consulta com ID ${consultaVO.id} não encontrada!") }

        // Atualiza campos da entidade
        consultaVO.dataHoraConsulta?.let { entity.dataHoraConsulta = it }
        consultaVO.localAtendimento?.let { entity.localAtendimento = it }
        consultaVO.descricao?.let { entity.descricao = it }

        consultaVO.medicoId?.let { medicoId ->
            val medico = usuarioRepository.findById(medicoId)
                .orElseThrow { ResourceNotFoundException("Médico com ID $medicoId não encontrado!") }
            entity.medico = medico
        } ?: run {
            // Se medicoId for nulo no VO, significa que o médico deve ser desvinculado (se for o caso)
            entity.medico = null
        }

        consultaVO.status?.let { entity.status = it }

        val updatedEntity = consultaRepository.save(entity)
        logger.info("Consulta ID ${updatedEntity.id} atualizada com sucesso.")

        val retornoVO = DozerMapper.parseObject(updatedEntity, ConsultaVO::class.java)
        retornoVO.pacienteId = updatedEntity.paciente.id
        retornoVO.medicoId = updatedEntity.medico?.id

        return retornoVO
    }

    /**
     * Exclui uma consulta pelo ID.
     */
    @Transactional
    fun delete(id: Long) {
        logger.info("Excluindo consulta com ID $id.")
        val entity = consultaRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Nenhum registro de consulta encontrado para este ID ($id)!") }
        consultaRepository.delete(entity)
        logger.info("Consulta ID $id excluída com sucesso.")
    }
}