package application.vendas.domain.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usuario")
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column
    @NotEmpty(message = "{campo.login.obrigatorio}")
    private String login;

    @Column
    @NotEmpty(message = "{campo.senha.obrigatorio}")
    private String senha;

    @Column
    private boolean admin;


}
