import com.sun.jna.Library;
import com.sun.jna.Native;
import java.util.Scanner;
import javax.swing.JFileChooser;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.io.FileInputStream;

public class Main {

    public interface ImpressoraDLL extends Library {

        ImpressoraDLL INSTANCE = (ImpressoraDLL) Native.load(
                "C:\\Users\\Andressa\\Desktop\\Java-Aluno EM\\Java-Aluno EM\\E1_Impressora01.dll",
                ImpressoraDLL.class
        );

        int AbreConexaoImpressora(int tipo, String modelo, String conexao, int param);
        int FechaConexaoImpressora();
        int ImpressaoTexto(String dados, int posicao, int estilo, int tamanho);
        int Corte(int avanco);
        int ImpressaoQRCode(String dados, int tamanho, int nivelCorrecao);
        int ImpressaoCodigoBarras(int tipo, String dados, int altura, int largura, int HRI);
        int AvancaPapel(int linhas);
        int StatusImpressora(int param);
        int AbreGavetaElgin();
        int AbreGaveta(int pino, int ti, int tf);
        int SinalSonoro(int qtd, int tempoInicio, int tempoFim);
        int ModoPagina();
        int LimpaBufferModoPagina();
        int ImprimeModoPagina();
        int ModoPadrao();
        int PosicaoImpressaoHorizontal(int posicao);
        int PosicaoImpressaoVertical(int posicao);
        int ImprimeXMLSAT(String dados, int param);
        int ImprimeXMLCancelamentoSAT(String dados, String assQRCode, int param);
    }

    private static boolean conexaoAberta = false;
    private static int tipo;
    private static String modelo;
    private static String conexao;
    private static int parametro;
    private static final Scanner scanner = new Scanner(System.in);

    private static String capturarEntrada(String mensagem) {
        System.out.print(mensagem);
        return scanner.nextLine();
    }

    public static void configurarConexao() {
        if (conexaoAberta) {
            System.out.println("⚠️ Conexão já está aberta. Feche antes de reconfigurar.");
            return;
        }

        try {
            System.out.println("\n--- Configurar Conexão ---");
            System.out.print("Digite o tipo (1=USB, 2=Serial, 3=Rede): ");
            tipo = Integer.parseInt(scanner.nextLine());

            System.out.print("Digite o modelo (ex: i9, i8): ");
            modelo = scanner.nextLine();

            System.out.print("Digite a conexao (ex: USB, COM1, 192.168.0.1): ");
            conexao = scanner.nextLine();

            System.out.print("Digite o parametro (ex: 0 para USB, 9600 para Serial): ");
            parametro = Integer.parseInt(scanner.nextLine());

            System.out.println("✅ Configuração salva.");

        } catch (NumberFormatException e) {
            System.err.println("❌ Erro: Tipo e Parâmetro devem ser números.");
        }
    }

    public static void AbreConexaoImpressora() {
        if (!conexaoAberta) {
            int retorno = ImpressoraDLL.INSTANCE.AbreConexaoImpressora(tipo, modelo, conexao, parametro);
            if (retorno == 0) {
                conexaoAberta = true;
                System.out.println("✅ Conexão aberta com sucesso.");
            } else {
                System.out.println("❌ Erro ao abrir conexão. Código de erro: " + retorno);
            }
        } else {
            System.out.println("⚠️ Conexão já está aberta.");
        }
    }

    public static void FechaConexaoImpressora() {
        if (conexaoAberta) {
            int retorno = ImpressoraDLL.INSTANCE.FechaConexaoImpressora();
            if (retorno == 0) {
                conexaoAberta = false;
                System.out.println("✅ Conexão fechada.");
            } else {
                System.err.println("❌ Erro ao fechar conexão. Código: " + retorno);
            }
        } else {
            System.out.println("⚠️ Conexão já estava fechada.");
        }
    }

    public static void ImpressaoTexto() {
        if (conexaoAberta) {
            String texto = capturarEntrada("Digite o texto para imprimir: ");
            int retorno = ImpressoraDLL.INSTANCE.ImpressaoTexto(texto, 1, 4, 0);
            if (retorno == 0) {
                System.out.println("✅ Impressão de texto realizada com sucesso.");
            } else {
                System.err.println("❌ Erro ao imprimir texto. Código: " + retorno);
            }
        } else {
            System.err.println("❌ Erro: Precisa abrir conexao primeiro.");
        }
    }

