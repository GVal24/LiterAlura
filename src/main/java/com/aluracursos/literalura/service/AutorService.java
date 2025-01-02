package com.aluracursos.literalura.service;

import com.aluracursos.literalura.model.Autor;
import com.aluracursos.literalura.repository.AutorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AutorService {

    @Autowired
    private AutorRepository autorRepository;

    // Autores registrados en la db
    public List<String> listarAutoresRegistrados() {
        List<Autor> autores = autorRepository.findAll();


        return autores.stream()
                .sorted((p1, p2) -> p1.getNombre().compareToIgnoreCase(p2.getNombre()))
                .map(Autor::toString)
                .collect(Collectors.toList());
    }

    // Corroborar si un actor estaba vivo en un año determinado
    public List<Autor> getAutoresVivosEnAnio(int anio) {
        return autorRepository.findAll().stream()
                .filter(autor -> autor.getFechaDeNacimiento() != null && autor.getFechaDeNacimiento() <= anio)
                .filter(autor -> autor.getFechaDeMuerte() == null || autor.getFechaDeMuerte() >= anio)
                .collect(Collectors.toList());
    }

    // Buscar autor vivo en determinado año
    public void listarAutoresVivosEnAnio(int anio) {
        List<Autor> autoresVivos = getAutoresVivosEnAnio(anio);

        if (autoresVivos.isEmpty()) {
            System.out.println("\n/*/ EN NUESTRA BASE DE DATOS NO HAY REGISTRO DE AUTORES VIVOS EN EL AÑO " + anio + ". /*/");
        } else {
            System.out.println("\nAUTORES VIVOS EN EL AÑO " + anio + ":\n──────────────────────────────");
            autoresVivos.forEach(System.out::println);
        }
    }

    //  Buscar autores por nombre
    public List<Autor> listarAutoresPorNombre(String nombre) {
        return autorRepository.findByNombreContainingIgnoreCase(nombre);
    }

}
