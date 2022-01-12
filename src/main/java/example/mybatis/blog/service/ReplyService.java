package example.mybatis.blog.service;

import example.mybatis.blog.controller.ArticleController;
import example.mybatis.blog.model.ArticleDTO;
import example.mybatis.blog.model.Reply;
import example.mybatis.blog.model.ReplyDTO;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private SqlSessionTemplate sqlSessionTemplate;

    public List<Reply> getReplies(long aid) {
        Map<String, Object> param = new HashMap<>();
        param.put("aid", aid);
        return sqlSessionTemplate.selectList("mybatis.ReplyMapper.getReplies", param);
    }

    public BigInteger addReply(ReplyDTO replyDTO) throws DataAccessException {
        Map<String, Object> param = new HashMap<>();
        param.put("aid", replyDTO.getAid());
        param.put("author", replyDTO.getAuthor());
        param.put("content", replyDTO.getContent());
        sqlSessionTemplate.insert("mybatis.ReplyMapper.addReply", param);
        return (BigInteger) param.get("rid");
    }

    public int updateReply(long rid, String content) throws DataAccessException {
        Map<String, Object> param = new HashMap<>();
        param.put("rid", rid);
        param.put("content", content);
        return sqlSessionTemplate.update("mybatis.ReplyMapper.updateReply", param);
    }

    public int deleteReply(long rid) throws DataAccessException {
        Map<String, Object> param = new HashMap<>();
        param.put("rid", rid);
        return sqlSessionTemplate.delete("mybatis.ReplyMapper.deleteReply", param);
    }
}
