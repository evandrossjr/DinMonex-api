package com.essjr.DinMonex.IA;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class OllamaService {

    private final ChatClient chatClient;

    public OllamaService(ChatClient.Builder builder){
        this.chatClient = builder.build();
    }

    public String classificar(String description){
        String prompt = """
                Você é um sistema de classificação de despesas pessoais.
                
                Sua tarefa é escolher UMA categoria da lista abaixo.
        
                Categorias permitidas:
                Alimentação
                Transporte
                Saúde
                Educação
                Compras
                Lazer
                Moradia
                Streaming
                Outros
        
                Regras:
                - Nunca crie novas categorias.
                - Responda exatamente uma palavra da lista.
                - Não explique nada.
                - Se tiver dúvida, responda Outros.
        
                Exemplos:
        
                "Uber para trabalho" = Transporte
                "Ônibus" = Transporte
                "Creche infantil" = Educação
                "Escola" = Educação
                "Netflix" = Streaming
                "Spotify" = Streaming
                "Restaurante" = Alimentação
                "Farmácia" = Saúde
        
                Descrição:
                %s
        
                Resposta:
                """.formatted(description);

        String resposta = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        if(resposta == null || resposta.isBlank()){
            return "Outros";
        }

        return resposta.trim();
    }

}
