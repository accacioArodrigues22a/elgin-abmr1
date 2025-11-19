import com.sun.jna.Library; // importa a classe base para mapear bibliotecas nativas (DLLs)
import com.sun.jna.Native; // importa a classe para carregar a biblioteca nativa
import java.util.Scanner; // importa a classe para ler a entrada do usuário
import javax.swing.JFileChooser; // importa para permitir a seleção de arquivos (embora não esteja sendo usado neste código, foi mantido como estava)
import java.io.File; // importa a classe file (não está sendo usado, mas foi mantido como estava)
import java.io.IOException; // importa para lidar com exceções de entrada/saída
import java.nio.charset.StandardCharsets; // importa para especificar o encoding (utf-8)
import java.io.FileInputStream; // importa para ler o conteúdo de um arquivo

// a classe principal que contém o menu e a lógica de comunicação com a impressora
public class Main {

    // esta interface é o coração do jna: ela mapeia as funções da dll para métodos java
    public interface ImpressoraDLL extends Library {

        // a instância única que carrega a dll. o jna se encarrega de fazer a ponte
        ImpressoraDLL INSTANCE = (ImpressoraDLL) Native.load(
                // o caminho da dll no sistema. atenção ao caminho!
                "C:\\Users\\Andressa\\Desktop\\Java-Aluno EM\\Java-Aluno EM\\E1_Impressora01.dll",
                ImpressoraDLL.class // a interface que define as funções da dll
        );

        // método para iniciar a conexão com a impressora
        int AbreConexaoImpressora(int tipo, String modelo, String conexao, int param);
        // método para encerrar a conexão com a impressora
        int FechaConexaoImpressora();
        // método para imprimir um texto com formatação
        int ImpressaoTexto(String dados, int posicao, int estilo, int tamanho);
        // método para cortar o papel. 'avanco' pode ser o tipo de corte ou avanço antes do corte
        int Corte(int avanco);
        // método para imprimir um qrcode. (dados, tamanho do módulo, nível de correção)
        int ImpressaoQRCode(String dados, int tamanho, int nivelCorrecao);
        // método para imprimir um código de barras. (tipo, dados, altura, largura, hri)
        int ImpressaoCodigoBarras(int tipo, String dados, int altura, int largura, int HRI);
        // método para avançar o papel em 'linhas'
        int AvancaPapel(int linhas);
        // método para checar o status atual da impressora (se está com papel, tampa aberta, etc.)
        int StatusImpressora(int param);
        // método específico para abrir a gaveta de dinheiro no padrão elgin
        int AbreGavetaElgin();
        // método genérico para abrir a gaveta de dinheiro (pino, tempo inicial, tempo final)
        int AbreGaveta(int pino, int ti, int tf);
        // método para emitir um sinal sonoro (beep)
        int SinalSonoro(int qtd, int tempoInicio, int tempoFim);
        // métodos para usar o "modo página", permitindo posicionar impressões livremente
        int ModoPagina();
        int LimpaBufferModoPagina();
        int ImprimeModoPagina();
        int ModoPadrao();
        int PosicaoImpressaoHorizontal(int posicao);
        int PosicaoImpressaoVertical(int posicao);
        // método para imprimir um xml sat (cupom fiscal eletrônico paulista)
        int ImprimeXMLSAT(String dados, int param);
        // método para imprimir o cancelamento de um xml sat
        int ImprimeXMLCancelamentoSAT(String dados, String assQRCode, int param);
    }

    // variável de controle para saber se a conexão está ativa
    private static boolean conexaoAberta = false;
    // variáveis para armazenar a configuração da conexão
    private static int tipo; // usb, serial, rede
    private static String modelo; // i9, i8, etc.
    private static String conexao; // usb, com1, 192.168.0.1
    private static int parametro; // 0 para usb, baudrate para serial
    // objeto scanner para ler a entrada do console do usuário
    private static final Scanner scanner = new Scanner(System.in);

    // função auxiliar para pegar a entrada do usuário de forma mais limpa
    private static String capturarEntrada(String mensagem) {
        System.out.print(mensagem);
        return scanner.nextLine();
    }

