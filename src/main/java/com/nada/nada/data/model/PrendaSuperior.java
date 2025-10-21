package com.nada.nada.data.model;

import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("SUPERIOR")
public class PrendaSuperior extends Prenda {
}
