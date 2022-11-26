package nodv.service;

import nodv.exception.NotFoundException;
import nodv.model.Like;
import nodv.repository.LikeRepository;
import nodv.security.TokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@Service
public class LikeService {
    @Autowired
    LikeRepository likeRepository;
    @Autowired
    UserService userService;
    @Autowired
    TokenProvider tokenProvider;
    public List<Like> findAllLike() {
        return likeRepository.findAll();
    }

    public Like createLike(String postId, HttpServletRequest request){
        Like like = new Like();
        String userId = tokenProvider.getUserIdFromToken(tokenProvider.getJwtFromRequest(request));

        like.setUserId(userId);
        like.setPostId(postId);

        return likeRepository.save(like);
    }

    public void deleteLike(String postId, HttpServletRequest request){
        String userId = tokenProvider.getUserIdFromToken(tokenProvider.getJwtFromRequest(request));
        String idLike = "";
        Optional<Like> like = likeRepository.findLike(userId, postId);
        if (like.isPresent()){
            idLike = like.get().getId();
            likeRepository.deleteById(idLike);
        }else {
            throw new NotFoundException("Not found!");
        }
    }
}