    // função para configurar as informações de conexão que serão usadas depois
    public static void configurarConexao() {
        // impede a reconfiguração se a conexão já estiver aberta
        if (conexaoAberta) {
            System.out.println("⚠️ conexão já está aberta. feche antes de reconfigurar.");
            return;
        }

        try {
            System.out.println("\n--- configurar conexão ---");
            // captura o tipo de conexão e converte para int
            System.out.print("digite o tipo (1=usb, 2=serial, 3=rede): ");
            tipo = Integer.parseInt(scanner.nextLine());

            // captura o modelo da impressora
            System.out.print("digite o modelo (ex: i9, i8): ");
            modelo = scanner.nextLine();

            // captura o endereço de conexão (porta, ip, nome)
            System.out.print("digite a conexao (ex: usb, com1, 192.168.0.1): ");
            conexao = scanner.nextLine();

            // captura o parâmetro de conexão (baudrate ou 0)
            System.out.print("digite o parametro (ex: 0 para usb, 9600 para serial): ");
            parametro = Integer.parseInt(scanner.nextLine());

            System.out.println("✅ configuração salva.");

        } catch (NumberFormatException e) {
            // tratamento de erro caso o usuário digite texto onde deveria ser número
            System.err.println("❌ erro: tipo e parâmetro devem ser números.");
        }
    }

    // função que, de fato, abre a conexão com a impressora
    public static void AbreConexaoImpressora() {
        // só tenta abrir se ainda não estiver aberta
        if (!conexaoAberta) {
            // chama a função da dll com os parâmetros configurados
            int retorno = ImpressoraDLL.INSTANCE.AbreConexaoImpressora(tipo, modelo, conexao, parametro);
            if (retorno == 0) {
                // sucesso
                conexaoAberta = true;
                System.out.println("✅ conexão aberta com sucesso.");
            } else {
                // falha
                System.out.println("❌ erro ao abrir conexão. código de erro: " + retorno);
            }
        } else {
            System.out.println("⚠️ conexão já está aberta.");
        }
    }

    // função que fecha a conexão com a impressora
    public static void FechaConexaoImpressora() {
        // só tenta fechar se estiver aberta
        if (conexaoAberta) {
            int retorno = ImpressoraDLL.INSTANCE.FechaConexaoImpressora();
            if (retorno == 0) {
                // sucesso
                conexaoAberta = false;
                System.out.println("✅ conexão fechada.");
            } else {
                // falha
                System.err.println("❌ erro ao fechar conexão. código: " + retorno);
            }
        } else {
            System.out.println("⚠️ conexão já estava fechada.");
        }
    }

    // função para demonstrar a impressão de texto
    public static void ImpressaoTexto() {
        if (conexaoAberta) {
            String texto = capturarEntrada("digite o texto para imprimir: ");
            // imprime o texto. (texto, posição, estilo, tamanho)
            int retorno = ImpressoraDLL.INSTANCE.ImpressaoTexto(texto, 1, 4, 0);
            if (retorno == 0) {
                System.out.println("✅ impressão de texto realizada com sucesso.");
            } else {
                System.err.println("❌ erro ao imprimir texto. código: " + retorno);
            }
        } else {
            System.err.println("❌ erro: precisa abrir conexao primeiro.");
        }
    }

    // função para demonstrar o corte de papel
    public static void Corte() {
        if (conexaoAberta) {
            // executa o corte (param: 1=corte total, 2=corte parcial, 3=avançar papel)
            int retorno = ImpressoraDLL.INSTANCE.Corte(2);
            if (retorno == 0) {
                System.out.println("✅ corte realizado com sucesso.");
            } else {
                System.err.println("❌ erro ao realizar corte. código: " + retorno);
            }
        } else {
            System.err.println("❌ erro: precisa abrir conexao primeiro.");
        }
    }

    // função para demonstrar a impressão de qrcode
    public static void ImpressaoQRCode() {
        if (conexaoAberta) {
            String dados = capturarEntrada("digite o texto para imprimir: ");
            // imprime o qrcode (dados, tamanho do módulo (1-16), nível de correção (1-4))
            int retorno = ImpressoraDLL.INSTANCE.ImpressaoQRCode(dados, 6,4);
            if (retorno == 0) {
                System.out.println("✅ impressão de qrcode realizada com sucesso.");
            } else {
                System.err.println("❌ erro ao imprimir qrcode. código: " + retorno);
            }
        } else {
            System.err.println("❌ erro: precisa abrir conexao primeiro.");
        }
    }

