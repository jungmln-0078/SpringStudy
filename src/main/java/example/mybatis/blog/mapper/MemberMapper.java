package example.mybatis.blog.mapper;

import example.mybatis.blog.model.Member;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface MemberMapper {
    List<Member> getMembers();

    Member getMemberByPk(@Param("email") String email);

    long joinMember(Map<String, Object> map);

    long updateMember(@Param("mid") long mid, @Param("email") String email, @Param("password") String password);

    long deleteMember(long mid);
}
