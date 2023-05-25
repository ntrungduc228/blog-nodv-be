package nodv.service;

import nodv.exception.ForbiddenException;
import nodv.exception.NotFoundException;
import nodv.model.*;
import nodv.payload.MonthlyCount;
import nodv.projection.PostPreviewProjection;
import nodv.repository.BlackListRepository;
import nodv.repository.CommentRepository;
import nodv.repository.PostRepository;
import nodv.security.TokenProvider;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
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

    @Autowired
    BookmarkService bookmarkService;

    @Autowired
    BlackListRepository blackListRepository;

    public Long countAllPosts() {
        return postRepository.count();
    }

    public Post findById(String id) {
        Optional<Post> post = postRepository.findById(id);
        if (post.isEmpty()) {
            throw new NotFoundException("Post is not found");
        }

        return post.get();
    }

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

    public Post updatePostStatus(String id, PostStatus status) {
        Optional<Post> post = postRepository.findById(id);
        if (post.isEmpty()) {
            throw new NotFoundException("Post is not found");
        }
        post.get().setStatus(status);
        postRepository.save(post.get());
        return post.get();
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

    public Slice<Post> findAll(int page, int limit, String title, String topicSlug) {
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


    public Page<Document> findByFilter(int page, int pageSize, String id, String topicSlug, String title, String authorId, String sortBy, String sortDirection, String userId) {
        Criteria criteria = new Criteria();

        if (topicSlug != null && !topicSlug.isEmpty()) {
            Topic topic = topicService.findBySlug(topicSlug);
            criteria.and("topics.id").is(topic.getId());
        }
        if (title != null && !title.isEmpty()) {
            criteria.and("title").regex(title, "i");
        }

        if (authorId != null && !authorId.isEmpty()) {
            criteria.and("user.id").is(authorId);
        }

        if (id != null && !id.isEmpty()) {
            criteria.and("id").is(id);
        } else if (userId != null) {
            User user = userService.findById(userId);
            boolean isAdmin = user.getRole() == Role.ADMIN;
            if (!isAdmin) {
                criteria.and("status").in(PostStatus.NORMAL, PostStatus.REPORTED);
            }
            Optional<BlackList> blackList = blackListRepository.findByUserId(userId);
            blackList.ifPresent(list -> criteria.and("id").nin(list.getPostIds()));
        }
        criteria.and("isPublish").is(true);
        MatchOperation matchOperation = Aggregation.match(criteria);
        LookupOperation userLookup = LookupOperation.newLookup()
                .from("users")
                .localField("user.$id")
                .foreignField("_id")
                .as("user");
        UnwindOperation userUnwind = Aggregation.unwind("user");
        ProjectionOperation projectionOperation = Aggregation.project()
                .and(ConvertOperators.ToString.toString("$_id")).as("id")
                .andInclude("title", "timeRead", "createdDate", "subtitle", "isPublish", "thumbnail", "status")
                .and("user._id").as("user._id")
                .and("user.username").as("user.username")
                .and("user.avatar").as("user.avatar")
                .and("user._id").as("userId")
                .and("user.email").as("user.email")
                .and("topics").as("topics")
                .and(ConvertOperators.ToString.toString("$userId")).as("user.id")
                .and(ArrayOperators.Size.lengthOfArray(ConditionalOperators.ifNull("userLikeIds")
                        .then(Collections.emptyList())))
                .as("likeCount");
        SortOperation sortOperation;
        Sort.Direction direction = Sort.Direction.DESC;
        if ("ASC".equalsIgnoreCase(sortDirection)) {
            direction = Sort.Direction.ASC;
        }
        if ("trending".equalsIgnoreCase(sortBy)) {
            sortOperation = Aggregation.sort(direction, "likeCount");
        } else {
            String sort = "createdDate";
            if (sortBy != null) sort = sortBy;
            sortOperation = switch (sort) {
                case "title" -> Aggregation.sort(direction, "title");
                case "author" -> Aggregation.sort(direction, "user.username");
                default -> Aggregation.sort(direction, "createdDate");
            };
        }

        SkipOperation skipOperation = Aggregation.skip(page * pageSize);
        LimitOperation limitOperation = Aggregation.limit(pageSize);
        Aggregation aggregation = Aggregation.newAggregation(
                matchOperation,
                userLookup,
                userUnwind,
                Aggregation.lookup("topics", "topics.$id", "_id", "topics"),
                projectionOperation,
                sortOperation,
                skipOperation,
                limitOperation
        );

        AggregationResults<Document> aggregationResults = mongoTemplate.aggregate(aggregation, Post.class, Document.class);
        List<Document> documents = aggregationResults.getMappedResults();
        long totalCount = mongoTemplate.count(Query.query(criteria), Post.class);
        return new PageImpl<>(documents, PageRequest.of(page, pageSize), totalCount);
    }

    public Page<PostPreviewProjection> findFollowing(int page, int limit, String userId) {
        User user = userService.findById(userId);
        List<String> followingIds = user.getFollowingId();
        Pageable pageable = PageRequest.of(page, limit, Sort.by("createdDate").descending());
        return postRepository.findByUserIdInAndIsPublishTrue(followingIds, pageable);
    }

    public Page<PostPreviewProjection> findOwnPosts(int page, int limit, String userId, Boolean isPublish) {
        Pageable pageable = PageRequest.of(page, limit, Sort.by("createdDate").descending());
        if (isPublish == null) {
            return postRepository.findByUserId(userId, pageable);
        } else {
            return postRepository.findByUserIdAndIsPublish(userId, isPublish, pageable);
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
                .and(ConvertOperators.ToString.toString("$_id")).as("id")
                .andInclude("title", "timeRead", "createdDate")
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

    public Page<PostPreviewProjection> findByUserBookmark(int page, int limit, String userId) {
        Pageable pageable = PageRequest.of(page, limit, Sort.by("createdDate").descending());
        List<String> ids = bookmarkService.getListPostIds(userId);
        return postRepository.findByIdIn(ids, pageable);
    }

    public List<MonthlyCount> getMonthlyCount() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.project()
                        .and(DateOperators.DateToString.dateOf("lastModifiedDate").toString("%Y-%m")).as("month"),
                Aggregation.group("month")
                        .count().as("total"),
                Aggregation.project("total").and("_id").as("month")

        );

        AggregationResults<MonthlyCount> results = mongoTemplate.aggregate(
                aggregation, "posts", MonthlyCount.class
        );

        List<MonthlyCount> monthlyCounts = results.getMappedResults();
        return monthlyCounts;
    }
}