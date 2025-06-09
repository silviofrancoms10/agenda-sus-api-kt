package br.com.silviofrancoms.agendasusapikt.model

enum class Genero(val codigo: Int, val descricao: String) {
    FEMININO(0, "Feminino"),
    MASCULINO(1, "Masculino");

    companion object {
        fun toEnum(codigo: Int?): Genero? {
            if (codigo == null) return null

            return values().find { it.codigo == codigo }
                ?: throw IllegalArgumentException("Id inválido: $codigo")
        }
    }

    override fun toString(): String {
        return descricao
    }
}