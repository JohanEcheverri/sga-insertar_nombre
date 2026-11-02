package co.uniquindio.edu.sga.application.support;

/**
 * I-04 / I-05 - Ejecuta un efecto secundario no esencial (notificación, IA) sin que su fallo
 * detenga la operación de negocio: el error se registra y se continúa.
 */
public final class Seguro {

    private static final System.Logger LOG = System.getLogger(Seguro.class.getName());

    private Seguro() { }

    public static void ejecutar(String contexto, Runnable accion) {
        try {
            accion.run();
        } catch (RuntimeException e) {
            LOG.log(System.Logger.Level.WARNING, "Fallo no bloqueante en " + contexto + ": " + e.getMessage(), e);
        }
    }
}
