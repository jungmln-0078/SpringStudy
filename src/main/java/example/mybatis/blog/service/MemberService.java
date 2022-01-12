package example.mybatis.blog.service;

import example.mybatis.blog.model.Member;
import example.mybatis.blog.model.MemberDTO;
import org.mybatis.spring.SqlSessionTemplate;
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
public class MemberService {

    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

    public List<Member> getMembers() throws DataAccessException  {
        return sqlSessionTemplate.selectList("mybatis.MemberMapper.getMembers");
    }

    public Member getMemberByPk(String email, String nickname) throws DataAccessException {
        Map<String, Object> param = new HashMap<>();
        param.put("email", email);
        param.put("nickname", nickname);
        return sqlSessionTemplate.selectOne("mybatis.MemberMapper.getMemberByPk", param);
    }

    public Member getMemberByPw(String email, String password) throws DataAccessException {
        Map<String, Object> param = new HashMap<>();
        param.put("email", email);
        param.put("password", password);
        return sqlSessionTemplate.selectOne("mybatis.MemberMapper.getMemberByPw", param);
    }

    public BigInteger addMember(MemberDTO memberDTO) throws DataAccessException {
        Map<String, Object> param = new HashMap<>();
        param.put("email", memberDTO.getEmail());
        param.put("nickname", memberDTO.getNickname());
        param.put("password", memberDTO.getPassword());
        sqlSessionTemplate.insert("mybatis.MemberMapper.joinMember", param);
        return (BigInteger) param.get("mid");
    }

    public int updateMember(long mid, MemberDTO memberDTO) throws DataAccessException {
        Map<String, Object> param = new HashMap<>();
        param.put("mid", mid);
        param.put("email", memberDTO.getEmail());
        param.put("nickname", memberDTO.getNickname());
        param.put("password", memberDTO.getPassword());
        return sqlSessionTemplate.update("mybatis.MemberMapper.updateMember", param);
    }

    public int deleteMember(long mid) throws DataAccessException {
        Map<String, Object> param = new HashMap<>();
        param.put("mid", mid);
        return sqlSessionTemplate.delete("mybatis.MemberMapper.deleteMember", param);
    }
}
