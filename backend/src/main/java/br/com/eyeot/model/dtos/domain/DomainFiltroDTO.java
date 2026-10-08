package br.com.eyeot.model.dtos.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor 
@NoArgsConstructor 
@Builder 
public class DomainFiltroDTO {

    private String busca;
}