    public static void Corte() {
        if (conexaoAberta) {
            int retorno = ImpressoraDLL.INSTANCE.Corte(2);
            if (retorno == 0) {
                System.out.println("✅ Corte realizado com sucesso.");
            } else {
                System.err.println("❌ Erro ao realizar corte. Código: " + retorno);
            }
        } else {
            System.err.println("❌ Erro: Precisa abrir conexao primeiro.");
        }
    }

    public static void ImpressaoQRCode() {
        if (conexaoAberta) {
            String dados = capturarEntrada("Digite o QRCODE para imprimir: ");
            int retorno = ImpressoraDLL.INSTANCE.ImpressaoQRCode(dados, 6,4);
            if (retorno == 0) {
                System.out.println("✅ Impressão de QRCODE realizada com sucesso.");
            } else {
                System.err.println("❌ Erro ao imprimir QRCODE. Código: " + retorno);
            }
        } else {
            System.err.println("❌ Erro: Precisa abrir conexao primeiro.");
        }
    }

    public static void ImpressaoCodigoBarras() {
        if (conexaoAberta) {
            String dados = "{A012345678912";

            int retorno = ImpressoraDLL.INSTANCE.ImpressaoCodigoBarras(8, dados, 100, 2, 3);

            if (retorno == 0) {
                System.out.println("✅ Impressão de Cód. Barras realizada com sucesso.");
            } else {
                System.err.println("❌ Erro ao imprimir Cód. Barras. Código: " + retorno);
                // Dica extra caso dê erro mesmo assim:
                System.out.println("DICA: Verifique se a bobina não acabou ou se a tampa está aberta.");
            }
        } else {
            System.err.println("❌ Erro: Precisa abrir conexao primeiro.");
        }
    }

    public static void AvancaPapel() {
        if (conexaoAberta) {
            int retorno = ImpressoraDLL.INSTANCE.AvancaPapel(2);
            if (retorno == 0) {
                System.out.println("✅ Papel avançado com sucesso.");
            } else {
                System.err.println("❌ Erro ao avançar papel. Código: " + retorno);
            }
        } else {
            System.err.println("❌ Erro: Precisa abrir conexao primeiro.");
        }
    }

    public static void AbreGavetaElgin() {
        if (conexaoAberta) {
            int retorno = ImpressoraDLL.INSTANCE.AbreGavetaElgin();
            if (retorno == 0) {
                System.out.println("✅ Gaveta Elgin acionada.");
            } else {
                System.err.println("❌ Erro ao acionar Gaveta Elgin. Código: " + retorno);
            }
        } else {
            System.err.println("❌ Erro: Precisa abrir conexao primeiro.");
        }
    }

    public static void AbreGaveta() {
        if (conexaoAberta) {
            int retorno = ImpressoraDLL.INSTANCE.AbreGaveta(1, 5, 10);
            if (retorno == 0) {
                System.out.println("✅ Gaveta Genérica acionada.");
            } else {
                System.err.println("❌ Erro ao acionar Gaveta Genérica. Código: " + retorno);
            }
        } else {
            System.err.println("❌ Erro: Precisa abrir conexao primeiro.");
        }
    }

    public static void SinalSonoro() {
        if (conexaoAberta) {
            int retorno = ImpressoraDLL.INSTANCE.SinalSonoro(4, 5, 5);
            if (retorno == 0) {
                System.out.println("✅ Sinal sonoro emitido.");
            } else {
                System.err.println("❌ Erro ao emitir sinal sonoro. Código: " + retorno);
            }
        } else {
            System.err.println("❌ Erro: Precisa abrir conexao primeiro.");
        }
    }

    public static void ImprimeXMLSAT() {
        if(conexaoAberta){

            String dados = "path=C:\\Users\\andressa_accacio\\Downloads\\Java-Aluno EM\\Java-Aluno EM\\Java-Aluno EM\\XMLSAT.xml";

            int retorno = ImpressoraDLL.INSTANCE.ImprimeXMLSAT(dados,0);

            if(retorno == 0){
                System.out.println("XML impresso com sucesso");
            }else{
                System.out.println("Erro. Retorno "+retorno);
            }

        }else{
            System.out.println("Precisa abrir a conexao primeiro");
        }
    }

