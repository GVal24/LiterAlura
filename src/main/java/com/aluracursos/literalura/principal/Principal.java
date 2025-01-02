package com.aluracursos.literalura.principal;

import com.aluracursos.literalura.model.*;
import com.aluracursos.literalura.repository.*;
import com.aluracursos.literalura.service.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;

@Component
public class Principal {
    private Scanner teclado;
    private ConsumoAPI consumoAPI;
    private ConvierteDatos conversor;
    private static final String URL_BASE = "https://gutendex.com/books/";
    private List<Libro> libro;


    @Autowired
    private LibrosRepository librosRepository;

    @Autowired
    private AutorRepository autorRepository;

    @Autowired
    private LibroService libroService;

    @Autowired
    private AutorService autorService;

    @Autowired
    private EstadisticasService estadisticasService;


    @PostConstruct
    public void init() {
        teclado = new Scanner(System.in);
        consumoAPI = new ConsumoAPI();
        conversor = new ConvierteDatos();
    }

    // Menu
    public void muestraMenu() {
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    \nElija el número de la opción deseada:\n
                    1 - Buscar libro por título
                    2 - Libros registrados
                    3 - Autores registrados
                    4 - Autores vivos en un determinado año
                    5 - Libros por idioma
                    6 - Libros por título en la base de datos
                    7 - Autores por nombre
                    8 - Top 10 libros más descargados
                    9 - Estadisticas de nuestra base de datos

                    0 - Salir
                    """;
            System.out.println(menu);
            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion) {
                case 1:
                    buscarLibroPorTitulo();
                    break;
                case 2:
                    listarLibrosRegistrados();
                    break;
                case 3:
                    listarAutoresRegistrados();
                    break;
                case 4:
                    listarAutoresVivosEnDeterminadoAnio();
                    break;
                case 5:
                    listarLibrosPorIdioma();
                    break;
                case 6:
                    listarLibrosPorTitulo();
                    break;
                case 7:
                    listarAutoresPorNombre();
                    break;
                case 8:
                    buscarTop5LibrosDescargados();
                    break;
                case 9:
                    mostrarEstadisticas();
                    break;
                case 0:
                    System.out.println("\n\nGracias por utilizar nuestra aplicación\n\n");
                    break;
                default:
                    System.out.println("Opción inválida.");
            }
        }
    }

    // Buscar libro en la API (por titulo o parte de él)
    public void buscarLibroPorTitulo() {
        System.out.println("Escribe el nombre del libro que deseas buscar: ");
        String tituloLibro = teclado.nextLine();
        String json = consumoAPI.obtenerDatos(URL_BASE + "?search=" + tituloLibro.replace(" ", "+").toLowerCase());

        libroService.buscarLibroPorTitulo(tituloLibro, json);
    }

    //Listar libros registrados en la db
    private void listarLibrosRegistrados() {
        List<Libro> libros = librosRepository.findAll();
        libros.forEach(System.out::println);
    }

    //Listar los autores registrados en la db
    private void listarAutoresRegistrados() {
        List<String> sortedAutores = autorService.listarAutoresRegistrados();

        System.out.println("\nAutores registrados:\n──────────────────────────────");
        sortedAutores.forEach(System.out::println);
    }

    //Lista de autores vivos en un determinado año
    private void listarAutoresVivosEnDeterminadoAnio() {
        System.out.println("En esta opción podrá buscar autores vivos en un determinado año." +
                "\n¿Autores vivos en qué año desea encontrar?");

        int anio = teclado.nextInt();
        autorService.listarAutoresVivosEnAnio(anio);
    }

    //Lista de libro según el idioma
    private void listarLibrosPorIdioma() {
        System.out.println("¿En qué idioma desea buscar libros?");
        String idiomaStr = teclado.nextLine().toLowerCase();

        idiomaStr = LibroService.eliminarTildes(idiomaStr);

        if ("español".equalsIgnoreCase(idiomaStr)) {
            idiomaStr = "CASTELLANO";
        }

        try {
            Idiomas idioma = Idiomas.valueOf(idiomaStr.toUpperCase());
            libroService.listarLibrosPorIdioma(idioma);
        } catch (IllegalArgumentException e) {
            System.out.println("El idioma ingresado es inválido");
        }
    }

    //Lista de libros por título
    private void listarLibrosPorTitulo() {
        System.out.println("¿Qué titulo desea buscar?");
        String titulo = teclado.nextLine();
        List<Libro> libro = librosRepository.findByTituloContainingIgnoreCase(titulo);

        if (libro.isEmpty()) {
            System.out.println("No se encontraron libros con el titulo: " + titulo);
        } else {
            System.out.println("\nLibro encontrado:\n────────────────");
            libro.forEach(System.out::println);
        }
    }

    //Lista de autores por nombre
    private void listarAutoresPorNombre() {
        System.out.println("¿El nombre o apellido de qué autor desea buscar?");

        String nombreAutor = teclado.nextLine();


        List<Autor> autores = autorService.listarAutoresPorNombre(nombreAutor);

        if (autores.isEmpty()) {
            System.out.println("No se encontraron autores con el nombre: " + nombreAutor);
        } else {
            System.out.println("\nAutor encontrado:\n────────────────");
            autores.forEach(System.out::println);
        }
    }

    //Top 5 libros más descargados de nuestra db
    private void buscarTop5LibrosDescargados() {

        List<Libro> libros = librosRepository.findAll();

        List<Libro> top10Libros = libroService.ObtenerTop10LibrosMasDescargados(libros);

        System.out.println("\nTop 10 libros más descargados.\n  Cantidad               Titulos\nde descargas          mas descargados\n──────────────       ────────────────────");

        top10Libros.forEach(libro -> System.out.println(libro.getNumeroDescargas() + "                    " + libro.getTitulo().toUpperCase()));
    }

    //Mostrar estadísticas
    private void mostrarEstadisticas() {

        estadisticasService.mostrarEstadisticas();
    }
}


