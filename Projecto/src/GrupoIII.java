import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.temporal.IsoFields;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class GrupoIII {

    static Scanner in = new Scanner(System.in);
    static List<List<Object>> distritos = new ArrayList<>();
    static String[] partidos = {"ad", "ps", "ch", "il", "be"};

    public static void main(String[] args) {
        int op;

        do {
            System.out.println("\n---- Menu Eleições ----");
            System.out.println("1 - Leitura dos dados");
            System.out.println("2 - Visualizar dados");
            System.out.println("3 - Total de votantes por distrito");
            System.out.println("4 - Total de votos noutros partidos");
            System.out.println("5 - Partido com mais votos");
            System.out.println("6 - Distrito/Partido com mais votos");
            System.out.println("7 - Ordenar distritos");
            System.out.println("8 - Actualizar distrito");
            System.out.println("9 - Email de distritos com mais votos inválidos");
            System.out.println("10 - Guardar dados");
            System.out.println("0 - Sair");
            System.out.print("Escolha uma opção: ");

            try {
                op = in.nextInt();
                in.nextLine();

                switch (op) {
                    case 1 -> lerDadosFicheiro("distritos.txt");
                    case 2 -> visualizarDados();
                    case 3 -> totalVotantesPorDistrito();
                    case 4 -> totalVotosOutrosPartidos();
                    case 5 -> partidoComMaisVotos();
//                    case 6 -> consultarRanking();
//                    case 7 -> consultarRanking();
//                    case 8 -> consultarRanking();
//                    case 9 -> consultarRanking();
//                    case 10 -> consultarRanking();
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


    //1. Leitura dos dados do ficheiro

    private static void lerDadosFicheiro(String caminho) {
        distritos.clear();

        List<String> linhas = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(caminho))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                String[] partes = linha.split(";");
                System.out.println(partes[0]);
                List<Object> distrito = new ArrayList<>();
                distrito.add(partes[0]);
                for (int i = 1; i < partes.length; i++) {
                    distrito.add(Integer.parseInt(partes[i]));

                }
                System.out.println(distrito.size());
                distritos.add(distrito);
            }
            System.out.println("Ficheiro lido com sucesso!");
        } catch (FileNotFoundException e) {
            System.out.println("Erro: Ficheiro não encontrado: " + caminho);
        } catch (IOException e) {
            System.out.println("Erro ao ler do ficheiro: " + e.getMessage());
        }
    }

    private static void visualizarDados() {
        // Cabeçalho da tabela
        System.out.printf("| %-10s | %-10s | %-10s | %-8s | %-8s | %-8s | %-8s | %-8s | %-8s | %-8s |%n",
                "Distrito", "Inscritos", "Votantes", "Nulos", "Brancos", "AD", "PS", "CH", "IL", "BE");
        System.out.println("+------------+------------+------------+----------+----------+----------+----------+----------+----------+----------+");


        // Dados dos distritos
        for (int i = 0; i < distritos.size(); i++) {
            List<Object> distrito = distritos.get(i);

            // Validação: verifica se a sublista tem 10 elementos
            if (distrito.size() != 10) {
                System.out.printf("Erro: O distrito '%s' tem dados incompletos e será ignorado.%n", distrito.get(0));
                continue; //
            }

            System.out.printf("| %-10s | %-10d | %-10d | %-8d | %-8d | %-8d | %-8d | %-8d | %-8d | %-8d |%n",
                    distrito.get(0), // Nome do distrito (String)
                    (int) distrito.get(1),   // Inscritos
                    (int) distrito.get(2),   // Votantes
                    (int) distrito.get(3),   // Nulos
                    (int) distrito.get(4),   // Brancos
                    (int) distrito.get(5),   // AD
                    (int) distrito.get(6),   // PS
                    (int) distrito.get(7),   // CH
                    (int) distrito.get(8),   // IL
                    (int) distrito.get(9)    // BE
            );
        }

        // Linha de fechamento da tabela
        System.out.println("+------------+------------+------------+----------+----------+----------+----------+----------+----------+----------+");
    }


    //3.Total de votantes por distrito
    private static void totalVotantesPorDistrito() {

        int maxVotos = 0;
        List<String> maioresDistritos = new ArrayList<>();

        for (List<Object> distrito : distritos) {
            int votantes = (int) distrito.get(2);
            if (votantes > maxVotos) {
                maxVotos = votantes;
                System.out.println(maxVotos);
                maioresDistritos.clear();
                maioresDistritos.add((String) distrito.get(0));
            } else if (votantes == maxVotos) {
                maioresDistritos.add((String) distrito.get(0));

            }
        }
        System.out.println("Distritos com maior número de votos depositados: " + maioresDistritos);
    }

    //4. Total de votos noutros partidos

    private static void totalVotosOutrosPartidos() {
        for (List<Object> distrito : distritos) {
            int totalVotos = (int) distrito.get(2);
            int somatorioPartidos = 0;
            for (int i = 5; i < distrito.size(); i++) {
                somatorioPartidos += (int) distrito.get(i);
            }
            int outros = totalVotos - somatorioPartidos;
            System.out.printf("%s - Votos noutros partidos: %d%n", distrito.get(0), outros);
        }
    }

    //5. Partido com mais votos
    private static void partidoComMaisVotos() {
        int[] votosPartidos = new int[partidos.length];

        for (List<Object> distrito : distritos) {
            for (int i = 0; i < partidos.length; i++) {
                votosPartidos[i] += (int) distrito.get(5 + i);
            }
        }


        int maxVotos = 0;
        int indexPartidoComMaisVotos = -1;

        for (int i = 0; i < votosPartidos.length; i++) {
            if (votosPartidos[i] > maxVotos) {
                maxVotos = votosPartidos[i];
                indexPartidoComMaisVotos = i;
            }
        }

//        if (indexPartidoComMaisVotos != -1) {
//            double percentagem = (maxVotos * 100.0) / votosPartidos[i];
//            System.out.printf("Partido com mais votos: %s (%d votos, %.2f%%)%n", partidos[indexPartidoComMaisVotos], maxVotos, percentagem);
//        }
    }


}
