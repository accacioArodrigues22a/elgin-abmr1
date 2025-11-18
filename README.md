# Integração Java com Impressora Térmica Elgin via DLL

## Visão Geral do Projeto

Este projeto consiste em uma aplicação Console (CLI) desenvolvida em Java, projetada para realizar a interface e comunicação direta com impressoras térmicas da fabricante Elgin (modelos como i9, i8, entre outros). A comunicação é estabelecida através do uso da biblioteca nativa `E1_Impressora01.dll`, utilizando o framework JNA (Java Native Access) para mapear as funções da biblioteca dinâmica do Windows para métodos Java.

O objetivo principal da ferramenta é demonstrar a implementação de funcionalidades essenciais de automação comercial, incluindo impressão de textos, códigos de barras, QR Codes, cupons fiscais (XML do SAT), acionamento de gavetas e controle de hardware (corte de papel e sinal sonoro).

## Funcionalidades

A aplicação oferece um menu interativo que permite testar as seguintes operações:

* **Configuração de Conexão:** Definição dinâmica dos parâmetros de conexão (Tipo, Modelo, Porta de Comunicação e Taxa de Transmissão).
* **Gerenciamento de Sessão:** Abertura e fechamento de conexão com o dispositivo.
* **Impressão de Texto:** Envio de strings simples com formatação básica.
* **Impressão de QR Code:** Geração e impressão de códigos QR com níveis de correção configuráveis.
* **Impressão de Código de Barras:** Suporte para impressão de códigos de barras lineares.
* **Integração SAT Fiscal:**
    * Impressão de extrato de venda via arquivo XML do SAT.
    * Impressão de cancelamento de venda via arquivo XML.
* **Controle de Hardware:**
    * Acionamento de guilhotina (corte de papel total ou parcial).
    * Avanço de papel (line feed).
    * Emissão de sinal sonoro (buzzer).
* **Controle de Gaveta:**
    * Abertura de gaveta padrão Elgin.
    * Abertura de gaveta genérica (controle de pulso via pino).

## Requisitos do Sistema

* **Sistema Operacional:** Windows (devido à dependência de arquivos `.dll`).
* **Java Development Kit (JDK):** Versão 8 ou superior.
* **Dependências de Bibliotecas:**
    * `jna.jar` (Java Native Access) para carregar a biblioteca nativa.
    * `jna-platform.jar` (Opcional, mas recomendado para melhor compatibilidade).
* **Drivers:** `E1_Impressora01.dll` (Fornecida pela fabricante Elgin).
* **Hardware:** Impressora térmica Elgin conectada via USB, Serial ou Ethernet.

## Estrutura do Projeto

Com base nos arquivos fonte e na organização de diretórios, a estrutura do projeto é a seguinte:

* **src/Main.java:** Classe principal contendo o método `main`, a lógica do menu e a interface `ImpressoraDLL` que estende `Library` do JNA.
* **libs/:** Diretório destinado às bibliotecas `.jar` externas (JNA).
* **E1_Impressora01.dll:** Biblioteca dinâmica proprietária da Elgin (localizada na raiz ou pasta específica).
* **Arquivos XML de Teste:**
    * `XMLSAT.xml`: Arquivo modelo para teste de impressão de venda SAT.
    * `CANC_SAT.xml`: Arquivo modelo para teste de cancelamento.
    * `NFCe.xml`: Arquivo modelo para Nota Fiscal de Consumidor Eletrônica.

## Configuração e Instalação

Para executar o projeto em um ambiente local, é necessário realizar ajustes nos caminhos absolutos definidos no código fonte, pois eles apontam para diretórios específicos da máquina de desenvolvimento original.

**1. Configuração da DLL**

Localize a interface `ImpressoraDLL` dentro do arquivo `Main.java`. O caminho para carregar a DLL deve ser alterado para o local onde o arquivo `E1_Impressora01.dll` está salvo no seu computador.

