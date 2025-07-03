package br.com.silviofrancoms.agendasusapikt.model

enum class PreferenciaHorario(val codigo: Int, val descricao: String) {
    MATUTINO(0, "Matutino"),
    VESPERTINO(1, "Vespertino");

    companion object {
        fun toEnum(codigo: Int?): PreferenciaHorario? {
            if (codigo == null) return null
            return entries.find { it.codigo == codigo }
                ?: throw IllegalArgumentException("Código de preferência de horário inválido: $codigo")
        }
    }

    override fun toString(): String {
        return descricao
    }
}