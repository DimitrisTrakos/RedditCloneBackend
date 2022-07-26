package com.dtrakos.Reddit.Clone.service;

import com.dtrakos.Reddit.Clone.dto.CommentDto;
import com.dtrakos.Reddit.Clone.exceptions.PostNotFoundException;
import com.dtrakos.Reddit.Clone.exceptions.SpringRedditException;
import com.dtrakos.Reddit.Clone.mapper.CommentMapper;
import com.dtrakos.Reddit.Clone.model.Comment;
import com.dtrakos.Reddit.Clone.model.NotificationEmail;
import com.dtrakos.Reddit.Clone.model.Post;
import com.dtrakos.Reddit.Clone.model.User;
import com.dtrakos.Reddit.Clone.repository.CommentRepository;
import com.dtrakos.Reddit.Clone.repository.PostRepository;
import com.dtrakos.Reddit.Clone.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
public class CommentService {
    private static final String POST_URL="";
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;
    private final MailContentBuilder mailContentBuilder;
    private final MailService mailService;

    public void save(CommentDto commentDto){
        Post post= postRepository.findById(commentDto.getPostId())
                .orElseThrow(()-> new PostNotFoundException(commentDto.getPostId().toString()));
        Comment comment=commentMapper.map(commentDto,post,authService.getCurrentUser());
        commentRepository.save(comment);

        String message= mailContentBuilder.build(post.getUser().getUsername() + "posted a comment on your post." + POST_URL);
        sendCommentNotification(message,post.getUser());

    }

    private void sendCommentNotification(String message, User user) {
        mailService.sendMail(new NotificationEmail(user.getUsername() +"  Commented on your post", user.getEmail(), message));
    }

    public List<CommentDto> getAllCommentsForPost(Long postId){
        Post post=postRepository.findById(postId)
                .orElseThrow(()-> new PostNotFoundException(postId.toString()));
        return commentRepository.findByPost(post)
                .stream()
                .map(commentMapper::mapToDto).collect(toList());
    }

    public List<CommentDto> getAllCommentsForUser(String userName){
        User user= userRepository.findByUsername(userName)
                .orElseThrow(()-> new UsernameNotFoundException(userName));
        return commentRepository.findAllByUser(user)
                .stream()
                .map(commentMapper::mapToDto)
                .collect(toList());

    }

    public boolean containSwearWords(String comment){
        if(comment.contains("shit")) {
            throw new SpringRedditException("Comments contains unacceptable language");

        }
        return false;
    }
}
