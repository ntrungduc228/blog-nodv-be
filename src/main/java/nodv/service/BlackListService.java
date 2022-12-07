package nodv.service;

import nodv.model.BlackList;
import nodv.model.Bookmark;
import nodv.model.Post;
import nodv.payload.BlackListDTO;
import nodv.repository.BlackListRepository;
import nodv.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service

public class BlackListService {
//    @Autowired
//    BlackListRepository blackListRepository;

    @Autowired
   BlackListRepository blackListRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    MongoTemplate mongoTemplate;


   public BlackList createBlackList(BlackListDTO blackListDTO){
       BlackList blackList = new BlackList();
       blackList.setUserId(blackListDTO.getUserId());
       List<String> listPostIds = new ArrayList<>();
       if(!blackListDTO.getPostId().isEmpty()){
           listPostIds.add(blackListDTO.getPostId());
       }
       blackList.setPostIds(listPostIds);
       return blackListRepository.save(blackList);
   }

   public List<String> getListPostIds(String userId){
       Optional<BlackList> blackList = blackListRepository.findByUserId(userId);

       if (!blackList.isPresent()){
           //ofNullable: Tra ve mot optionanl chua gia tri duoc truyen vao neu khac null nguoc lai se tra rong
           //createBookmark(BookmarkDTO(userId, "")): Tao mot bookmark cua userId voi postId la rong -> neu ma truyen vao postId rong thi
           blackList = Optional.ofNullable(this.createBlackList(new BlackListDTO(userId, "")));
       }
       return blackList.get().getPostIds();
   }

   public BlackList findByUserId(String userId) throws Exception{
       Optional<BlackList> blackList = blackListRepository.findByUserId(userId);

       if(!blackList.isPresent()){
           blackList = Optional.ofNullable(this.createBlackList(new BlackListDTO(userId, "")));
           blackList.get().setPosts(new ArrayList<Post>());
           return  blackList.get();
       }

       if(blackList.get().getPostIds().size() > 0){
           List<Post> posts = new ArrayList<>();
           for(String postId: blackList.get().getPostIds()){
               Optional<Post> post = postRepository.findById(postId);
               if(post.isPresent()){
                   posts.add((post.get()));
               }
           }
           blackList.get().setPosts(posts);
       }
       return blackList.get();
   }

   public List<String> updatePostIdToBlackList(String userId, String postId) throws Exception{
       Optional<BlackList> blackList = blackListRepository.findByUserId(userId);
        if(!blackList.isPresent()){
            blackList = Optional.ofNullable(this.createBlackList(new BlackListDTO(userId, "")));
        }

        Boolean postIdsExists = false;
        if(blackList.get().getPostIds().contains(postId)){
            postIdsExists = true;
        }

       Query query = new Query();
       Criteria criteria = Criteria.where("userId").is(blackList.get().getUserId());
       query.addCriteria(criteria);
       Update update = new Update();


       update.push("postIds", postId);


       mongoTemplate.updateFirst(query, update, BlackList.class);

       blackList = blackListRepository.findByUserId(userId);

       return blackList.get().getPostIds();

   }
}
