package com.dtrakos.Reddit.Clone.repository;

import com.dtrakos.Reddit.Clone.model.Post;
import com.dtrakos.Reddit.Clone.model.Subreddit;
import com.dtrakos.Reddit.Clone.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post,Long> {
    List<Post> findAllBySubreddit(Subreddit subreddit);

    List<Post> findByUser(User user);
}
