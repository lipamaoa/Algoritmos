import java.io.*;
import java.util.*;

public class GrupoIII {

    static Scanner in = new Scanner(System.in);
    static List<List<Object>> distritos = new ArrayList<>();
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
        distritos.clear();

        try (BufferedReader reader = new BufferedReader(new FileReader(caminho))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                String[] partes = linha.split(";");
                List<Object> distrito = new ArrayList<>();
                distrito.add(partes[0]);

                for (int i = 1; i < partes.length; i++) {
                    distrito.add(Integer.parseInt(partes[i]));
                }

                //Calcula os número de votos noutros partidos
                int totalVotos = (int) distrito.get(2);
                int somatorioPartidos = calcularSoma(partes, 3, partes.length);
                int outros = totalVotos - somatorioPartidos;
                distrito.add(outros);

                //Calcula o Total de votos por distrito
                int somatorioTotal = calcularSoma(distrito, 3, distrito.size());
                distrito.add(somatorioTotal);

                distritos.add(distrito);
                System.out.println(distritos);
            }
            System.out.println("Ficheiro lido com sucesso!");
        } catch (FileNotFoundException e) {
            System.out.println("Erro: Ficheiro não encontrado: " + caminho);
        } catch (IOException e) {
            System.out.println("Erro ao ler do ficheiro: " + e.getMessage());
        }
    }


    //2. Permite a visualização dos dados do ficheiro
    private static void visualizarDados() {
        System.out.printf("| %-10s | %-10s | %-10s | %-8s | %-8s | %-8s | %-8s | %-8s | %-8s | %-8s | %-8s | %-8s |%n",
                "Distrito", "Inscritos", "Votantes", "Nulos", "Brancos", "AD", "PS", "CH", "IL", "BE", "Outros", "Total");
        System.out.println("+------------+------------+------------+----------+----------+----------+----------+----------+----------+----------+----------+----------+");

        for (List<Object> distrito : distritos) {
            System.out.printf("| %-10s | %-10d | %-10d | %-8d | %-8d | %-8d | %-8d | %-8d | %-8d | %-8d | %-8d | %-8d |%n",
                    distrito.get(0),
                    (int) distrito.get(1),
                    (int) distrito.get(2),
                    (int) distrito.get(3),
                    (int) distrito.get(4),
                    (int) distrito.get(5),
                    (int) distrito.get(6),
                    (int) distrito.get(7),
                    (int) distrito.get(8),
                    (int) distrito.get(9),
                    (int) distrito.get(10),
                    (int) distrito.get(11)
            );
        }

        System.out.println("+------------+------------+------------+----------+----------+----------+----------+----------+----------+----------+----------+----------+");
    }


    //3. Calcula o distrito com maior número de votos
    private static void distritosComMaisVotos() {
        int maxVotos = 0;
        List<String> maioresDistritos = new ArrayList<>();

        for (List<Object> distrito : distritos) {
            int votantes = (int) distrito.get(2);
            if (votantes > maxVotos) {
                maxVotos = votantes;
                maioresDistritos.clear();
                maioresDistritos.add((String) distrito.get(0));
            } else if (votantes == maxVotos) {
                maioresDistritos.add((String) distrito.get(0));
            }
        }

        System.out.println("\nDistrito(s) com maior número de votantes:");
        for (String distrito : maioresDistritos) {
            System.out.printf("- %s (%d votantes)%n", distrito, maxVotos);
        }
    }

    //5. Determina o partido com maior número de votos
    private static void partidoComMaisVotos() {
        int[] votosPartidos = somarVotosPartidos();
        int totalVotos = 0;
        for (List<Object> distrito : distritos) {
            totalVotos += (int) distrito.get(2);
        }

        int maxVotos = Arrays.stream(votosPartidos).max().orElse(0);

        List<Integer> partidosEmpatados = new ArrayList<>();
        for (int i = 0; i < votosPartidos.length; i++) {
            if (votosPartidos[i] == maxVotos) {
                partidosEmpatados.add(i);
            }
        }

        System.out.println("Partido(s) com mais votos:");
        for (int indice : partidosEmpatados) {
            double percentagem = (votosPartidos[indice] * 100.0) / totalVotos;
            System.out.printf("| %s: %d votos (%.2f%% do total)%n", partidos[indice], votosPartidos[indice], percentagem);
        }
    }

    //6. Calcula Distrito/Partido com mais votos
    private static void distritoPartidoComMaisVotos() {
        System.out.println("\nDistrito/Partido com mais votos:");

        for (List<Object> distrito : distritos) {
            String nomeDistrito = (String) distrito.get(0);
            int totalVotantes = (int) distrito.get(2);

            int maxVotos = 0;
            int indexPartidoVencedor = -1;

            for (int i = 0; i < partidos.length; i++) {
                int votosPartido = (int) distrito.get(5 + i);
                if (votosPartido > maxVotos) {
                    maxVotos = votosPartido;
                    indexPartidoVencedor = i;
                }
            }

            if (indexPartidoVencedor != -1) {
                double percentagem = (maxVotos * 100.0) / totalVotantes;
                System.out.printf("Distrito: %s | Partido vencedor: %s (%d votos, %.2f%%)%n",
                        nomeDistrito, partidos[indexPartidoVencedor], maxVotos, percentagem);
            } else {
                System.out.printf("Distrito: %s não teve votos válidos.%n", nomeDistrito);
            }
        }
    }

    //7. ordena os distritos de forma descendente do número de votos no partido que ganhou as eleições
    private static void ordenarDistritosPorPartidoVencedor() {
        int indexPartidoVencedor = encontrarPartidoVencedor();
        if (indexPartidoVencedor == -1) {
            System.out.println("Erro: Não foi possível determinar o partido vencedor.");
            return;
        }

        String partidoVencedor = partidos[indexPartidoVencedor];
        System.out.printf("Partido vencedor nacional: %s.%n", partidoVencedor);

        distritos.sort((d1, d2) -> {
            int votosD1 = (int) d1.get(5 + indexPartidoVencedor);
            int votosD2 = (int) d2.get(5 + indexPartidoVencedor);
            return Integer.compare(votosD2, votosD1);
        });

        System.out.println("\nDistritos ordenados pelo número de votos no partido vencedor:");
        for (List<Object> distrito : distritos) {
            System.out.printf("%s: %d votos%n",
                    distrito.get(0),
                    (int) distrito.get(5 + indexPartidoVencedor));
        }
    }

    //8. Atualiza a informação de um distrito, de acordo com os resultados de uma freguesia em que as eleições tiveram de se realizar num dia posterior.
    private static void atualizarDistrito() {
        System.out.print("Insira o nome do distrito a atualizar: ");
        String nomeDistrito = in.nextLine().trim();

        Optional<List<Object>> distritoOptional = distritos.stream()
                .filter(d -> d.get(0).equals(nomeDistrito))
                .findFirst();

        if (distritoOptional.isEmpty()) {
            System.out.println("Distrito não encontrado.");
            return;
        }

        List<Object> distrito = distritoOptional.get();
        System.out.println("Insira os novos valores para o distrito:");

        for (int i = 1; i < distrito.size(); i++) {
            String coluna = switch (i) {
                case 1 -> "Inscritos";
                case 2 -> "Votantes";
                case 3 -> "Nulos";
                case 4 -> "Brancos";
                default -> partidos[i - 5];
            };

            System.out.printf("%s (%s): ", coluna, distrito.get(i));
            int novoValor = in.nextInt();
            distrito.set(i, novoValor);
        }

        // Validar os dados do distrito após a atualização
        validarDistrito(distrito);
        System.out.println("Distrito atualizado com sucesso.");
    }


    //9. Imprime o endereço de correio eletrónico do distrito com mais votos inválidos (nulos e brancos).
    private static void emailDistritosMaisVotosInvalidos() {
        int maxInvalidos = 0;
        List<String> distritosInvalidos = new ArrayList<>();

        for (List<Object> distrito : distritos) {
            int votosInvalidos = (int) distrito.get(3) + (int) distrito.get(4);
            if (votosInvalidos > maxInvalidos) {
                maxInvalidos = votosInvalidos;
                distritosInvalidos.clear();
                distritosInvalidos.add((String) distrito.get(0));
            } else if (votosInvalidos == maxInvalidos) {
                distritosInvalidos.add((String) distrito.get(0));
            }
        }

        System.out.println("Distrito(s) com mais votos inválidos:");
        for (String distrito : distritosInvalidos) {
            String email = gerarEmailDistrito(distrito);
            System.out.printf("%s - Email: %s%n", distrito, email);
        }
    }


    //10. Guarda no ficheiro de texto
    private static void guardarInformacoes(String caminho) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(caminho))) {
            for (List<Object> distrito : distritos) {
                for (int i = 0; i < distrito.size(); i++) {
                    writer.print(distrito.get(i));
                    if (i < distrito.size() - 1) writer.print(";");
                }
                writer.println();
            }
            System.out.println("Informações guardadas no ficheiro: " + caminho);
        } catch (IOException e) {
            System.out.println("Erro ao guardar informações: " + e.getMessage());
        }
    }

    //Métodos

    private static int calcularSoma(String[] array, int inicio, int fim) {
        int soma = 0;
        for (int i = inicio; i < fim; i++) {
            soma += Integer.parseInt(array[i]);
        }
        return soma;
    }

    private static int calcularSoma(List<Object> lista, int inicio, int fim) {
        int soma = 0;
        for (int i = inicio; i < fim; i++) {
            soma += (int) lista.get(i);
        }
        return soma;
    }


    private static int[] somarVotosPartidos() {
        int[] votosPartidos = new int[partidos.length];
        for (List<Object> distrito : distritos) {
            for (int i = 0; i < partidos.length; i++) {
                votosPartidos[i] += (int) distrito.get(5 + i);
            }
        }
        return votosPartidos;
    }

    private static int encontrarPartidoVencedor() {
        int[] votosPartidos = somarVotosPartidos();
        int maxVotos = 0;
        int indexPartidoVencedor = -1;
        for (int i = 0; i < votosPartidos.length; i++) {
            if (votosPartidos[i] > maxVotos) {
                maxVotos = votosPartidos[i];
                indexPartidoVencedor = i;
            }
        }
        return indexPartidoVencedor;
    }

    private static void validarDistrito(List<Object> distrito) {
        int inscritos = (int) distrito.get(1);
        int votantes = (int) distrito.get(2);
        int somatorioVotos = calcularSoma(distrito, 3, distrito.size() - 2); // Exclui "Outros" e "Total"

        if (votantes > inscritos) {
            System.out.println("Erro: Votantes não podem exceder o número de inscritos.");
        }

        if (votantes != somatorioVotos) {
            System.out.println("Erro: Soma de nulos, brancos e votos em partidos não corresponde ao número de votantes.");
        }
    }

    private static String gerarEmailDistrito(String distrito) {
        return String.format("%c%c%c%c@ine.pt",
                distrito.charAt(0),
                distrito.charAt(1),
                distrito.charAt(distrito.length() - 2),
                distrito.charAt(distrito.length() - 1));
    }


}