    public static void ImprimeXMLCancelamentoSAT() {
        if(conexaoAberta){

            String dados = "path=C:\\Users\\andressa_accacio\\Downloads\\Java-Aluno EM\\Java-Aluno EM\\Java-Aluno EM\\CANC_SAT.xml";
            String assQRCode = "Q5DLkpdRijIRGY6YSSNsTWK1TztHL1vD0V1Jc4spo/CEUqICEb9SFy82ym8EhBRZjbh3btsZhF+sjHqEMR159i4agru9x6KsepK/q0E2e5xlU5cv3m1woYfgHyOkWDNcSdMsS6bBh2Bpq6s89yJ9Q6qh/J8YHi306ce9Tqb/drKvN2XdE5noRSS32TAWuaQEVd7u+TrvXlOQsE3fHR1D5f1saUwQLPSdIv01NF6Ny7jZwjCwv1uNDgGZONJdlTJ6p0ccqnZvuE70aHOI09elpjEO6Cd+orI7XHHrFCwhFhAcbalc+ZfO5b/+vkyAHS6CYVFCDtYR9Hi5qgdk31v23w==";

            int retorno = ImpressoraDLL.INSTANCE.ImprimeXMLCancelamentoSAT(dados, assQRCode, 0);

            if(retorno == 0){
                System.out.println("XML impresso com sucesso");
            }else{
                System.out.println("Erro. Retorno "+retorno);
            }

        }else{
            System.out.println("Precisa abrir a conexao primeiro");
        }
    }

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n*************************************************");
            System.out.println("**************** MENU IMPRESSORA *******************");
            System.out.println("       (Conexão: " + (conexaoAberta ? "ABERTA" : "FECHADA") + ")");
            System.out.println("*************************************************\n");

            System.out.println("1  - Configurar Conexao");
            System.out.println("2  - Abrir Conexao");
            System.out.println("-------------------------------------------------");
            System.out.println("3  - Impressao Texto (PADRAO)");
            System.out.println("4  - Impressao QR Code (PADRAO)");
            System.out.println("5  - Impressao Cod Barras (PADRAO)");
            System.out.println("6  - Imprime XML SAT");
            System.out.println("7  - Imprime XML Cancelamento SAT");
            System.out.println("-------------------------------------------------");
            System.out.println("8  - Abrir Gaveta Elgin");
            System.out.println("9  - Abrir Gaveta (Genérica)");
            System.out.println("10 - Emitir Sinal Sonoro");
            System.out.println("\n0  - Fechar Conexao e Sair");

            String escolha = capturarEntrada("\nDigite a opção desejada: ");

            if (escolha.equals("0")) {
                FechaConexaoImpressora();
                System.out.println("Programa encerrado.");
                break;
            }

            switch (escolha) {
                case "1":
                    configurarConexao();
                    break;
                case "2":
                    AbreConexaoImpressora();
                    break;
                case "3":
                    ImpressoraDLL.INSTANCE.LimpaBufferModoPagina();
                    ImpressaoTexto();
                    ImpressoraDLL.INSTANCE.Corte(4);
                    break;
                case "4":
                    ImpressaoQRCode();
                    ImpressoraDLL.INSTANCE.Corte(4);
                    break;
                case "5":
                    ImpressaoCodigoBarras();
                    break;
                case "6":
                    ImprimeXMLSAT();
                    ImpressoraDLL.INSTANCE.Corte(4);
                    break;
                case "7":
                    ImprimeXMLCancelamentoSAT();
                    ImpressoraDLL.INSTANCE.Corte(4);
                    break;
                case "8":
                    AbreGavetaElgin();
                    break;
                case "9":
                    AbreGaveta();
                    break;
                case "10":
                    SinalSonoro();
                    break;
                case "11":
                    AvancaPapel();
                    break;
                case "12":
                    Corte();
                    break;
                default:
                    System.out.println("OPÇÃO INVÁLIDA");
            }
        }

        scanner.close();
    }

    private static String lerArquivoComoString(String path) throws IOException {
        FileInputStream fis = new FileInputStream(path);
        byte[] data = fis.readAllBytes();
        fis.close();
        return new String(data, StandardCharsets.UTF_8);
    }
}
