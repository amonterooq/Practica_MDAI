package com.nada.nada.data.services;

import com.nada.nada.data.model.Usuario;
import com.nada.nada.data.repository.ConjuntoRepository;
import com.nada.nada.data.repository.PrendaRepository;
import com.nada.nada.data.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PrendaRepository prendaRepository;
    private final ConjuntoRepository conjuntoRepository;


    @Autowired //no necesario, para recordar que nos inyecta y crea el IoC de Spring
    public UsuarioServiceImpl (UsuarioRepository usuarioRepository, PrendaRepository prendaRepository, ConjuntoRepository conjuntoRepository) {
        System.out.println("\t UsuarioServiceImpl constructor ");
        this.usuarioRepository=usuarioRepository;
        this.prendaRepository=prendaRepository;
        this.conjuntoRepository=conjuntoRepository;
    }

    //En la capa servicios es donde se implementa la LOGICA de negocio
    @Override
    public List<Usuario> findAllUsers() {
        // TODO Asi no es recomendable: Comprobar si hay usuarios, si esta vacio ...
        return (List<Usuario>) this.usuarioRepository.findAll();
    }

    @Override
    public void crearUsuario(Usuario usuario) {
        // TODO Logica...
        usuarioRepository.save(usuario);
    }

    @Override
    public void actualizarUsuario(Usuario usuario) {

        //Algo de logica de control, de como ha ido la operacion, no estaria mal.
        //recordad el usuario viene sin direcciones, pero solo se van a actualizar los campos actualizados.
        //Hibernate salida sql: Hibernate: update usuario set email=?,name=? where id=?
        usuarioRepository.save(usuario);

        // ---- otra opcion ---
        //podemos pedir al repositorio el usuario existente que comparte ese usuario.id y actualizar sus campos, por si en algun caso es necesario:
        // Obtener el usuario existente de la base de datos
        //		Usuario usuarioExistente = usuarioRepository.findById(usuario.getId());
        //
        //		// Actualizar solo los campos que se pueden actualizar
        //		usuarioExistente.setNombre(usuario.getNombre());
        //		usuarioExistente.setEmail(usuario.getEmail());
        //
        //	   usuarioRepository.save(usuarioExistente);

    }

    @Override
    public Optional<Usuario> findUsuarioById(Long usuarioId) {
        return usuarioRepository.findById(usuarioId);
    }

    @Override
    public void deleteUsuarioById(Long usuarioId) {
        //Algo de logica de control, de como ha ido la operacion, no estaria mal.
        Usuario usuario= usuarioRepository.findById(usuarioId).get();//optional a lo bruto. Mejorarlo.
        usuarioRepository.deleteById(usuarioId);
    }
}

