package br.com.eyeot.model.dtos;

public record DomainResumeDTO( 
    Integer id_domain,
    String ownerName,
    String domainName,
    String description,
    Integer numDevices) {
}
