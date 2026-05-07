package blackjack;



public enum TiposDeBaralho {

    BARALHA1("BlackJack",
            "imagenes/r.jpg",
            "imagenes/c1.jpg,imagenes/c2.jpg,imagenes/c3.jpg,imagenes/c4.jpg,"
                    + "imagenes/c5.jpg,imagenes/c6.jpg,imagenes/c7.jpg,imagenes/c8.jpg,"
                    + "imagenes/c9.jpg,imagenes/c10.jpg,imagenes/c11.jpg,imagenes/c12.jpg,"
                    + "imagenes/c13.jpg,imagenes/d1.jpg,imagenes/d2.jpg,imagenes/d3.jpg,"
                    + "imagenes/d4.jpg,imagenes/d5.jpg,imagenes/d6.jpg,imagenes/d7.jpg,"
                    + "imagenes/d8.jpg,imagenes/d9.jpg,imagenes/d10.jpg,imagenes/d11.jpg,"
                    + "imagenes/d12.jpg,imagenes/d13.jpg,imagenes/p1.jpg,imagenes/p2.jpg,"
                    + "imagenes/p3.jpg,imagenes/p4.jpg,imagenes/p5.jpg,imagenes/p6.jpg,"
                    + "imagenes/p7.jpg,imagenes/p8.jpg,imagenes/p9.jpg,imagenes/p10.jpg,"
                    + "imagenes/p11.jpg,imagenes/p12.jpg,imagenes/p13.jpg,imagenes/t1.jpg,"
                    + "imagenes/t2.jpg,imagenes/t3.jpg,imagenes/t4.jpg,imagenes/t5.jpg,"
                    + "imagenes/t6.jpg,imagenes/t7.jpg,imagenes/t8.jpg,imagenes/t9.jpg,"
                    + "imagenes/t10.jpg,imagenes/t11.jpg,imagenes/t12.jpg,imagenes/t13.jpg",
            "2,3,4,5,6,7,8,9,10,10,10,10,11,",
            21f, 11f),

    BARALHA2("Sete e Meio",
            "imagenes/r.jpg",
            "imagenes/b1.jpg,imagenes/b2.jpg,imagenes/b3.jpg,imagenes/b4.jpg,"
                    + "imagenes/b5.jpg,imagenes/b6.jpg,imagenes/b7.jpg,imagenes/bs.jpg,"
                    + "imagenes/bc.jpg,imagenes/br.jpg,imagenes/copas1.jpg,imagenes/copas2.jpg,"
                    + "imagenes/copas3.jpg,imagenes/copas4.jpg,imagenes/copas5.jpg,"
                    + "imagenes/copas6.jpg,imagenes/copas7.jpg,imagenes/copass.jpg,"
                    + "imagenes/copasc.jpg,imagenes/copasr.jpg,imagenes/e1.jpg,imagenes/e2.jpg,"
                    + "imagenes/e3.jpg,imagenes/e4.jpg,imagenes/e5.jpg,imagenes/e6.jpg,"
                    + "imagenes/e7.jpg,imagenes/es.jpg,imagenes/ec.jpg,imagenes/er.jpg,"
                    + "imagenes/o1.jpg,imagenes/o2.jpg,imagenes/o3.jpg,imagenes/o4.jpg,"
                    + "imagenes/o5.jpg,imagenes/o6.jpg,imagenes/o7.jpg,imagenes/os.jpg,"
                    + "imagenes/oc.jpg,imagenes/or.jpg",
            "1,2,3,4,5,6,7,0.5,0.5,0.5,",
            7.5f, 7f);

    private final String nome;
    private final String imagemReverso;
    private final String imagensCartas;
    private final String valoresStr;
    private final float limite;
    private final float valorMaximo;

    TiposDeBaralho(String nome, String imagemReverso, String imagensCartas,
                   String valoresStr, float limite, float valorMaximo) {
        this.nome = nome;
        this.imagemReverso = imagemReverso;
        this.imagensCartas = imagensCartas;
        this.valoresStr = valoresStr;
        this.limite = limite;
        this.valorMaximo = valorMaximo;
    }

    public String getNome()           { return nome; }
    public String getImagemReverso()  { return imagemReverso; }
    public float getLimite()          { return limite; }
    public float getValorMaximo()     { return valorMaximo; }

    public String[] getImagensCartas() {
        return imagensCartas.split(",");
    }

    public Float[] getValores() {
        String[] partes = valoresStr.split(",");
        Float[] ret = new Float[partes.length];
        for (int i = 0; i < partes.length; i++)
            ret[i] = Float.valueOf(partes[i]);
        return ret;
    }

    public int totalDeCartas() {
        return imagensCartas.split(",").length;
    }

    public static String[] nomesBaralho() {
        TiposDeBaralho[] vals = values();
        String[] nomes = new String[vals.length];
        for (int i = 0; i < vals.length; i++)
            nomes[i] = vals[i].getNome();
        return nomes;
    }

    public static TiposDeBaralho buscarBaralho(String nome) {
        for (TiposDeBaralho b : values())
            if (b.getNome().equals(nome)) return b;
        return null;
    }

    public static String getReverso() {
        return values()[0].getImagemReverso();
    }
}