    // função para demonstrar a impressão de código de barras
    public static void ImpressaoCodigoBarras() {
        if (conexaoAberta) {
            // dados de exemplo para o código de barras (code 128)
            String dados = "{A012345678912";

            // imprime o código de barras (tipo (ex: 8=code 128), dados, altura, largura, hri (posição dos números))
            int retorno = ImpressoraDLL.INSTANCE.ImpressaoCodigoBarras(8, dados, 100, 2, 3);

            if (retorno == 0) {
                System.out.println("✅ impressão de cód. barras realizada com sucesso.");
            } else {
                System.err.println("❌ erro ao imprimir cód. barras. código: " + retorno);
                // dica para o usuário em caso de erro comum
                System.out.println("dica: verifique se a bobina não acabou ou se a tampa está aberta.");
            }
        } else {
            System.err.println("❌ erro: precisa abrir conexao primeiro.");
        }
    }

    // função para avançar o papel
    public static void AvancaPapel() {
        if (conexaoAberta) {
            // avança o papel em 2 linhas
            int retorno = ImpressoraDLL.INSTANCE.AvancaPapel(2);
            if (retorno == 0) {
                System.out.println("✅ papel avançado com sucesso.");
            } else {
                System.err.println("❌ erro ao avançar papel. código: " + retorno);
            }
        } else {
            System.err.println("❌ erro: precisa abrir conexao primeiro.");
        }
    }

    // função para abrir a gaveta de dinheiro (modelo elgin)
    public static void AbreGavetaElgin() {
        if (conexaoAberta) {
            int retorno = ImpressoraDLL.INSTANCE.AbreGavetaElgin();
            if (retorno == 0) {
                System.out.println("✅ gaveta elgin acionada.");
            } else {
                System.err.println("❌ erro ao acionar gaveta elgin. código: " + retorno);
            }
        } else {
            System.err.println("❌ erro: precisa abrir conexao primeiro.");
        }
    }

    // função para abrir a gaveta de dinheiro (genérica)
    public static void AbreGaveta() {
        if (conexaoAberta) {
            // abre a gaveta (pino, tempo inicial, tempo final)
            int retorno = ImpressoraDLL.INSTANCE.AbreGaveta(1, 5, 10);
            if (retorno == 0) {
                System.out.println("✅ gaveta genérica acionada.");
            } else {
                System.err.println("❌ erro ao acionar gaveta genérica. código: " + retorno);
            }
        } else {
            System.err.println("❌ erro: precisa abrir conexao primeiro.");
        }
    }

    // função para emitir um sinal sonoro (beep)
    public static void SinalSonoro() {
        if (conexaoAberta) {
            // emite 4 bipes (quantidade, tempo inicial, tempo final)
            int retorno = ImpressoraDLL.INSTANCE.SinalSonoro(4, 5, 5);
            if (retorno == 0) {
                System.out.println("✅ sinal sonoro emitido.");
            } else {
                System.err.println("❌ erro ao emitir sinal sonoro. código: " + retorno);
            }
        } else {
            System.err.println("❌ erro: precisa abrir conexao primeiro.");
        }
    }

    // função para imprimir um xml do sat
    public static void ImprimeXMLSAT() {
        if(conexaoAberta){

            // a string contém o caminho para o xml sat
            String dados = "path=C:\\Users\\andressa_accacio\\Downloads\\Java-Aluno EM\\Java-Aluno EM\\Java-Aluno EM\\XMLSAT.xml";

            // imprime o xml (caminho, parâmetro extra)
            int retorno = ImpressoraDLL.INSTANCE.ImprimeXMLSAT(dados,0);

            if(retorno == 0){
                System.out.println("xml impresso com sucesso");
            }else{
                System.out.println("erro. retorno "+retorno);
            }

        }else{
            System.out.println("precisa abrir a conexao primeiro");
        }
    }

