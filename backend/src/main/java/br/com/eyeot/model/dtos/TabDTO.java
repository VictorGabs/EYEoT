package br.com.eyeot.model.dtos;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@Builder 
@AllArgsConstructor 
@NoArgsConstructor 
public class TabDTO {
    private UUID uuid;
    private String routerLink;
    private String icon;
    private String span;
}