package com.nada.nada.data.model;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("INFERIOR")
public class PrendaInferior extends Prenda {
}
