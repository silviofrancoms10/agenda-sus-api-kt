package br.com.silviofrancoms.agendasusapikt.model

enum class StatusConsulta(val codigo: Int, val descricao: String) {
    AGENDADA(0, "Agendada"),
    CONFIRMADA(1,"Confirmada pelo Atendente"),
    REALIZADA(2, "Realizada"),
    CANCELADA(3, "Cancelada");

    companion object {
        fun toEnum(codigo: Int?): StatusConsulta? {
            if (codigo == null) return null
            return entries.find { it.codigo == codigo }
                ?: throw IllegalArgumentException("Código de status de consulta inválido: $codigo")
        }
    }

    override fun toString(): String {
        return descricao
    }
}