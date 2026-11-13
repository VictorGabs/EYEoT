package br.com.eyeot.model.dtos.domain;

public record DomainResumeDTO( 
    Integer id_domain,
    Integer id_owner,
    String ownerName,
    String ownerEmail,
    String domainName,
    String description,
    Integer numDevices) {
}
