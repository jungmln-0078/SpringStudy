package example.mybatis.blog.service;

import example.mybatis.blog.mapper.ReplyMapper;
import example.mybatis.blog.model.Reply;
import example.mybatis.blog.model.ReplyDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ReplyService {

    @Autowired
    private ReplyMapper replyMapper;

    public List<Reply> getReplies(long aid) {
        return replyMapper.getReplies(aid);
    }

    public Reply getReplyById(long rid) {
        return replyMapper.getReplyById(rid);
    }

    public BigInteger addReply(ReplyDTO replyDTO) throws DataAccessException {
        Map<String, Object> param = new HashMap<>();
        param.put("aid", replyDTO.getAid());
        param.put("author", replyDTO.getAuthor());
        param.put("content", replyDTO.getContent());
        replyMapper.addReply(replyDTO.getAid(), replyDTO.getAuthor(), replyDTO.getContent());
        return (BigInteger) param.get("rid");
    }

    public int updateReply(long rid, String content) throws DataAccessException {
        return replyMapper.updateReply(content, rid);
    }

    public int deleteReply(long rid) throws DataAccessException {
        return replyMapper.deleteReply(rid);
    }
}
