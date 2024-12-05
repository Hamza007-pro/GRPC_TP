package ma.projet.grpc.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "compte")
public class Compte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // For auto-increment behavior
    private Long id;

    private float solde;
    private String dateCreation;
    private TypeCompte typeCompte;

}
