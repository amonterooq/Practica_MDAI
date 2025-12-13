package com.nada.nada.data.repository;

import com.nada.nada.data.model.Post;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends CrudRepository<Post, Long> {
    public Post findById(long id);

    @Query("SELECT DISTINCT p FROM Post p LEFT JOIN FETCH p.usuariosQueDieronLike")
    public List<Post> findAllWithLikes();

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.usuariosQueDieronLike WHERE p.id = :id")
    public Optional<Post> findByIdWithLikes(@Param("id") Long id);
}
