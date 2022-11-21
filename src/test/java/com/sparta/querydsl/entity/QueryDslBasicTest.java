package com.sparta.querydsl.entity;


import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;

import static com.sparta.querydsl.entity.QMember.member;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class QueryDslBasicTest {

    @Autowired
    EntityManager em;

    JPAQueryFactory queryFactory;


    //테스트 실행전에 데이터를 미리 세팅
    @BeforeEach
    public void before(){
        queryFactory = new JPAQueryFactory(em);
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
    }

    //jpql 사용
    //오류가 있어도 일단 컴파일이된다 -> 컴파일된후에 오류를 찾을수 있다.
    @Test
    public void startJPQL(){
        //member1을 찾아라.
        String qlString = "select m from Member m" +
                          " where m.username = :username";

        Member findMember = em.createQuery(qlString, Member.class)
                .setParameter("username", "member1") //파라미터 바인딩
                .getSingleResult();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    //queryDsl 사용
    //오류가 생기면 컴파일 시점에서 잡아준다.
    //파라미터 바인딩을 자동으로 해결해준다.
    @Test
    public void startQueryDsl(){
        //별칭 직접 주기
//        QMember m = new QMember("m");
        //static import 사용 깔끔하다
        Member findMember = queryFactory
                .select(member)
                .from(member)
                .where(member.username.eq("member1")) //파라미터 바인딩 처리
                .fetchOne();
        assert findMember != null;
        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    //검색 조건 쿼리
    @Test
    public void search(){
        Member findMember = queryFactory
                .selectFrom(member)
                .where(member.username.eq("member1")               //username == "member1"
                          .and(member.username.ne("member2"))        //username != "member2"
                          .and(member.username.eq("member2").not())  //username != "member2"
//                        .and(member.username.isNotNull())                //이름이 is not null
                          .and(member.age.in(10, 20))            //age in (10, 20) 나이가 10살이거나 20살인사람
//                        .and(member.age.notIn(10, 20))         //age not in(10 , 20)
//                        .and(member.age.between(10, 30))                 //나이가 10 에서 20 사이
//                        .and(member.age.goe(30))                   //나이가 30보다 크거나 같음
//                        .and(member.age.gt(30))                    //나이가 30보다 큰값
                          .and(member.age.loe(30))                   //나이가 30보다 작거나 같은값
                          .and(member.age.lt(30))                    //나이가 30보다 작음
                          .and(member.username.like("member%")))      //like 검색
//                        .and(member.username.contains("member"))         //like '%member%' 검색
//                        .and(member.username.startsWith("member")))    //like 'member%' 검색
                        .fetchOne();
        assert findMember != null;
        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    //검색 조건 쿼리
    @Test
    public void searchAndParam(){
        Member findMember = queryFactory
                .selectFrom(member)
                .where(
                        member.username.eq("member1"),
                        member.username.ne("member2")
                )
                .fetchOne();
        assert findMember != null;
        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    //결과 조회
    @Test
    public void resultFetch(){
//        //리스트 조회
//        List<Member> fetch = queryFactory
//                .selectFrom(member)
//                .fetch();
//        //단건 조회
//        Member fetchOne = queryFactory
//                .selectFrom(member)
//                .fetchOne();
//        //첫번째 조회
//        Member fetchFirst = queryFactory
//                .selectFrom(member)
//                .fetchFirst();
//        QueryResults<Member> results = queryFactory
//                .selectFrom(member)
//                .fetchResults();
//        //결과값 다 더한값
//        long total = results.getTotal();
//        //결과값 안의 내용물
//        List<Member> content = results.getResults();
//        //몇개 까지 가져와 -> 페이징
//        long limit = results.getLimit();
//        //몇에서 몇에서 부터 가져와 -> 페이징
//        long offset = results.getOffset();

        long totalCnt = queryFactory
                .selectFrom(member)
                .fetchCount();

    }
}
