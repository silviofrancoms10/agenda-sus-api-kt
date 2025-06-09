package br.com.silviofrancoms.agendasusapikt.service

import br.com.silviofrancoms.agendasusapikt.model.Endereco
import br.com.silviofrancoms.agendasusapikt.exception.ResourceNotFoundException
import br.com.silviofrancoms.agendasusapikt.mapper.DozerMapper
import br.com.silviofrancoms.agendasusapikt.model.Usuario
import br.com.silviofrancoms.agendasusapikt.model.data.vo.UsuarioVO
import br.com.silviofrancoms.agendasusapikt.model.data.vo.EnderecoVO // Adicionar esta importação
import br.com.silviofrancoms.agendasusapikt.repository.UsuarioRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.logging.Logger

@Service
class UsuarioService @Autowired constructor(
    private val repository: UsuarioRepository
) {

    private val logger = Logger.getLogger(UsuarioService::class.java.name)
    private val passwordEncoder = BCryptPasswordEncoder()

    @Transactional
    fun create(usuarioVO: UsuarioVO): UsuarioVO {
        logger.info("Criando um novo usuário!")

        // 1. Mapeia o UsuarioVO para Usuario (sem o Endereco por enquanto)
        // Criamos uma cópia do UsuarioVO sem o Endereco para o mapeamento inicial.
        // E garantimos que 'roles' nunca seja nulo para o mapeador.
        val usuarioVOToMap = usuarioVO.copy(
            endereco = null,
            roles = usuarioVO.roles ?: "USUARIO" // <-- ESTA É A LINHA CRÍTICA
        )
        val usuarioEntity: Usuario = DozerMapper.parseObject(usuarioVOToMap, Usuario::class.java)

        // 2. Faz o hash da senha antes de salvar
        usuarioEntity.senha = passwordEncoder.encode(usuarioVO.senha)

        // 3. Mapeia o EnderecoVO para Endereco e define o relacionamento
        usuarioVO.endereco?.let { enderecoVOFromVO ->
            val enderecoEntity: Endereco = DozerMapper.parseObject(enderecoVOFromVO, Endereco::class.java)
            enderecoEntity.usuario = usuarioEntity // Vincula o endereço ao usuário
            usuarioEntity.endereco = enderecoEntity // Vincula o usuário ao endereço
        }

        // 4. Salva a entidade Usuario (que agora tem o Endereco vinculado e será cascateado)
        val savedUsuario = repository.save(usuarioEntity)

        // 5. Converte a Entidade salva de volta para VO e retorna
        return DozerMapper.parseObject(savedUsuario, UsuarioVO::class.java)
    }

    @Transactional(readOnly = true)
    fun findById(id: Long): UsuarioVO {
        logger.info("Buscando um usuário com ID $id!")
        val entity = repository.findById(id)
            .orElseThrow { ResourceNotFoundException("Nenhum registro encontrado para este ID ($id)!") }
        return DozerMapper.parseObject(entity, UsuarioVO::class.java)
    }

    @Transactional(readOnly = true)
    fun findAll(): List<UsuarioVO> {
        logger.info("Buscando todos os usuários!")
        val entities = repository.findAll()
        return DozerMapper.parseListObjects(entities, UsuarioVO::class.java)
    }

    @Transactional
    fun update(usuarioVO: UsuarioVO): UsuarioVO {
        logger.info("Atualizando um usuário com ID ${usuarioVO.id}!")
        val entity = repository.findById(usuarioVO.id!!)
            .orElseThrow { ResourceNotFoundException("Nenhum registro encontrado para este ID (${usuarioVO.id})!") }

        // Atualiza campos de VO para Entidade
        entity.nomeCompleto = usuarioVO.nomeCompleto
        entity.cpf = usuarioVO.cpf
        entity.cns = usuarioVO.cns
        // IMPORTANTE: Apenas atualiza a senha se uma nova for fornecida e precisar ser hash
        if (usuarioVO.senha != null && usuarioVO.senha!!.isNotEmpty()) {
            entity.senha = passwordEncoder.encode(usuarioVO.senha)
        }
        entity.dataNascimento = usuarioVO.dataNascimento
        entity.genero = usuarioVO.genero
        entity.email = usuarioVO.email
        entity.telefone = usuarioVO.telefone
        entity.aceitaTermos = usuarioVO.aceitaTermos
        entity.aceitaNotificacoes = usuarioVO.aceitaNotificacoes
        entity.roles = usuarioVO.roles ?: "USUARIO" // Atualiza roles, com default se nulo

        // Atualiza detalhes do Endereco se fornecidos
        usuarioVO.endereco?.let { enderecoVO ->
            // Se o endereço já existir para o usuário, atualize-o.
            entity.endereco?.apply {
                cep = enderecoVO.cep
                rua = enderecoVO.rua
                numero = enderecoVO.numero
                complemento = enderecoVO.complemento
                bairro = enderecoVO.bairro
                cidade = enderecoVO.cidade
                uf = enderecoVO.uf
            } ?: run {
                // Se o endereço não existir, cria um novo e o vincula.
                val newEndereco = DozerMapper.parseObject(enderecoVO, Endereco::class.java)
                newEndereco.usuario = entity // Vincula o novo endereço ao usuário
                entity.endereco = newEndereco // Atribui o novo endereço ao usuário
            }
        }

        val updatedUsuario = repository.save(entity)
        return DozerMapper.parseObject(updatedUsuario, UsuarioVO::class.java)
    }


    @Transactional
    fun delete(id: Long) {
        logger.info("Excluindo um usuário com ID $id!")
        val entity = repository.findById(id)
            .orElseThrow { ResourceNotFoundException("Nenhum registro encontrado para este ID ($id)!") }
        repository.delete(entity)
    }
}