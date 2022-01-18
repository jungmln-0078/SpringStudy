package example.mybatis.blog.mapper;

import example.mybatis.blog.model.Reply;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReplyMapper {
    List<Reply> getReplies(@Param("aid") long aid);

    Reply getReplyById(@Param("rid") long rid);

    long addReply(@Param("aid") long aid, @Param("author") long author, @Param("content") String content);

    int updateReply(@Param("content") String content, @Param("rid") long rid);

    int deleteReply(long rid);
}
