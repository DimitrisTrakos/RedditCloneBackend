package com.dtrakos.Reddit.Clone.repository;

import com.dtrakos.Reddit.Clone.model.Comment;
import com.dtrakos.Reddit.Clone.model.Post;
import com.dtrakos.Reddit.Clone.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPost(Post post);

    List<Comment> findAllByUser(User user);
}
