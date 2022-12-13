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
    PostRepository postRepository;

    @Autowired
    MongoTemplate mongoTemplate;

    public Bookmark createBookmark(BookmarkDTO bookmarkDTO) {
        Bookmark bookmark = new Bookmark();
        bookmark.setUserId(bookmarkDTO.getUserId());
        List<String> listPostIds = new ArrayList<>();
        System.out.println(bookmarkDTO.getPostId().isEmpty());
        if(!bookmarkDTO.getPostId().isEmpty()) {
            listPostIds.add(bookmarkDTO.getPostId());
        }
        bookmark.setPostIds(listPostIds);
        return bookmarkRepository.save(bookmark);
    }

    public List<String> getListPostIds(String userId) {
        Optional<Bookmark> bookmark = bookmarkRepository.findByUserId(userId);
        if(!bookmark.isPresent()){
            bookmark = Optional.ofNullable(this.createBookmark(new BookmarkDTO(userId, "")));
        }

        return bookmark.get().getPostIds();
    }

    public List<String> updatePostIdToBookmark(String userId, String postId) throws Exception {
        Optional<Bookmark> bookmark = bookmarkRepository.findByUserId(userId);
        if(!bookmark.isPresent()){
            bookmark = Optional.ofNullable(this.createBookmark(new BookmarkDTO(userId, postId)));
        }

        Boolean postIdIsExists = false;
        if(bookmark.get().getPostIds().contains(postId)){ postIdIsExists = true;}

        Query query = new Query();
        Criteria criteria = Criteria.where("userId").is(bookmark.get().getUserId());
        query.addCriteria(criteria);
        Update update = new Update();
        if(postIdIsExists) {
            update.pull("postIds", postId);
        }else {
            update.push("postIds", postId);
        }
        mongoTemplate.updateFirst(query, update, Bookmark.class);

        bookmark = bookmarkRepository.findByUserId(userId);

        return bookmark.get().getPostIds();
    }

    public Bookmark findByUserId(String userId) throws Exception {
        Optional<Bookmark> bookmark = bookmarkRepository.findByUserId(userId);
        if(!bookmark.isPresent()){
            bookmark = Optional.ofNullable(this.createBookmark(new BookmarkDTO(userId, "")));
            bookmark.get().setPosts(new ArrayList<Post>());
            return bookmark.get();
        }

        if(bookmark.get().getPostIds().size() > 0 ) {
            List<Post> posts = new ArrayList<>();
            for (String postId : bookmark.get().getPostIds()) {
                Optional<Post> post = postRepository.findById(postId);
                if (post.isPresent()) {
                    posts.add(post.get());
                }
            }
            bookmark.get().setPosts(posts);

        }

        return bookmark.get();
    }
}