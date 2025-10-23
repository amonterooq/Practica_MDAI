package com.nada.nada.data.model;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("CALZADO")
public class PrendaCalzado extends Prenda{
}
