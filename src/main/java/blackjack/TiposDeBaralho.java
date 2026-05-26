package blackjack;

/**
 * c=copas, d=ouros, p=paus, t=espadas,
 * numeradas de 1 a 13 (1=Ás, 11=Valete, 12=Dama, 13=Rei).
 *
 * ás=11 (ajustado para 1 por {@link JogadorBlackJack} se necessário),
 * 2-10=valor nominal, valete/dama/rei=10.
 */
public final class TiposDeBaralho {

    // ── Constantes do baralho ─────────────────────────────────────────────────

    public static final String NOME          = "BlackJack";
    public static final float  LIMITE        = 21f;
    public static final float  VALOR_MAXIMO  = 11f;
    public static final String IMAGEM_REVERSO = "imagens/r.jpg";

    /** Imagens das 52 cartas (4 naipes × 13 cartas). */
    private static final String[] IMAGENS_CARTAS = {
            "imagens/c1.jpg",  "imagens/c2.jpg",  "imagens/c3.jpg",  "imagens/c4.jpg",
            "imagens/c5.jpg",  "imagens/c6.jpg",  "imagens/c7.jpg",  "imagens/c8.jpg",
            "imagens/c9.jpg",  "imagens/c10.jpg", "imagens/c11.jpg", "imagens/c12.jpg", "imagens/c13.jpg",
            "imagens/d1.jpg",  "imagens/d2.jpg",  "imagens/d3.jpg",  "imagens/d4.jpg",
            "imagens/d5.jpg",  "imagens/d6.jpg",  "imagens/d7.jpg",  "imagens/d8.jpg",
            "imagens/d9.jpg",  "imagens/d10.jpg", "imagens/d11.jpg", "imagens/d12.jpg", "imagens/d13.jpg",
            "imagens/p1.jpg",  "imagens/p2.jpg",  "imagens/p3.jpg",  "imagens/p4.jpg",
            "imagens/p5.jpg",  "imagens/p6.jpg",  "imagens/p7.jpg",  "imagens/p8.jpg",
            "imagens/p9.jpg",  "imagens/p10.jpg", "imagens/p11.jpg", "imagens/p12.jpg", "imagens/p13.jpg",
            "imagens/t1.jpg",  "imagens/t2.jpg",  "imagens/t3.jpg",  "imagens/t4.jpg",
            "imagens/t5.jpg",  "imagens/t6.jpg",  "imagens/t7.jpg",  "imagens/t8.jpg",
            "imagens/t9.jpg",  "imagens/t10.jpg", "imagens/t11.jpg", "imagens/t12.jpg", "imagens/t13.jpg"
    };

    /**
     * Valores por posição dentro de cada naipe (índice 0–12):
     * Ás=11, 2–10=valor nominal, Valete=10, Dama=10, Rei=10.
     */
    private static final float[] VALORES = {
            11f, 2f, 3f, 4f, 5f, 6f, 7f, 8f, 9f, 10f, 10f, 10f, 10f
    };

    // ── API pública ───────────────────────────────────────────────────────────

    public static String[] getImagensCartas() {
        return IMAGENS_CARTAS.clone();
    }

    public static float[] getValores() {
        return VALORES.clone();
    }

    public static int totalDeCartas() {
        return IMAGENS_CARTAS.length;
    }

    // Construtor privado: classe utilitária, não deve ser instanciada.
    private TiposDeBaralho() {}
}
