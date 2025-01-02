    package com.aluracursos.literalura.service;

    import com.aluracursos.literalura.model.*;
    import com.aluracursos.literalura.repository.LibrosRepository;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Service;

    import java.text.Normalizer;
    import java.util.ArrayList;
    import java.util.Comparator;
    import java.util.List;
    import java.util.Optional;
    import java.util.regex.Pattern;

    @Service
    public class LibroService {

        @Autowired
        private LibrosRepository librosRepository;

        @Autowired
        private ConvierteDatos conversor;

        // Buscar libro en la API
        public void buscarLibroPorTitulo(String tituloLibro, String json) {
            try {
                Datos datos = conversor.obtenerDatos(json, Datos.class);

                Optional<DatosLibros> libroBuscado = datos.resultados().stream()
                        .filter(l -> l.titulo().toUpperCase().contains(tituloLibro.toUpperCase()))
                        .findFirst();

                if (libroBuscado.isPresent()) {
                    DatosLibros datosLibros = libroBuscado.get();

                    Libro libro = new Libro(datosLibros);

                    System.out.println("\nDATOS DEL LIBRO:\n─────────────────\n" + libro.toString());

                    // Guarda el libro en la base de datos utilizando el repositorio
                    librosRepository.save(libro);
                    System.out.println("\n /*/ Libro guardado en la base de datos local /*/");
                } else {
                    System.out.println("\n /*/ Libro no encontrado /*/");
                }
            } catch (Exception e) {
                System.err.println("Error inesperado: " + e.getMessage());
            }
        }

        public static String eliminarTildes(String input) {
            String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
            Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
            return pattern.matcher(normalized).replaceAll("");
        }

        // Buscar libros según el idioma
        public List<Libro> listarLibrosPorIdioma(Idiomas idioma) {
            List<Libro> libros = librosRepository.findByIdioma(idioma);
            if (libros.isEmpty()) {
                System.out.println("/*/ En nuestra base de datos local no hay libros en " + idioma);
            } else {
                System.out.println("\nLibros disponibles en " + idioma + ":\n──────────────────────────────");
                libros.forEach(System.out::println);
            }
            return libros;
        }

        //Top 5 libros más descargados
        public List<Libro> ObtenerTop10LibrosMasDescargados(List<Libro> libros) {
            // Ordenar los libros por número de descargas
            libros.sort(Comparator.comparingInt(Libro::getNumeroDescargas).reversed());

            // Limitar a los primeros 5 libros
            return libros.subList(0, Math.min(libros.size(), 10));
        }


    }