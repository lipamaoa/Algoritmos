import java.io.*;
import java.util.*;

public class GrupoIII {

    static Scanner in = new Scanner(System.in);
    static List<String> distritosNomes = new ArrayList<>(); // Stores district names
    static int[][] distritosDados; // Stores numerical data for each district
    static String[] partidos = {"ad", "ps", "ch", "il", "be"};

    public static void main(String[] args) {
        int op = -1;

        do {
            System.out.println("\n---- Menu Eleições ----");
            System.out.println("1 - Leitura dos dados");
            System.out.println("2 - Visualizar dados");
            System.out.println("3 - Distrito com mais votantes");
            System.out.println("4 - Partido com mais votos");
            System.out.println("5 - Distrito/Partido com mais votos");
            System.out.println("6 - Ordenar distritos por partido vencedor");
            System.out.println("7 - Atualizar distrito");
            System.out.println("8 - Email de distritos com mais votos inválidos");
            System.out.println("9 - Guardar informações");
            System.out.println("0 - Sair");
            System.out.print("Escolha uma opção: ");

            try {
                op = in.nextInt();
                in.nextLine();

                switch (op) {
                    case 1 -> lerDadosFicheiro("distritos.txt");
                    case 2 -> visualizarDados();
                    case 3 -> distritosComMaisVotos();
                    case 4 -> partidoComMaisVotos();
                    case 5 -> distritoPartidoComMaisVotos();
                    case 6 -> ordenarDistritosPorPartidoVencedor();
                    case 7 -> atualizarDistrito();
                    case 8 -> emailDistritosMaisVotosInvalidos();
                    case 9 -> guardarInformacoes("distritos.txt");
                    case 0 -> System.out.println("A sair...");
                    default -> System.out.println("Opção inválida. Tente novamente.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Entrada inválida. Por favor, insira um número.");
                in.nextLine();
            }
        } while (op != 0);
    }


    //1. Lê dados do ficheiro
    private static void lerDadosFicheiro(String caminho) {
        distritosNomes.clear();

        try (BufferedReader reader = new BufferedReader(new FileReader(caminho))) {
            List<int[]> tempData = new ArrayList<>();
            String linha;

            while ((linha = reader.readLine()) != null) {
                String[] partes = linha.split(";");
                if (partes.length != 10) { // Validate the number of columns (1 name + 9 data fields)
                    System.out.printf("Erro: Dados incompletos no distrito '%s'. Linha ignorada.%n", partes[0]);
                    continue;
                }

                String nomeDistrito = partes[0];
                int[] dados = new int[11];


                for (int i = 1; i < partes.length; i++) {
                    dados[i - 1] = Integer.parseInt(partes[i]);
                }

                // Calcula "Outros"
                int totalVotos = dados[1]; // Votantes
                int somatorioPartidos = 0;
                for (int i = 4; i < 9; i++) { // Sum of party votes
                    somatorioPartidos += dados[i];
                }
                dados[9] = totalVotos - (somatorioPartidos + dados[2] + dados[3]); // Outros

                // Calcula "Total"
                int somatorioTotal = 0;
                for (int i = 2; i <= 10; i++) { // Sum from Nulos to Outros
                    somatorioTotal += dados[i];
                }
                dados[10] = somatorioTotal;

                // Valida od dados do distrito
                if (!validarDistrito(nomeDistrito, dados)) {
                    System.out.printf("Erro: Dados inválidos no distrito '%s'. Linha ignorada.%n", nomeDistrito);
                    continue; //
                }

                distritosNomes.add(nomeDistrito);
                tempData.add(dados);
            }

            distritosDados = tempData.toArray(new int[0][]);

            System.out.println("Ficheiro lido com sucesso!");
        } catch (FileNotFoundException e) {
            System.out.println("Erro: Ficheiro não encontrado: " + caminho);
        } catch (IOException e) {
            System.out.println("Erro ao ler do ficheiro: " + e.getMessage());
        }
    }


    //2. Permite a visualização dos dados do ficheiro
    private static void visualizarDados() {

        System.out.printf("| %-10s ", "Distrito");
        for (String column : new String[]{"Inscritos", "Votantes", "Nulos", "Brancos", "AD", "PS", "CH", "IL", "BE", "Outros", "Total"}) {
            System.out.printf("| %-10s ", column);
        }
        System.out.println("|");
        System.out.println("+------------" + "------------".repeat(12) + "+");

        for (int i = 0; i < distritosNomes.size(); i++) {
            System.out.printf("| %-10s ", distritosNomes.get(i));
            for (int value : distritosDados[i]) {
                System.out.printf("| %-10d ", value);
            }
            System.out.println("|");
        }

        System.out.println("+------------" + "------------".repeat(12) + "+");
    }

    //3. Calcula o distrito com maior número de votos
    private static void distritosComMaisVotos() {
        int maxVotos = 0;
        List<String> maioresDistritos = new ArrayList<>();

        for (int i = 0; i < distritosNomes.size(); i++) {
            int votantes = distritosDados[i][1];
            if (votantes > maxVotos) {
                maxVotos = votantes;
                maioresDistritos.clear();
                maioresDistritos.add(distritosNomes.get(i));
            } else if (votantes == maxVotos) {
                maioresDistritos.add(distritosNomes.get(i));
            }
        }

        System.out.println("\nDistrito(s) com maior número de votantes:");
        for (String distrito : maioresDistritos) {
            System.out.printf("- %s (%d votantes)%n", distrito, maxVotos);
        }
    }

    //5. Determina o partido com maior número de votos
    private static void partidoComMaisVotos() {
        int[] votosPartidos = new int[partidos.length];

        for (int[] dados : distritosDados) {
            for (int i = 0; i < partidos.length; i++) {
                votosPartidos[i] += dados[4 + i];
            }
        }

        int maxVotos = 0;
        for (int votos : votosPartidos) {
            if (votos > maxVotos) {
                maxVotos = votos;
            }
        }
        List<Integer> partidosEmpatados = new ArrayList<>();

        for (int i = 0; i < votosPartidos.length; i++) {
            if (votosPartidos[i] == maxVotos) {
                partidosEmpatados.add(i);
            }
        }

        System.out.println("Partido(s) com mais votos:");
        for (int indice : partidosEmpatados) {
            System.out.printf("- %s: %d votos%n", partidos[indice], votosPartidos[indice]);
        }
    }

    //6. Calcula Distrito/Partido com mais votos
    private static void distritoPartidoComMaisVotos() {
        System.out.println("\nDistrito/Partido com mais votos:");

        for (int i = 0; i < distritosNomes.size(); i++) {
            int maxVotos = 0;
            int indexPartido = -1;

            for (int j = 0; j < partidos.length; j++) {
                if (distritosDados[i][4 + j] > maxVotos) {
                    maxVotos = distritosDados[i][4 + j];
                    indexPartido = j;
                }
            }

            if (indexPartido != -1) {
                double percentagem = (maxVotos * 100.0) / distritosDados[i][1];
                System.out.printf("Distrito: %s | Partido vencedor: %s (%d votos, %.2f%%)%n",
                        distritosNomes.get(i), partidos[indexPartido], maxVotos, percentagem);
            }
        }
    }

    //7. Ordena os distritos de forma descendente do número de votos no partido que ganhou as eleições
    private static void ordenarDistritosPorPartidoVencedor() {
        int[] votosNacionais = new int[partidos.length];

        for (int[] dados : distritosDados) {
            for (int i = 0; i < partidos.length; i++) {
                votosNacionais[i] += dados[4 + i];
            }
        }

        int maxVotos = 0;
        for (int votos : votosNacionais) {
            if (votos > maxVotos) {
                maxVotos = votos;
            }
        }

        int partidoVencedor = -1;
        for (int i = 0; i < votosNacionais.length; i++) {
            if (votosNacionais[i] == maxVotos) {
                partidoVencedor = i;
                break;
            }
        }

        if (partidoVencedor == -1) {
            System.out.println("Erro: Não foi possível determinar o partido vencedor.");
            return;
        }

        int finalPartidoVencedor = partidoVencedor;
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < distritosNomes.size(); i++) {
            indices.add(i);
        }

        indices.sort((a, b) -> Integer.compare(distritosDados[b][4 + finalPartidoVencedor], distritosDados[a][4 + finalPartidoVencedor]));

        System.out.println("Distritos ordenados pelo número de votos no partido vencedor:");
        for (int index : indices) {
            System.out.printf("%s: %d votos%n", distritosNomes.get(index), distritosDados[index][4 + finalPartidoVencedor]);
        }
    }

