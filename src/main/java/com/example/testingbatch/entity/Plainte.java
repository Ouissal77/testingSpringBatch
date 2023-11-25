package com.example.testingbatch.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Plainte {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String codeFournisseur;
    private String nomFournisseur;
    private String prenomFournisseur;
    private String raisonSociale;
    private String adresseFournisseur;
    private String emailFournisseur;
    private String telephoneFournisseur;
    private String nomDuBanque;
    private String numeroDuCompte;

}
