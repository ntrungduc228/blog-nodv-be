package nodv.service;

import nodv.exception.BadRequestException;
import nodv.exception.NotFoundException;
import nodv.model.Like;
import nodv.repository.LikeRepository;
import nodv.security.TokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Null;
import java.util.List;
import java.util.Optional;

@Service
public class LikeService {
    @Autowired
    LikeRepository likeRepository;
    @Autowired
    UserService userService;

    public List<Like> findAll() {
        return likeRepository.findAll();
    }

    public Like createLike(String postId, String userId){
        Like like = new Like();
        Optional <Like> checkLike = likeRepository.findLike(userId, postId);
        if(checkLike.isPresent()){
              throw new BadRequestException("Exist!");
        }
        else {
            like.setUserId(userId);
            like.setPostId(postId);
        }
        return likeRepository.save(like);

    }

    public void deleteLike(String postId, String userId){

        String idLike = "";
        Optional <Like> like = likeRepository.findLike(userId, postId);

        if (like.isPresent()){
            idLike = like.get().getId();
            likeRepository.deleteById(idLike);
        }else {
            throw new NotFoundException("Not found!");
        }
    }
}
