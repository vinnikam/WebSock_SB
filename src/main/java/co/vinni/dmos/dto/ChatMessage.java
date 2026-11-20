package co.vinni.dmos.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ChatMessage {
    private String sala;
    private String remitente;
    private String contenido;

}
