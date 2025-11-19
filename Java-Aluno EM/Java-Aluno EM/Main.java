import com.sun.jna.Library; // Interface principal para carregar DLLs
import com.sun.jna.Native; // Carrega a biblioteca nativa no Java
import java.util.Scanner; // Para leitura de entrada do console
import javax.swing.JFileChooser; // Não utilizado neste código
import java.io.File; // Não utilizado neste código
import java.io.IOException; // Para lidar com erros de I/O
import java.nio.charset.StandardCharsets; // Codificação de caracteres (UTF-8)
import java.io.FileInputStream; // Para ler dados de arquivo como bytes

public class Main {

    // Mapeia as funções da DLL da impressora
    public interface ImpressoraDLL extends Library {

        // Instancia a DLL (E1_Impressora01.dll) e a carrega usando JNA
        ImpressoraDLL INSTANCE = (ImpressoraDLL) Native.load(
                "C:\\Users\\Andressa\\Desktop\\Java-Aluno EM\\Java-Aluno EM\\E1_Impressora01.dll",
                ImpressoraDLL.class
        );

        // --- Funções da DLL Mapeadas ---

        // Abre a conexão com a impressora
        int AbreConexaoImpressora(int tipo, String modelo, String conexao, int param);
        // Fecha a conexão ativa
        int FechaConexaoImpressora();
        // Imprime um texto
        int ImpressaoTexto(String dados, int posicao, int estilo, int tamanho);
        // Executa um corte de papel
        int Corte(int avanco);
        // Imprime um código QR Code
        int ImpressaoQRCode(String dados, int tamanho, int nivelCorrecao);
        // Imprime um código de barras
        int ImpressaoCodigoBarras(int tipo, String dados, int altura, int largura, int HRI);
        // Avança o papel
        int AvancaPapel(int linhas);
        // Consulta o status da impressora
        int StatusImpressora(int param);
        // Aciona a abertura da gaveta de dinheiro (Elgin)
        int AbreGavetaElgin();
        // Aciona a abertura da gaveta de dinheiro (genérico)
        int AbreGaveta(int pino, int ti, int tf);
        // Emite um sinal sonoro (beep)
        int SinalSonoro(int qtd, int tempoInicio, int tempoFim);
        // Entra no modo de página
        int ModoPagina();
        // Limpa o buffer de impressão do modo página
        int LimpaBufferModoPagina();
        // Imprime o conteúdo bufferizado (modo página)
        int ImprimeModoPagina();
        // Retorna para o modo de impressão padrão
        int ModoPadrao();
        // Define a posição horizontal (modo página)
        int PosicaoImpressaoHorizontal(int posicao);
        // Define a posição vertical (modo página)
        int PosicaoImpressaoVertical(int posicao);
        // Imprime o Extrato SAT Fiscal (XML)
        int ImprimeXMLSAT(String dados, int param);
        // Imprime o Extrato de Cancelamento SAT (XML)
        int ImprimeXMLCancelamentoSAT(String dados, String assQRCode, int param);
    }

    // Status da conexão: true se aberta
    private static boolean conexaoAberta = false;
    // Parâmetros de conexão
    private static int tipo; // 1=USB, 2=Serial, 3=Rede
    private static String modelo;
    private static String conexao; // Ex: USB, COM1, 192.168.0.1
    private static int parametro; // Ex: Baudrate para Serial
    // Scanner para ler a entrada do usuário
    private static final Scanner scanner = new Scanner(System.in);

    // --- Métodos de Controle e Impressão ---

    // Define os parâmetros de conexão
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

    // Abre a conexão com a impressora
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

    // Fecha a conexão ativa
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

