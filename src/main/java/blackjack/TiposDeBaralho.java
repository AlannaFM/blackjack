package blackjack;



public enum TiposDeBaralho {

    BARALHO1("BlackJack",
            "imagens/r.jpg",
            "imagens/c1.jpg,imagens/c2.jpg,imagens/c3.jpg,imagens/c4.jpg,"
                    + "imagens/c5.jpg,imagens/c6.jpg,imagens/c7.jpg,imagens/c8.jpg,"
                    + "imagens/c9.jpg,imagens/c10.jpg,imagens/c11.jpg,imagens/c12.jpg,"
                    + "imagens/c13.jpg,imagens/d1.jpg,imagens/d2.jpg,imagens/d3.jpg,"
                    + "imagens/d4.jpg,imagens/d5.jpg,imagens/d6.jpg,imagens/d7.jpg,"
                    + "imagens/d8.jpg,imagens/d9.jpg,imagens/d10.jpg,imagens/d11.jpg,"
                    + "imagens/d12.jpg,imagens/d13.jpg,imagens/p1.jpg,imagens/p2.jpg,"
                    + "imagens/p3.jpg,imagens/p4.jpg,imagens/p5.jpg,imagens/p6.jpg,"
                    + "imagens/p7.jpg,imagens/p8.jpg,imagens/p9.jpg,imagens/p10.jpg,"
                    + "imagens/p11.jpg,imagens/p12.jpg,imagens/p13.jpg,imagens/t1.jpg,"
                    + "imagens/t2.jpg,imagens/t3.jpg,imagens/t4.jpg,imagens/t5.jpg,"
                    + "imagens/t6.jpg,imagens/t7.jpg,imagens/t8.jpg,imagens/t9.jpg,"
                    + "imagens/t10.jpg,imagens/t11.jpg,imagens/t12.jpg,imagens/t13.jpg",
            "2,3,4,5,6,7,8,9,10,10,10,10,11,",
            21f, 11f),

    BARALHO2("Sete e Meio",
            "imagens/r.jpg",
            "imagens/b1.jpg,imagens/b2.jpg,imagens/b3.jpg,imagens/b4.jpg,"
                    + "imagens/b5.jpg,imagens/b6.jpg,imagens/b7.jpg,imagens/bs.jpg,"
                    + "imagens/bc.jpg,imagens/br.jpg,imagens/copas1.jpg,imagens/copas2.jpg,"
                    + "imagens/copas3.jpg,imagens/copas4.jpg,imagens/copas5.jpg,"
                    + "imagens/copas6.jpg,imagens/copas7.jpg,imagens/copass.jpg,"
                    + "imagens/copasc.jpg,imagens/copasr.jpg,imagens/e1.jpg,imagens/e2.jpg,"
                    + "imagens/e3.jpg,imagens/e4.jpg,imagens/e5.jpg,imagens/e6.jpg,"
                    + "imagens/e7.jpg,imagens/es.jpg,imagens/ec.jpg,imagens/er.jpg,"
                    + "imagens/o1.jpg,imagens/o2.jpg,imagens/o3.jpg,imagens/o4.jpg,"
                    + "imagens/o5.jpg,imagens/o6.jpg,imagens/o7.jpg,imagens/os.jpg,"
                    + "imagens/oc.jpg,imagens/or.jpg",
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