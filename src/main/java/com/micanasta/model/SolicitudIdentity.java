package com.micanasta.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;


import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.io.Serializable;

@Embeddable
public class SolicitudIdentity implements Serializable{

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn (name = "usuario_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "familia_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Familia familia;
}