    //8. Atualiza a informação de um distrito, de acordo com os resultados de uma freguesia em que as eleições tiveram de se realizar num dia posterior.
    private static void atualizarDistrito() {
        System.out.print("Insira o nome do distrito a atualizar: ");
        String nomeDistrito = in.nextLine().trim();

        int index = distritosNomes.indexOf(nomeDistrito);
        if (index == -1) {
            System.out.println("Distrito não encontrado.");
            return;
        }

        int[] distrito = distritosDados[index];
        boolean continuar = true;

        while (continuar) {
            System.out.println("\nEscolha o campo que deseja atualizar:");
            for (int i = 0; i < distrito.length - 2; i++) { // Exclude "Outros" and "Total"
                String coluna = switch (i) {
                    case 0 -> "Inscritos";
                    case 1 -> "Votantes";
                    case 2 -> "Nulos";
                    case 3 -> "Brancos";
                    default -> partidos[i - 4];
                };
                System.out.printf("%d - %s (%d)%n", i + 1, coluna, distrito[i]);
            }
            System.out.print("Escolha uma opção (1-" + (distrito.length - 2) + "): ");
            int opcao;
            try {
                opcao = in.nextInt();
                in.nextLine(); // Consume newline

                if (opcao < 1 || opcao > distrito.length - 2) {
                    System.out.println("Opção inválida. Tente novamente.");
                    continue;
                }

                int indexCampo = opcao - 1;
                System.out.printf("Insira o novo valor para %s (%d): ",
                        switch (indexCampo) {
                            case 0 -> "Inscritos";
                            case 1 -> "Votantes";
                            case 2 -> "Nulos";
                            case 3 -> "Brancos";
                            default -> partidos[indexCampo - 4];
                        },
                        distrito[indexCampo]
                );

                int novoValor = in.nextInt();
                in.nextLine();
                distrito[indexCampo] = novoValor;


                recalcularOutrosETotal(distrito);
            } catch (InputMismatchException e) {
                System.out.println("Entrada inválida. Por favor, insira um número.");
                in.nextLine(); // Consume invalid input
                continue;
            }


            System.out.print("Deseja alterar outro campo? (s/n): ");
            String resposta = in.nextLine().trim().toLowerCase();
            if (!resposta.equals("s")) {
                continuar = false;
            }
        }


        if (!validarDistrito(distritosNomes.get(index), distrito)) {
            System.out.println("Alguns dados estão inválidos após a atualização.");
        } else {
            System.out.println("Distrito atualizado com sucesso.");
        }
    }


