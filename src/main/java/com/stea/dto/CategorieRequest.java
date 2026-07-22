package com.stea.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategorieRequest {
    private String nom;
    private String description;
}
