package br.com.silviofrancoms.agendasusapikt.controller

import br.com.silviofrancoms.agendasusapikt.model.data.vo.NovaConsultaVO // VO de ENTRADA (para solicitação do paciente)
import br.com.silviofrancoms.agendasusapikt.model.data.vo.ConsultaVO // VO COMPLETO (para respostas e outras operações)
import br.com.silviofrancoms.agendasusapikt.service.ConsultaService
import br.com.silviofrancoms.agendasusapikt.exception.ResourceNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.logging.Logger

@RestController
@RequestMapping("/api/consultas")
class ConsultaController @Autowired constructor(
    private val service: ConsultaService
) {

    private val logger = Logger.getLogger(ConsultaController::class.java.name)

    /**
     * Endpoint para o paciente enviar uma nova solicitação de consulta.
     * @param pacienteId O ID do paciente que faz a solicitação.
     * @param requestVO O corpo da requisição, contendo apenas os dados que o paciente informa.
     * @return ResponseEntity com o ConsultaVO da consulta criada ou um status de erro.
     */
    @PostMapping(
        value = ["/solicitar/{pacienteId}"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun solicitarConsulta(
        @PathVariable pacienteId: Long,
        @RequestBody requestVO: NovaConsultaVO
    ): ResponseEntity<ConsultaVO> {
        logger.info("Recebida solicitação de consulta para paciente ID $pacienteId.")

        return try {
            val createdConsulta = service.criarSolicitacaoConsulta(pacienteId, requestVO)
            ResponseEntity.status(HttpStatus.CREATED).body(createdConsulta)
        } catch (e: Exception) {
            logger.severe("Erro ao solicitar consulta: ${e.message}")
            when (e) {
                is ResourceNotFoundException -> ResponseEntity.status(HttpStatus.NOT_FOUND).build()
                is IllegalArgumentException -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null)
                else -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null)
            }
        }
    }

    /**
     * Endpoint para buscar uma consulta específica por ID.
     * @param id O ID da consulta a ser buscada.
     * @return O ConsultaVO completo da consulta encontrada.
     * @throws ResourceNotFoundException se a consulta não for encontrada.
     */
    @GetMapping(
        value = ["/{id}"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun findById(@PathVariable(value = "id") id: Long): ConsultaVO {
        logger.info("Requisição recebida para buscar consulta por ID: $id")
        return service.findById(id)
    }

    /**
     * Endpoint para buscar todas as consultas no sistema.
     * @return Uma lista de ConsultaVOs.
     */
    @GetMapping(
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun findAll(): List<ConsultaVO> {
        logger.info("Requisição recebida para buscar todas as consultas.")
        return service.findAll()
    }

    /**
     * Endpoint para buscar todas as consultas de um paciente específico.
     * @param pacienteId O ID do paciente.
     * @return Uma lista de ConsultaVOs.
     * @throws ResourceNotFoundException se o paciente não for encontrado.
     */
    @GetMapping(
        value = ["/paciente/{pacienteId}"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun findAllByPacienteId(@PathVariable(value = "pacienteId") pacienteId: Long): List<ConsultaVO> {
        logger.info("Requisição recebida para buscar todas as consultas para o paciente ID: $pacienteId")
        return service.findAllByPacienteId(pacienteId)
    }

    /**
     * Endpoint para atualizar uma consulta existente.
     * @param consultaVO O ConsultaVO com os dados da consulta a ser atualizada.
     * @return ResponseEntity com o ConsultaVO atualizado ou um status de erro.
     */
    @PutMapping(
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun update(@RequestBody consultaVO: ConsultaVO): ResponseEntity<ConsultaVO> {
        logger.info("Requisição recebida para atualizar consulta ID: ${consultaVO.id}")
        return try {
            val updatedConsulta = service.updateConsultaByAttendant(consultaVO)
            ResponseEntity.ok(updatedConsulta)
        } catch (e: Exception) {
            logger.severe("Erro ao atualizar consulta: ${e.message}")
            when (e) {
                is ResourceNotFoundException -> ResponseEntity.status(HttpStatus.NOT_FOUND).build()
                is IllegalArgumentException -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null)
                else -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null)
            }
        }
    }

    /**
     * Endpoint para excluir uma consulta pelo ID.
     * @param id O ID da consulta a ser excluída.
     * @return ResponseEntity com status NO_CONTENT.
     * @throws ResourceNotFoundException se a consulta não for encontrada.
     */
    @DeleteMapping(value = ["/{id}"])
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable(value = "id") id: Long) {
        logger.info("Requisição recebida para excluir consulta por ID: $id")
        service.delete(id)
    }
}