package com.dtrakos.Reddit.Clone.controller;

import com.dtrakos.Reddit.Clone.dto.SubredditDto;
import com.dtrakos.Reddit.Clone.service.SubredditService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/subreddit")
@AllArgsConstructor
@Slf4j
public class SubredditController {

    private final SubredditService subredditService;


    @PostMapping
    public  ResponseEntity<SubredditDto> createSubreddit (@RequestBody SubredditDto subredditDto){
        return ResponseEntity.status(CREATED)
                .body(subredditService.save(subredditDto));
    }

    @GetMapping
    public ResponseEntity <List<SubredditDto>> getAllSubreddits(){

        return ResponseEntity.status(OK).body(subredditService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubredditDto> getSubreddit (@PathVariable Long id) {
        return ResponseEntity.status(OK).body(subredditService.getSubreddit(id));
    }

}
