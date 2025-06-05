package br.com.silviofrancoms.agendasusapikt.controller

import br.com.silviofrancoms.agendasusapikt.model.data.vo.UsuarioVO
import br.com.silviofrancoms.agendasusapikt.service.UsuarioService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.logging.Logger

@RestController
@RequestMapping("/api/usuarios")
class UsuarioController @Autowired constructor(
    private val service: UsuarioService
) {

    private val logger = Logger.getLogger(UsuarioController::class.java.name)

    @PostMapping(
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun create(@RequestBody usuario: UsuarioVO): ResponseEntity<UsuarioVO> {
        logger.info("Requisição recebida para criar usuário: ${usuario.nomeCompleto}")
        val createdUsuario = service.create(usuario)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUsuario)
    }

    @GetMapping(
        value = ["/{id}"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun findById(@PathVariable(value = "id") id: Long): UsuarioVO {
        logger.info("Requisição recebida para buscar usuário por ID: $id")
        return service.findById(id)
    }

    @GetMapping(
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun findAll(): List<UsuarioVO> {
        logger.info("Requisição recebida para buscar todos os usuários.")
        return service.findAll()
    }

    @PutMapping(
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun update(@RequestBody usuario: UsuarioVO): UsuarioVO {
        logger.info("Requisição recebida para atualizar usuário: ${usuario.id}")
        return service.update(usuario)
    }

    @DeleteMapping(value = ["/{id}"])
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204 No Content para exclusão bem-sucedida
    fun delete(@PathVariable(value = "id") id: Long) {
        logger.info("Requisição recebida para excluir usuário por ID: $id")
        service.delete(id)
    }
}