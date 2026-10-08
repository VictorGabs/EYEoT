package br.com.eyeot.model.dtos.domain;

public record DomainResumeDTO( 
    Integer id_domain,
    String ownerName,
    String domainName,
    String description,
    Integer numDevices) {
}
