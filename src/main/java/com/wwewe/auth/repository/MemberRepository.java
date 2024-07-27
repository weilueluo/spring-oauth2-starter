package com.wwewe.auth.repository;

import com.wwewe.auth.model.dao.Member;
import com.wwewe.auth.utils.EmailUtils;
import lombok.RequiredArgsConstructor;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class MemberRepository {

    private final SessionFactory sessionFactory;

    public Optional<Member> findById(Long id) {
        return sessionFactory.fromSession(session -> Optional.ofNullable(
                session.createNativeQuery("select * from member where member.id = :id", Member.class)
                        .setParameter("id", id)
                        .getSingleResultOrNull()
        ));
    }

    public Optional<Member> findByEmail(String email) {
        return sessionFactory.fromSession(session -> Optional.ofNullable(
                session.createNativeQuery("select * from member where member.email = :email", Member.class)
                        .setParameter("email", email)
                        .getSingleResultOrNull()
        ));
    }


    public Member createByEmail(String email) {
        Member member = Member.builder()
                .email(email)
                .username(EmailUtils.getName(email))
                .build();

        sessionFactory.inTransaction(session ->
                session.persist(member)
        );

        return member;
    }

    public void deleteMember(Long id) {
        sessionFactory.inTransaction(session ->
                session.createNativeQuery("delete from member where member.id = :id", Member.class)
                        .setParameter("id", id)
                        .executeUpdate()
        );
    }
}