Trecho a ser alterado:
`ImpressoraDLL INSTANCE = (ImpressoraDLL) Native.load("Caminho\\Para\\Sua\\E1_Impressora01.dll", ImpressoraDLL.class);`

**2. Configuração dos Arquivos XML**

Para as funções de impressão de XML (opções 6 e 7 do menu), localize os métodos `ImprimeXMLSAT` e `ImprimeXMLCancelamentoSAT`. Atualize a variável `dados` com o caminho absoluto correto dos arquivos XML em sua máquina.

Trecho a ser alterado:
`String dados = "path=C:\\Caminho\\Para\\Seu\\Arquivo\\XMLSAT.xml";`

## Manual de Utilização

Ao executar a classe `Main`, o console exibirá um menu numerado. Abaixo está a descrição detalhada de cada opção:

**Opção 1: Configurar Conexão**
Solicita ao usuário os dados para conexão.
* Tipo: 1 (USB), 2 (Serial/RS232), 3 (Ethernet/TCP-IP).
* Modelo: Exemplo 'i9', 'i8'.
* Conexão: 'USB' para USB, 'COMx' para serial, ou endereço IP para rede.
* Parâmetro: 0 para USB, Baudrate (ex: 9600) para serial, ou porta (ex: 9100) para rede.

**Opção 2: Abrir Conexão**
Efetiva a comunicação com a impressora utilizando os parâmetros configurados na Opção 1. Deve ser executada antes de qualquer comando de impressão.

**Opção 3: Impressão Texto**
Solicita uma string ao usuário e imprime o texto na impressora, seguido de um comando de corte de papel.

**Opção 4: Impressão QR Code**
Solicita o conteúdo do QR Code e o imprime centralizado, com nível de correção configurado.

**Opção 5: Impressão Cód. Barras**
Imprime um código de barras de exemplo (hardcoded no código como `{A012345678912`) no padrão Code 128.

**Opção 6: Imprime XML SAT**
Lê o arquivo `XMLSAT.xml` do disco e imprime o extrato fiscal correspondente conforme layout da SEFAZ.

**Opção 7: Imprime XML Cancelamento SAT**
Lê o arquivo `CANC_SAT.xml` e imprime o comprovante de cancelamento. Requer a assinatura do QR Code (string Base64) configurada no código.

**Opção 8: Abrir Gaveta Elgin**
Envia comando proprietário para abertura de gaveta de dinheiro conectada à impressora.

**Opção 9: Abrir Gaveta (Genérica)**
Envia pulso elétrico configurável (pino, tempo inicial, tempo final) para acionamento de gavetas genéricas.

**Opção 10: Emitir Sinal Sonoro**
Aciona o buzzer interno da impressora.

**Opção 11: Avança Papel**
Avança uma quantidade predefinida de linhas em branco.

**Opção 12: Corte**
Aciona a guilhotina para corte do papel.

**Opção 0: Fechar Conexão e Sair**
Encerra a comunicação com a porta da impressora e finaliza a aplicação Java.

## Tratamento de Erros

O código implementa verificações básicas de retorno das funções da DLL (onde 0 geralmente indica sucesso).
* **Erro de Link (UnsatisfiedLinkError):** Ocorre se o caminho da DLL estiver incorreto ou se a arquitetura do Java (32/64 bits) não corresponder à da DLL.
* **Erro de Conexão:** Ocorre se a impressora estiver desligada, desconectada ou se os parâmetros de porta/tipo estiverem errados.
* **Exceções de Arquivo:** Podem ocorrer nos métodos de leitura de XML se os caminhos dos arquivos não existirem.

## Notas Técnicas sobre a API JNA

A interface `ImpressoraDLL` define a assinatura dos métodos nativos em C/C++.
* `AbreConexaoImpressora`: Inicializa o handle de comunicação.
* `ImpressaoTexto`: Envia buffer de caracteres.
* `ImprimeXMLSAT`: Função de alto nível que processa o XML, formata o cupom e imprime gráficos/textos automaticamente.