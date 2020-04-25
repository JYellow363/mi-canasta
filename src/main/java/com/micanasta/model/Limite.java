package com.micanasta.model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Limites")
public class Limite {
    @Id
    private long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn (name = "categoria_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Categoria categoria;

    @NotNull
    private float cantidadXPersona;
}
