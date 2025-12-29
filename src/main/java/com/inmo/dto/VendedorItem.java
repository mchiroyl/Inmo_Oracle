package com.inmo.dto;

/**
 * Item ligero para combos/listas de vendedores.
 * Contiene el id real del vendedor y un nombre mostrado.
 */
public class VendedorItem {
    public final Long id;
    public final String nombre;

    public VendedorItem(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        // Lo que se mostrará en el ComboBox
        return nombre == null ? "" : nombre;
    }
}
