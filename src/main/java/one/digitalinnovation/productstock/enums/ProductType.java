package one.digitalinnovation.productstock.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProductType {

    MEDICAMENTO("Medicamento"),
    LIMPEZA("Limpeza"),
    RACAO("Ração"),
    PETISCO("Petisco"),
    BRINQUEDO("Brinquedo"),
    VESTUARIO("Vestuario");

    private final String description;
}
