package br.com.silviofrancoms.agendasusapikt.model

enum class Especialidade(val codigo: Int, val descricao: String) {
    MEDICO(0, "Médico"),
    ENFERMEIRO(1, "Enfermeiro"),
    DENTISTA(2, "Dentista"),
    EXAMES(3, "Exames");

    companion object {
        fun toEnum(codigo: Int?): Especialidade? {
            if (codigo == null) return null
            return entries.find { it.codigo == codigo }
                ?: throw IllegalArgumentException("Código de especialidade inválido: $codigo")
        }
    }

    override fun toString(): String {
        return descricao
    }
}