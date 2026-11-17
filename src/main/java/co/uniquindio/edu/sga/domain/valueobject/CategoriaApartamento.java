package co.uniquindio.edu.sga.domain.valueobject;

/** Categoría comercial del apartamento y unidad usada para calcular su tarifa. */
public enum CategoriaApartamento {
    STANDARD(3, 2, false),
    PREMIUM(6, 3, true);

    private final int aforoMaximo;
    private final int cantidadDormitorios;
    private final boolean tarifaPorPersona;

    CategoriaApartamento(int aforoMaximo, int cantidadDormitorios, boolean tarifaPorPersona) {
        this.aforoMaximo = aforoMaximo;
        this.cantidadDormitorios = cantidadDormitorios;
        this.tarifaPorPersona = tarifaPorPersona;
    }

    public int aforoMaximo() { return aforoMaximo; }
    public int cantidadDormitorios() { return cantidadDormitorios; }
    public boolean tarifaPorPersona() { return tarifaPorPersona; }
}