    // Imprime um texto digitado pelo usuário
    public static void ImpressaoTexto() {
        if (conexaoAberta) {
            System.out.print("Digite o texto para imprimir: ");
            String texto = scanner.nextLine();

            // Posição: 1=Esquerda, Estilo: 4=Normal, Tamanho: 0=Padrão
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

    // Executa um corte de papel (total)
    public static void Corte() {
        if (conexaoAberta) {
            int retorno = ImpressoraDLL.INSTANCE.Corte(2); // 2 = corte total
            if (retorno == 0) {
                System.out.println("✅ Corte realizado com sucesso.");
            } else {
                System.err.println("❌ Erro ao realizar corte. Código: " + retorno);
            }
        } else {
            System.err.println("❌ Erro: Precisa abrir conexao primeiro.");
        }
    }

    // Imprime um QR Code com dados fornecidos pelo usuário
    public static void ImpressaoQRCode() {
        if (conexaoAberta) {
            System.out.print("Digite o QRCODE para imprimir: ");
            String dados = scanner.nextLine();

            // Tamanho: 6, Nível de correção: 4
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

    // Imprime um Código de Barras (EAN-13 fixo)
    public static void ImpressaoCodigoBarras() {
        if (conexaoAberta) {
            String dados = "{A012345678912"; // Dados de teste EAN-13
            // Tipo: 8 (EAN-13), Altura: 100, Largura: 2, HRI: 3 (posição do texto)
            int retorno = ImpressoraDLL.INSTANCE.ImpressaoCodigoBarras(8, dados, 100, 2, 3);

            if (retorno == 0) {
                System.out.println("✅ Impressão de Cód. Barras realizada com sucesso.");
            } else {
                System.err.println("❌ Erro ao imprimir Cód. Barras. Código: " + retorno);
                System.out.println("DICA: Verifique se a bobina não acabou ou se a tampa está aberta.");
            }
        } else {
            System.err.println("❌ Erro: Precisa abrir conexao primeiro.");
        }
    }

    // Avança o papel
    public static void AvancaPapel() {
        if (conexaoAberta) {
            int retorno = ImpressoraDLL.INSTANCE.AvancaPapel(2); // Avança 2 linhas
            if (retorno == 0) {
                System.out.println("✅ Papel avançado com sucesso.");
            } else {
                System.err.println("❌ Erro ao avançar papel. Código: " + retorno);
            }
        } else {
            System.err.println("❌ Erro: Precisa abrir conexao primeiro.");
        }
    }

    // Aciona a gaveta de dinheiro (Elgin)
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

    // Aciona a gaveta de dinheiro (Genérico)
    public static void AbreGaveta() {
        if (conexaoAberta) {
            int retorno = ImpressoraDLL.INSTANCE.AbreGaveta(1, 5, 10); // Pino 1, tempos 5ms/10ms
            if (retorno == 0) {
                System.out.println("✅ Gaveta Genérica acionada.");
            } else {
                System.err.println("❌ Erro ao acionar Gaveta Genérica. Código: " + retorno);
            }
        } else {
            System.err.println("❌ Erro: Precisa abrir conexao primeiro.");
        }
    }

    // Emite um sinal sonoro (beep)
    public static void SinalSonoro() {
        if (conexaoAberta) {
            int retorno = ImpressoraDLL.INSTANCE.SinalSonoro(4, 5, 5); // 4 beeps
            if (retorno == 0) {
                System.out.println("✅ Sinal sonoro emitido.");
            } else {
                System.err.println("❌ Erro ao emitir sinal sonoro. Código: " + retorno);
            }
        } else {
            System.err.println("❌ Erro: Precisa abrir conexao primeiro.");
        }
    }

    // Imprime Extrato SAT (XML)
    public static void ImprimeXMLSAT() {
        if(conexaoAberta){
            // Caminho fixo do XML (precisa ser real)
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

    // Imprime Cancelamento SAT (XML)
    public static void ImprimeXMLCancelamentoSAT() {
        if(conexaoAberta){
            // Caminho fixo do XML de cancelamento (precisa ser real)
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

    // Método principal
    public static void main(String[] args) {
        // Loop principal do menu
        while (true) {
            // Imprime o menu com o status da conexão
            System.out.println("\n*************************************************");
            System.out.println("**************** MENU IMPRESSORA *******************");
            System.out.println("     (Conexão: " + (conexaoAberta ? "ABERTA" : "FECHADA") + ")");
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

            System.out.print("\nDigite a opção desejada: ");
            String escolha = scanner.nextLine();

            if (escolha.equals("0")) {
                FechaConexaoImpressora(); // Fecha a conexão
                System.out.println("Programa encerrado.");
                break;
            }

            // Executa a função escolhida
            switch (escolha) {
                case "1":
                    configurarConexao();
                    break;
                case "2":
                    AbreConexaoImpressora();
                    break;
                case "3":
                    ImpressoraDLL.INSTANCE.LimpaBufferModoPagina(); // Limpa buffer
                    ImpressaoTexto();
                    ImpressoraDLL.INSTANCE.Corte(4); // Corte parcial
                    break;
                case "4":
                    ImpressaoQRCode();
                    ImpressoraDLL.INSTANCE.Corte(4);
                    break;
                case "5":
                    ImpressaoCodigoBarras();
                    ImpressoraDLL.INSTANCE.Corte(4);
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

                default:
                    System.out.println("OPÇÃO INVÁLIDA");
            }
        }

        scanner.close(); // Fecha o Scanner
    }

    // Lê o conteúdo de um arquivo para uma String (não utilizado nos métodos de impressão)
    private static String lerArquivoComoString(String path) throws IOException {
        FileInputStream fis = new FileInputStream(path);
        byte[] data = fis.readAllBytes();
        fis.close();
        // Converte bytes para String UTF-8
        return new String(data, StandardCharsets.UTF_8);
    }
}