    //9. Imprime o endereço de correio eletrónico do distrito com mais votos inválidos (nulos e brancos).
    private static void emailDistritosMaisVotosInvalidos() {
        int maxInvalidos = 0;
        List<Integer> indicesInvalidos = new ArrayList<>();

        for (int i = 0; i < distritosNomes.size(); i++) {
            int invalidos = distritosDados[i][2] + distritosDados[i][3];
            if (invalidos > maxInvalidos) {
                maxInvalidos = invalidos;
                indicesInvalidos.clear();
                indicesInvalidos.add(i);
            } else if (invalidos == maxInvalidos) {
                indicesInvalidos.add(i);
            }
        }

        System.out.println("Distrito(s) com mais votos inválidos:");
        for (int index : indicesInvalidos) {
            System.out.printf("%s - Email: %s%n", distritosNomes.get(index), gerarEmailDistrito(distritosNomes.get(index)));
        }
    }

    private static String gerarEmailDistrito(String distrito) {
        return String.format("%c%c%c%c@ine.pt",
                distrito.charAt(0),
                distrito.charAt(1),
                distrito.charAt(distrito.length() - 2),
                distrito.charAt(distrito.length() - 1));
    }

    //10. Guarda no ficheiro de texto
    private static void guardarInformacoes(String caminho) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(caminho))) {
            for (int i = 0; i < distritosNomes.size(); i++) {
                writer.print(distritosNomes.get(i));
                for (int j = 0; j < distritosDados[i].length; j++) {
                    writer.print(";" + distritosDados[i][j]);
                }
                writer.println();
            }
            System.out.println("Informações guardadas no ficheiro: " + caminho);
        } catch (IOException e) {
            System.out.println("Erro ao guardar informações: " + e.getMessage());
        }
    }


    //Método para validar os dados do distrito
    private static boolean validarDistrito(String nome, int[] dados) {
        int inscritos = dados[0];
        int votantes = dados[1];
        int nulos = dados[2];
        int brancos = dados[3];
        int outros = dados[9];
        int total = dados[10];


        int somaVotos = nulos + brancos + outros;
        for (int i = 4; i < 9; i++) {
            somaVotos += dados[i];
        }


        if (votantes > inscritos) {
            System.out.printf("Erro no distrito '%s': Votantes (%d) não podem exceder inscritos (%d).%n", nome, votantes, inscritos);
            return false;
        }

        if (votantes != total) {
            System.out.printf("Erro no distrito '%s': Soma de votos (%d) não corresponde ao total de votantes (%d).%n", nome, somaVotos, votantes);
            return false;
        }

        return true;
    }

    //Método para recalcular "Outros" e "Final"
    private static void recalcularOutrosETotal(int[] distrito) {
        int totalVotantes = distrito[1];
        int somatorioVotos = 0;


        for (int i = 2; i < distrito.length - 2; i++) {
            somatorioVotos += distrito[i];
        }


        int outros = totalVotantes - somatorioVotos;
        distrito[distrito.length - 2] = Math.max(outros, 0);


        distrito[distrito.length - 1] = somatorioVotos + distrito[distrito.length - 2];
    }


}


