package br.com.silviofrancoms.agendasusapikt.config

import com.fasterxml.jackson.datatype.hibernate5.jakarta.Hibernate5JakartaModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JacksonConfig {
    @Bean
    fun hibernateModule(): Hibernate5JakartaModule {
        val module = Hibernate5JakartaModule()
        // --- ADICIONE OU DESCOMENTE ESTA LINHA ---
        // Este recurso força o Jackson a serializar coleções lazy (não inicializadas) como 'null',
        // o que geralmente previne L.I.E. e pode ajudar com SOE se houver algum loop com proxies.
        module.enable(Hibernate5JakartaModule.Feature.FORCE_LAZY_LOADING)
        // --- FIM DA ADIÇÃO/DESCOMENTAR ---
        return module
    }
}