package br.com.silviofrancoms.agendasusapikt.repository

import br.com.silviofrancoms.agendasusapikt.model.Endereco
import org.springframework.data.jpa.repository.JpaRepository

interface EnderecoRepository : JpaRepository<Endereco, Long> {
}