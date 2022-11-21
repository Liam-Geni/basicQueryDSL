package com.sparta.querydsl.entity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberTest {

    @Autowired
    EntityManager em;

    @Test
    public void testEntity(){
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
        //초기화
        // 영속성 컨텍스트에 있는 object들을 쿼리로 db에 날림
        em.flush();
        // 영속성 컨텍스트를 완전히 초기화, 캐쉬도 날림
        em.clear();

        //jpql 확인
        List<Member> members =  em.createQuery("select m from Member m", Member.class)
                .getResultList();

        for(Member member: members){
            System.out.println("member = " + member);
            System.out.println("-> member.team" + member.getTeam());
        }
    }
}