import java.util.*;


public class GrupoI {
    static Scanner in = new Scanner(System.in);
    static Random rand = new Random();
    static List<Integer> numerosVencedores = new ArrayList<>();
    static List<Integer> estrelasVencedoras = new ArrayList<>();

    public static void main(String[] args) {

        while (true) {
            System.out.println("\n--- Menu Euromilhões---\n");
            System.out.println("1. Simular Sorteio");
            System.out.println("2. Criar Boletim Manual");
            System.out.println("3. Criar Boletim Aleatório");
            System.out.println("4. Simular Quantas Vezes para Ganhar");
            System.out.println("0. Sair");
            System.out.print("Escolha uma opção: ");

            try {
                int op = in.nextInt();

                switch (op) {
                    case 1:
                        SimularSorteio();
                        break;
                    case 2:
                        CriarBoletimManual();
                        break;
                    case 3:
                        CriarBoletimAutomatico();
                        break;
                    case 4:
                        SimularPrimeiroPremio();
                        break;
                    case 0:
                        System.out.println("Obrigado por jogar! A encerrar o programa...");
                        return;
                    default:
                        System.out.println("Opção inválida. Por favor, escolha uma opção entre 0 e 4.");
                }

                // Pausa após executar uma opção
                System.out.println("\nPressione ENTER para continuar...");
                in.nextLine();
                in.nextLine();
            } catch (InputMismatchException e) {
                System.out.println("Entrada inválida. Por favor, insira um número.");
                in.nextLine();
            }
        }
    }

    // 1. Simula o sorteio do Euromilhões
    private static void SimularSorteio() {
        numerosVencedores = gerarNumeros(5, 50);
        estrelasVencedoras = gerarNumeros(2, 12);

        Collections.sort(numerosVencedores);
        Collections.sort(estrelasVencedoras);

        System.out.println("Chave vencedora: ");
        System.out.println("Números: " + numerosVencedores);
        System.out.println("Estrelas: " + estrelasVencedoras);

    }

    //2. Permite criar um boletim manualmente
    private static void CriarBoletimManual() {
        CriarBoletim(false); // Boletim inserido manualmente
    }

    //3. Permite criar um boletim automaticamente
    private static void CriarBoletimAutomatico() {
        CriarBoletim(true); // Boletim gerado automaticamente
    }


    // 4. Simula quantas tentativas seriam necessárias para ganhar o 1º prémio
    private static void SimularPrimeiroPremio() {
        System.out.println("Insira os números da sua chave fixa (5 números entre 1 e 50):");
        List<Integer> numerosFixos = lerNumeros(5, 50);

        System.out.println("Insira as estrelas da sua chave fixa (2 números entre 1 e 12):");
        List<Integer> estrelasFixas = lerNumeros(2, 12);

        System.out.println("\nA simular... Esta operação pode demorar algum tempo.\n");

        int tentativas = 0;

        while (true) {
            tentativas++;

            // Gera chave vencedora aleatória
            List<Integer> numerosVencedores = gerarNumeros(5, 50);
            List<Integer> estrelasVencedoras = gerarNumeros(2, 12);

            // Verifica se a chave fixa coincide com a chave vencedora
            boolean ganhouNumeros = numerosFixos.equals(numerosVencedores);
            boolean ganhouEstrelas = estrelasFixas.equals(estrelasVencedoras);

            if (ganhouNumeros && ganhouEstrelas) {
                System.out.printf("Parabéns! Você ganhou o 1º prémio após %d tentativas.%n", tentativas);
                System.out.println("Chave vencedora: ");
                System.out.println("Números: " + numerosVencedores);
                System.out.println("Estrelas: " + estrelasVencedoras);
                break;
            }

            // Feedback ao utilizador após cada 100.0000 tentativas
            if (tentativas % 1000000 == 0) {
                System.out.printf("Tentativas realizadas: %d... A continuar.%n", tentativas);
            }
        }
    }

    // Cria um boletim, podendo ser manual ou automático
    private static void CriarBoletim(boolean automatico) {
        if (numerosVencedores.isEmpty()) {
            System.out.println("Por favor simule o sorteio antes de criar um boletim.");
            return;
        }

        System.out.println("Quantas chaves deseja inserir (1 a 5)?");
        int quantidade = in.nextInt();
        in.nextLine();

        // Verifica se a quantidade está dentro do limite
        if (quantidade < 1 || quantidade > 5) {
            System.out.println("Quantidade inválida. Escolha entre 1 e 5.");
            return;
        }

        List<ArrayList<Integer>> boletimNumeros = new ArrayList<>();
        List<ArrayList<Integer>> boletimEstrelas = new ArrayList<>();

        for (int i = 0; i < quantidade; i++) {
            if (automatico) {
                boletimNumeros.add(gerarNumeros(5, 50));
                boletimEstrelas.add(gerarNumeros(2, 12));
            } else {
                System.out.printf("Insira os números da chave %d (5 números entre 1 e 50):%n", i + 1);
                boletimNumeros.add(lerNumeros(5, 50));

                System.out.printf("Insira as estrelas da chave %d (2 números entre 1 e 12):%n", i + 1);
                boletimEstrelas.add(lerNumeros(2, 12));
            }
        }

        for (int i = 0; i < quantidade; i++) {
            List<Integer> numeros = boletimNumeros.get(i);
            List<Integer> estrelas = boletimEstrelas.get(i);

            System.out.printf("Chave %d: %s | Estrelas: %s%n", i + 1, numeros, estrelas);
            int acertosNumeros = contarAcertos(numeros, numerosVencedores);
            int acertosEstrelas = contarAcertos(estrelas, estrelasVencedoras);
            System.out.printf("Acertos: %d números e %d estrelas.%n", acertosNumeros, acertosEstrelas);
        }
    }

    // Gera uma lista de números aleatórios únicos
    private static ArrayList<Integer> gerarNumeros(int quantidade, int limite) {
        ArrayList<Integer> numeros = new ArrayList<>();
        while (numeros.size() < quantidade) {
            int numero = rand.nextInt(limite) + 1;
            if (!numeros.contains(numero)) {
                numeros.add(numero);
            }
        }
        Collections.sort(numeros);
        return numeros;
    }

    // Permite ao utilizador inserir números
    private static ArrayList<Integer> lerNumeros(int quantidade, int limite) {
        ArrayList<Integer> numeros = new ArrayList<>();

        while (numeros.size() < quantidade) {
            System.out.printf("Insira um número de (1-%d):%n", limite);
            try {
                int numero = in.nextInt();
                if (numero >= 1 && numero <= limite && !numeros.contains(numero)) {
                    numeros.add(numero);
                } else {
                    System.out.println("Número inválido ou já inserido. Tente novamente.");
                }
                in.nextLine();
                Collections.sort(numeros);

            } catch (InputMismatchException e) {
                System.out.println("Entrada inválida. Por favor, insira um número.");
                in.nextLine();
            }
        }
        return numeros;
    }

    // Conta quantos números de uma chave estão corretos
    private static int contarAcertos(List<Integer> chave, List<Integer> vencedoras) {
        int acertos = 0;
        for (Integer integer : chave) {
            if (vencedoras.contains(integer)) {
                acertos++;
            }
        }
        return acertos;
    }
}