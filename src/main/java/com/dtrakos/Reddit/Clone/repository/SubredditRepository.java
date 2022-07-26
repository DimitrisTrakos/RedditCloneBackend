package com.dtrakos.Reddit.Clone.repository;

import com.dtrakos.Reddit.Clone.model.Subreddit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface SubredditRepository extends JpaRepository<Subreddit,Long> {

    Optional<Subreddit> findByName(String subredditname);
}
