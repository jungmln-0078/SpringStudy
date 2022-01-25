package example.mybatis.blog.service;

import example.mybatis.blog.mapper.MemberMapper;
import example.mybatis.blog.model.Member;
import example.mybatis.blog.model.MemberDTO;
import example.mybatis.blog.response.ResponseBuilder;
import example.mybatis.blog.response.ResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private MemberMapper memberMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Member> getMembers() throws DataAccessException {
        return memberMapper.getMembers();
    }

    public Member getMemberByEmail(String email) throws DataAccessException {
        return memberMapper.getMemberByEmail(email);
    }

    public Member getMemberById(long mid) throws DataAccessException {
        return memberMapper.getMemberById(mid);
    }

    public Member getMemberByPw(String email, String password) throws DataAccessException {
        Member member = getMemberByEmail(email);
        if (passwordEncoder.matches(password, member.getPassword())) {
            return member;
        } else {
            return null;
        }
    }

    public BigInteger addMember(MemberDTO memberDTO) throws DataAccessException {
        Map<String, Object> map = new HashMap<>();
        map.put("email", memberDTO.getEmail());
        map.put("password", passwordEncoder.encode(memberDTO.getPassword()));
        memberMapper.joinMember(map);
        return (BigInteger) map.get("mid");
    }

    public long updateMember(long mid, MemberDTO memberDTO) throws DataAccessException {
        return memberMapper.updateMember(mid, memberDTO.getEmail(), passwordEncoder.encode(memberDTO.getPassword()));
    }

    public long deleteMember(long mid) throws DataAccessException {
        return memberMapper.deleteMember(mid);
    }
}
