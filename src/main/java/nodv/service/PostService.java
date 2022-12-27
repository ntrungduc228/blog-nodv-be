package nodv.service;

import nodv.exception.ForbiddenException;
import nodv.exception.NotFoundException;
import nodv.controller.model.Post;
import nodv.controller.model.Topic;
import nodv.controller.model.User;
import nodv.repository.CommentRepository;
import nodv.repository.PostRepository;
import nodv.security.TokenProvider;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PostService {
    @Autowired
    PostRepository postRepository;
    @Autowired
    UserService userService;
    @Autowired
    CommentRepository commentRepository;

    @Autowired
    TokenProvider tokenProvider;
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    TopicService topicService;

    // mongodb-method
    public Post findById(String id, String userId) {
        Optional<Post> post = postRepository.findById(id);
        if (post.isEmpty()) {
            throw new NotFoundException("Post is not found");
        }
        Post postResponse = post.get();
        if (!postResponse.getUser().getId().equals(userId) && !postResponse.getIsPublish()) {
            throw new NotFoundException("Post is not found");
        }

        return post.get();
    }

    public Post createPost(Post post, String userId) {
        User user = userService.findById(userId);
        post.setUserId(user.getId());
        post.setUser(user);
        post.setIsPublish(true);
        post.setTopics(topicService.checkAndCreateListTopic(post.getTopics()));
        return postRepository.save(post);
    }

    public Post updatePost(String id, Post post, String userId) {
        Post updatePost = findById(id, userId);
        if (updatePost.getUser().getId().equals(userId)) {
            updatePost.setTitle(post.getTitle());
            updatePost.setContent(post.getContent());
            updatePost.setThumbnail(post.getThumbnail());
            updatePost.setTimeRead(post.getTimeRead());
            updatePost.setTopics(topicService.checkAndCreateListTopic(post.getTopics()));
            updatePost.setSubtitle(post.getSubtitle());
            return postRepository.save(updatePost);
        } else throw new ForbiddenException("You do not have permission to delete this post");

    }

    public void deletePost(String id, String userId) {
        Post post = findById(id, userId);
        if (post.getUser().getId().equals(userId)) {
            postRepository.deleteById(id);
            commentRepository.deleteByPostId(id);
        } else throw new ForbiddenException("You do not have permission to delete this post");

    }

    public Post changePublish(String id, String userId, boolean isPublic) {
        Post post = findById(id, userId);
        if (!post.getUser().getId().equals(userId))
            throw new ForbiddenException("You do not have permission to update this post");
        post.setIsPublish(isPublic);
        return postRepository.save(post);
    }

    public Page<Post> findAll(int page, int limit, String title, String topicSlug) {
        Pageable pageable = PageRequest.of(page, limit, Sort.by("createdDate").descending());
        Query query = new Query().with(pageable);
        List<Criteria> criteria = new ArrayList<>();
        criteria.add(Criteria.where("isPublish").is(true));

        if (title != null && !title.isEmpty()) {
            criteria.add(Criteria.where("title").regex(title, "i"));
        }

        if (topicSlug != null && !topicSlug.equals("all") && !topicSlug.isBlank()) {
            Topic topic = topicService.findBySlug(topicSlug);
            criteria.add(Criteria.where("topics.id").is(topic.getId()));
        }

        query.addCriteria(new Criteria().andOperator(criteria.toArray(new Criteria[0])));
        query.fields().exclude("content");
        return PageableExecutionUtils.getPage(
                mongoTemplate.find(query, Post.class), pageable,
                () -> mongoTemplate.count(query.skip(0).limit(0), Post.class));

    }


    public List<Post> findOwnedPost(String userId, String isPublish) {
        if (isPublish == null) {
            return postRepository.findByUserId(userId);
        } else {
            return postRepository.findByUserIdAndIsPublish(userId, Boolean.valueOf(isPublish));
        }

    }


    public Post likePost(String id, String userId) {
        Query query = new Query();
        Criteria criteria = Criteria.where("id").is(id);
        query.addCriteria(criteria);
        Update update = new Update();
        update.push("userLikeIds", userId);
        mongoTemplate.updateFirst(query, update, Post.class);
        return findById(id, userId);
    }

    public Post unlikePost(String id, String userId) {
        Query query = new Query();
        Criteria criteria = Criteria.where("id").is(id);
        query.addCriteria(criteria);
        Update update = new Update();
        update.pull("userLikeIds", userId);
        mongoTemplate.updateFirst(query, update, Post.class);
        return findById(id, userId);
    }

    public List<Document> findTopByLike(int limit) {
        LookupOperation lookupOperation = Aggregation.lookup("users", "user.$id", "_id", "user");
        ProjectionOperation projectionOperation = Aggregation.project()
                .andInclude("title", "timeRead", "id", "createdDate")
                .andExclude("_id")
                .and(ArrayOperators.Size.lengthOfArray(ConditionalOperators.ifNull("userLikeIds")
                        .then(Collections.emptyList())))
                .as("likeCount")
                .and(ArrayOperators.ArrayElemAt.arrayOf("user").elementAt(0)).as("user");
        SortOperation sortOperation = Aggregation.sort(Sort.Direction.DESC, "likeCount");
        LimitOperation limitOperation = Aggregation.limit(limit);
        Aggregation aggregation = Aggregation.newAggregation(
                lookupOperation,
                projectionOperation,
                sortOperation,
                limitOperation
        );
        return mongoTemplate.aggregate(aggregation, Post.class, Document.class).getMappedResults();
    }
}