    // função para imprimir o cancelamento de um xml sat
    public static void ImprimeXMLCancelamentoSAT() {
        if(conexaoAberta){

            // caminho para o xml de cancelamento
            String dados = "path=C:\\Users\\andressa_accacio\\Downloads\\Java-Aluno EM\\Java-Aluno EM\\Java-Aluno EM\\CANC_SAT.xml";
            // assinatura qrcode (geralmente fornecida pelo sat)
            String assQRCode = "Q5DLkpdRijIRGY6YSSNsTWK1TztHL1vD0V1Jc4spo/CEUqICEb9SFy82ym8EhBRZjbh3btsZhF+sjHqEMR159i4agru9x6KsepK/q0E2e5xlU5cv3m1woYfgHyOkWDNcSdMsS6bBh2Bpq6s89yJ9Q6qh/J8YHi306ce9Tqb/drKvN2XdE5noRSS32TAWuaQEVd7u+TrvXlOQsE3fHR1D5f1saUwQLPSdIv01NF6Ny7jZwjCwv1uNDgGZONJdlTJ6p0ccqnZvuE70aHOI09elpjEO6Cd+orI7XHHrFCwhFhAcbalc+ZfO5b/+vkyAHS6CYVFCDtYR9Hi5qgdk31v23w==";

            // imprime o cancelamento (caminho do xml, assinatura qrcode, parâmetro extra)
            int retorno = ImpressoraDLL.INSTANCE.ImprimeXMLCancelamentoSAT(dados, assQRCode, 0);

            if(retorno == 0){
                System.out.println("xml impresso com sucesso");
            }else{
                System.out.println("erro. retorno "+retorno);
            }

        }else{
            System.out.println("precisa abrir a conexao primeiro");
        }
    }

    // a função principal do programa, onde o menu é executado
    public static void main(String[] args) {
        // loop infinito para manter o menu ativo até o usuário sair
        while (true) {
            // exibe o menu principal com o status da conexão
            System.out.println("\n*************************************************");
            System.out.println("**************** menu impressora *******************");
            System.out.println("      (conexão: " + (conexaoAberta ? "aberta" : "fechada") + ")");
            System.out.println("*************************************************\n");

            System.out.println("1  - configurar conexao");
            System.out.println("2  - abrir conexao");
            System.out.println("-------------------------------------------------");
            System.out.println("3  - impressao texto (padrao)");
            System.out.println("4  - impressao qr code (padrao)");
            System.out.println("5  - impressao cod barras (padrao)");
            System.out.println("6  - imprime xml sat");
            System.out.println("7  - imprime xml cancelamento sat");
            System.out.println("-------------------------------------------------");
            System.out.println("8  - abrir gaveta elgin");
            System.out.println("9  - abrir gaveta (genérica)");
            System.out.println("10 - emitir sinal sonoro");
            System.out.println("\n0  - fechar conexao e sair");

            String escolha = capturarEntrada("\ndigite a opção desejada: ");

            // opção 0 para sair do programa
            if (escolha.equals("0")) {
                FechaConexaoImpressora(); // tenta fechar a conexão antes de sair
                System.out.println("programa encerrado.");
                break; // sai do loop while
            }

            // usa switch para processar a escolha do usuário
            switch (escolha) {
                case "1":
                    configurarConexao();
                    break;
                case "2":
                    AbreConexaoImpressora();
                    break;
                case "3":
                    // limpa o buffer e imprime texto seguido de um corte
                    ImpressoraDLL.INSTANCE.LimpaBufferModoPagina(); // boa prática para evitar lixo
                    ImpressaoTexto();
                    ImpressoraDLL.INSTANCE.Corte(4); // o 4 aqui pode ser um avanço seguido de corte
                    break;
                case "4":
                    // imprime qrcode seguido de um corte
                    ImpressaoQRCode();
                    ImpressoraDLL.INSTANCE.Corte(4);
                    break;
                case "5":
                    // imprime código de barras seguido de um corte
                    ImpressaoCodigoBarras();
                    ImpressoraDLL.INSTANCE.Corte(4);
                    break;
                case "6":
                    // imprime xml sat seguido de um corte
                    ImprimeXMLSAT();
                    ImpressoraDLL.INSTANCE.Corte(4);
                    break;
                case "7":
                    // imprime cancelamento sat seguido de um corte
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
                    System.out.println("opção inválida");
            }
        }

        // fecha o scanner ao sair do programa
        scanner.close();
    }

    // função auxiliar para ler o conteúdo de um arquivo (não está sendo usada nas funções xmls, que usam o path)
    private static String lerArquivoComoString(String path) throws IOException {
        FileInputStream fis = new FileInputStream(path);
        byte[] data = fis.readAllBytes();
        fis.close();
        // lê o arquivo e retorna como string usando utf-8
        return new String(data, StandardCharsets.UTF_8);
    }
}