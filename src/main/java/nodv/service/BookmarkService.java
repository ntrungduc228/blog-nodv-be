package nodv.service;

import nodv.model.Bookmark;
import nodv.model.Post;
import nodv.payload.BookmarkDTO;
import nodv.repository.BookmarkRepository;
import nodv.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BookmarkService {
    @Autowired
    BookmarkRepository bookmarkRepository;

    @Autowired
    PostService postService;

    @Autowired
    PostRepository postRepository;

    @Autowired
    MongoTemplate mongoTemplate;

    public Bookmark createBookmark(BookmarkDTO bookmarkDTO) {
        Bookmark bookmark = new Bookmark();
        bookmark.setUserId(bookmarkDTO.getUserId());
        List<String> listPostIds = new ArrayList<>();
        listPostIds.add(bookmarkDTO.getPostId());
        bookmark.setPostIds(listPostIds);
        return bookmarkRepository.save(bookmark);
    }

    public Bookmark addPostIdToBookmark(String userId, String postId) throws Exception {
        Optional<Bookmark> bookmark = bookmarkRepository.findByUserId(userId);
        if(!bookmark.isPresent()){
//            BookmarkDTO bookmarkDTO = new BookmarkDTO(userId, postId);
            bookmark = Optional.ofNullable(this.createBookmark(new BookmarkDTO(userId, postId)));
        }

        for(String findPostId: bookmark.get().getPostIds()){
            if(findPostId.equals(postId)){
                throw new Exception("PostId is exits");
            }
        }

        Query query = new Query();
        Criteria criteria = Criteria.where("userId").is(bookmark.get().getUserId());
        query.addCriteria(criteria);
        Update update = new Update();
        update.push("postIds", postId);
        mongoTemplate.updateFirst(query, update, Bookmark.class);

        bookmark = bookmarkRepository.findByUserId(userId);

        return bookmark.get();
    }

    public Bookmark deletePostIdToBookmark(String userId, String postId) throws Exception {
        Optional<Bookmark> bookmark = bookmarkRepository.findByUserId(userId);
        if(!bookmark.isPresent()){
           throw new Exception("Bookmark not found");
        }

        Query query = new Query();
        Criteria criteria = Criteria.where("userId").is(bookmark.get().getUserId());
        query.addCriteria(criteria);
        Update update = new Update();
        update.pull("postIds", postId);
        mongoTemplate.updateFirst(query, update, Bookmark.class);

        bookmark = bookmarkRepository.findByUserId(userId);
        return bookmark.get();
    }

    public Bookmark findByUserId(String userId) throws Exception {
        Optional<Bookmark> bookmark = bookmarkRepository.findByUserId(userId);
        if(!bookmark.isPresent()){
            throw new Exception("Bookmark by userid not found");
        }

        if(bookmark.get().getPostIds().size() > 0 ){
            List<Post> posts = new ArrayList<>();
            for(String postId : bookmark.get().getPostIds()){
                Optional<Post> post = postRepository.findById(postId);
               if(post.isPresent()) {posts.add(post.get());}
            }
            bookmark.get().setPosts(posts);
//            for(Post post: posts){
//                System.out.println("post ok " + post.getTitle());
//            }
        }



        return bookmark.get();
    }
}