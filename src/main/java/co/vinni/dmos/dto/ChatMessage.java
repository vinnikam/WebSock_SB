package co.vinni.dmos.dto;

public class ChatMessage {
    private String sala;
    private String remitente;
    private String contenido;

    public ChatMessage(String sala, String remitente, String contenido) {
        this.sala = sala;
        this.remitente = remitente;
        this.contenido = contenido;
    }

    public String getSala() {
        return sala;
    }

    public void setSala(String sala) {
        this.sala = sala;
    }

    public String getRemitente() {
        return remitente;
    }

    public void setRemitente(String remitente) {
        this.remitente = remitente;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }
}
