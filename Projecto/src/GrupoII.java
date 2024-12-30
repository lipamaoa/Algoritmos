import java.io.*;
import java.util.*;

public class GrupoII {

    static Scanner in = new Scanner(System.in);
    static List<String> nomesParticipantes = new ArrayList<>();
    static List<int[]> statsParticipantes = new ArrayList<>();

    public static void main(String[] args) {
        int op;

        do {
            System.out.println("\n---- Jogo do Saco ----");
            System.out.println("1 - Adicionar participante");
            System.out.println("2 - Remover participante");
            System.out.println("3 - Ver Ranking");
            System.out.println("4 - Jogar");
            System.out.println("5 - Guardar Ranking");
            System.out.println("6 - Consultar Ranking");
            System.out.println("0 - Sair");
            System.out.print("Escolha uma opção: ");

            try {
                op = in.nextInt();
                in.nextLine();

                switch (op) {
                    case 1 -> adicionarParticipante();
                    case 2 -> removerParticipante();
                    case 3 -> mostrarRanking();
                    case 4 -> Jogar();
                    case 5 -> guardarRanking();
                    case 6 -> consultarRanking();
                    case 0 -> System.out.println("A sair...");
                    default -> System.out.println("Opção inválida. Tente novamente.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Entrada inválida. Por favor, insira um número.");
                in.nextLine();
                op = -1;
            }
        } while (op != 0);

    }

    private static String lerTextoInserido(String mensagem, String regex, String erro) {
        while (true) {
            System.out.println(mensagem);
            String entrada = in.nextLine().trim();
            if (!entrada.matches(regex)) {
                System.out.println(erro);
            } else {
                return entrada;
            }
        }
    }

    // 1. Adiciona novos participantes à lista, valida o nome e permite adicionar múltiplos participantes.
    private static void adicionarParticipante() {
        boolean continuar = true;

        while (continuar) {
            String nome = lerTextoInserido(
                    "Insira o nome do participante:",
                    "[a-zA-Z\\s]{3,12}", // Nome deve ter entre 3 e 12 caracteres, apenas letras e espaços
                    "O nome deve conter apenas letras e espaços (entre 3 e 12 caracteres)."
            );

            if (!nomesParticipantes.contains(nome)) {
                nomesParticipantes.add(nome);
                statsParticipantes.add(new int[]{0, 0});
                System.out.println("Participante adicionado com sucesso!");
            } else {
                System.out.println("Participante já existe!");
            }

            // Pergunta se o utilizador deseja adicionar mais participantes
            String resposta = lerTextoInserido(
                    "Deseja adicionar outro participante? (s/n):",
                    "[sS]|[nN]", // Resposta deve ser 's' ou 'n'
                    "Responda apenas com 's' ou 'n'."
            ).toLowerCase();

            if (!resposta.equals("s")) {
                continuar = false;
            }
        }
    }

    //Remove um participante da lista após confirmação.
    private static void removerParticipante() {
        String nome = lerTextoInserido(
                "Insira o nome do participante a remover:",
                ".+", // Nome não pode estar vazio
                "O nome não pode estar vazio."
        );

        nome = Character.toUpperCase(nome.charAt(0)) + nome.substring(1).toLowerCase();

        int index = nomesParticipantes.indexOf(nome);
        if (index != -1) {
            String confirmacao = lerTextoInserido(
                    String.format("Tem certeza de que deseja remover %s? (s/n):", nome),
                    "[sS]|[nN]", // Resposta deve ser 's' ou 'n'
                    "Responda apenas com 's' ou 'n'."
            ).toLowerCase();

            if (confirmacao.equals("s")) {
                nomesParticipantes.remove(index);
                statsParticipantes.remove(index);
                System.out.println("Participante removido com sucesso!");
            } else {
                System.out.println("Remoção cancelada.");
            }
        } else {
            System.out.println("Participante não encontrado!");
        }
    }

    //2. Mostra o ranking dos participantes ordenado por número de vitórias.
    private static void mostrarRanking() {
        if (nomesParticipantes.isEmpty()) {
            System.out.println("Não há participantes registados no momento.");
            return;
        }

        System.out.println("\nRanking:");
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < nomesParticipantes.size(); i++) {
            indices.add(i);
        }

        indices.sort((a, b) -> statsParticipantes.get(b)[1] - statsParticipantes.get(a)[1]);

        for (int indice : indices) {
            String nome = nomesParticipantes.get(indice);
            int[] estatisticas = statsParticipantes.get(indice);
            System.out.printf("%s - Jogos: %d, Vitórias: %d%n", nome, estatisticas[0], estatisticas[1]);
        }
        System.out.printf("Total de participantes no ranking: %d%n", nomesParticipantes.size());
    }

