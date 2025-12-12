package com.nada.nada.data.repository;

import com.nada.nada.data.model.Post;
import org.springframework.data.repository.CrudRepository;

public interface PostRepository extends CrudRepository<Post, Long> {
    public Post findById(long id);
}