    //3. Um jogo onde os participantes tentam adivinhar o peso alvo.
    private static void Jogar() {
        if (nomesParticipantes.isEmpty()) {
            System.out.println("Não existem jogadores registados.");
            return;
        }

        double pesoAlvo = Math.random() * 10;
        System.out.printf("Peso alvo: %.2f kg%n", pesoAlvo);

        double margem = 0.150;
        double[] palpites = new double[nomesParticipantes.size()];

        // Cada participante insere o seu palpite

        ArrayList<Integer> indices = new ArrayList<>();
        for (int i = 0; i < nomesParticipantes.size(); i++) {
            indices.add(i);
        }
        Collections.shuffle(indices);

        for (int i = 0; i < indices.size(); i++) {
            int indexRandom = indices.get(i);
            palpites[indexRandom] = lerPalpite( nomesParticipantes.get(indexRandom), 0, 10);
        }

        String vencedor = null;
        double menorDiferenca = Double.MAX_VALUE;

        // Avaliação dos palpites
        for (int i = 0; i < nomesParticipantes.size(); i++) {
            String nome = nomesParticipantes.get(i);
            double palpite = palpites[i];
            double diferenca = Math.abs(pesoAlvo - palpite);

            System.out.printf("%s apostou %.2f kg (Diferença: %.2f)%n", nome, palpite, diferenca);

            statsParticipantes.get(i)[0]++;

            if (diferenca <= margem && diferenca < menorDiferenca) {
                menorDiferenca = diferenca;
                vencedor = nome;
            }
        }

        // Determina o vencedor
        if (vencedor != null) {
            int indiceVencedor = nomesParticipantes.indexOf(vencedor);
            statsParticipantes.get(indiceVencedor)[1]++;
            System.out.printf("Vencedor: %s com a menor diferença de %.2f kg!%n", vencedor, menorDiferenca);
        } else {
            System.out.println("Ninguém ganhou! Todos os palpites estavam fora da margem de erro.");
        }
    }

    //Verifica e valida o palpite do utilizador.
    private static double lerPalpite(String nome, double min, double max) {
        while (true) {
            System.out.printf("%s, insira o seu valor (entre %.1f e %.1f): ", nome, min, max);
            String input = in.nextLine().replace(",", ".");
            try {
                double palpite = Double.parseDouble(input);
                if (palpite >= min && palpite <= max) {
                    return palpite;
                } else {
                    System.out.println("Valor inválido! Deve estar entre " + min + " e " + max + ".");
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida! Tente novamente.");
            }
        }
    }

    //4. Guarda o ranking atual num ficheiro.
    private static void guardarRanking() {
        if (nomesParticipantes.isEmpty()) {
            System.out.println("Não há participantes para guardar o ranking.");
            return;
        }

        List<String> linhas = new ArrayList<>();
        for (int i = 0; i < nomesParticipantes.size(); i++) {
            String nome = nomesParticipantes.get(i);
            int[] stats = statsParticipantes.get(i);
            linhas.add(String.format("%s, %d, %d", nome, stats[0], stats[1]));
        }

        escreverNoFicheiro("ranking.txt", linhas);
        System.out.printf("Ranking salvo com sucesso! Total: %d participantes.%n", nomesParticipantes.size());
    }

    //Lê o ranking armazenado no ficheiro e mostra ao utilizador.
    private static void consultarRanking() {
        List<String> linhas = lerDoFicheiro("ranking.txt");
        nomesParticipantes.clear();
        statsParticipantes.clear();

        for (String linha : linhas) {
            String[] partes = linha.split(", ");
            if (partes.length != 3) {
                System.out.println("Linha ignorada (formato inválido): " + linha);
                continue;
            }

            try {
                nomesParticipantes.add(partes[0]);
                statsParticipantes.add(new int[]{Integer.parseInt(partes[1]), Integer.parseInt(partes[2])});
            } catch (NumberFormatException e) {
                System.out.println("Linha ignorada (dados inválidos): " + linha);
            }
        }
        mostrarRanking();
    }

    // Escreve uma lista de linhas num ficheiro
    private static void escreverNoFicheiro(String path, List<String> linhas) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(path))) {
            for (String linha : linhas) {
                writer.println(linha);
            }
            System.out.println("Ficheiro guardado com sucesso!");
        } catch (IOException e) {
            System.out.println("Erro ao escrever no ficheiro: " + e.getMessage());
        }
    }

    // Lê linhas de um arquivo
    private static List<String> lerDoFicheiro(String caminho) {
        List<String> linhas = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(caminho))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                linhas.add(linha);
            }
            System.out.println("Ficheiro lido com sucesso!");
        } catch (FileNotFoundException e) {
            System.out.println("Erro: Ficheiro não encontrado: " + caminho);
        } catch (IOException e) {
            System.out.println("Erro ao ler do ficheiro: " + e.getMessage());
        }
        return linhas;
    }

}
