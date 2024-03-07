-- 임시 sql

create table bookmark (
                          created_at timestamp(6),
                          idea_id bigint not null,
                          member_id bigint not null,
                          updated_at timestamp(6),
                          primary key (idea_id, member_id)
);

create table branch (
                        id bigint generated by default as identity,
                        name varchar(255) not null,
                        primary key (id)
);

create table comment (
                         created_at timestamp(6),
                         id bigint generated by default as identity,
                         idea_id bigint,
                         member_id bigint,
                         parent_comment_id bigint,
                         updated_at timestamp(6),
                         deleted boolean,
                         content clob not null,
                         primary key (id)
);

create table comment_like (
                              comment_id bigint,
                              created_at timestamp(6),
                              id bigint generated by default as identity,
                              member_id bigint,
                              updated_at timestamp(6),
                              primary key (id)
);

create table hit (
                     created_at timestamp(6),
                     id bigint generated by default as identity,
                     idea_id bigint,
                     member_id bigint,
                     updated_at timestamp(6),
                     primary key (id)
);

create table idea (
                      created_at timestamp(6),
                      id bigint generated by default as identity,
                      member_id bigint,
                      updated_at timestamp(6),
                      title varchar(20),
                      cooperation_way varchar(255) not null,
                      recruitment_place bigint,
                      introduce clob not null,
                      primary key (id)
);

create table idea_branch (
                             branch_id bigint,
                             id bigint generated by default as identity,
                             idea_id bigint,
                             primary key (id)
);

create table idea_like (
                           created_at timestamp(6),
                           idea_id bigint not null,
                           member_id bigint not null,
                           updated_at timestamp(6),
                           primary key (idea_id, member_id)
);

create table idea_purpose (
                              id bigint generated by default as identity,
                              idea_id bigint,
                              purpose_id bigint,
                              primary key (id)
);

create table idea_skill_category
(
    id             bigint generated by default as identity,
    idea_id        bigint,
    skill_category bigint,
    primary key (id)
);

create table member (
                        created_at timestamp(6),
                        id bigint generated by default as identity,
                        main_skill_id bigint,
                        updated_at timestamp(6),
                        email varchar(255) not null,
                        introduce varchar(255),
                        living_place varchar(255) check (living_place in ('SEOUL','BUSAN','DAEGU','INCHEON','GWANGJU','DAEJEON','ULSAN','SEJONG','GYEONGGI','GANGWON','CHUNGBUK','CHUNGNAM','JEONBUK','JEONNAM','GYEUNGBUK','GYEUNGNAM','JEJU')),
                        nickname varchar(255) not null,
                        oauth_server_id varchar(255) not null,
                        oauth_server_type varchar(255) not null check (oauth_server_type in ('KAKAO')),
                        profile_image_url varchar(255) not null,
                        working_place varchar(255),
                        primary key (id),
                        constraint oauth_id_unique unique (oauth_server_id, oauth_server_type)
);

create table member_branch (
                               branch_id bigint,
                               id bigint generated by default as identity,
                               member_id bigint,
                               primary key (id)
);

create table member_purpose (
                                id bigint generated by default as identity,
                                member_id bigint,
                                purpose_id bigint,
                                primary key (id)
);

create table member_skill_category (
                                       id bigint generated by default as identity,
                                       member_id bigint not null,
                                       skill_category bigint not null,
                                       skill_level varchar(255) not null check (skill_level in ('HIGH','MIDDLE','LOW')),
                                       primary key (id)
);

create table purpose (
                         id bigint generated by default as identity,
                         name varchar(255) not null,
                         primary key (id)
);

create table region (
                        id bigint generated by default as identity,
                        name varchar(255) check (name in ('KOREA','SEOUL','BUSAN','DAEGU','INCHEON','GWANGJU','DAEJEON','ULSAN','SEJONG','GYEONGGI','GANGWON','CHUNGBUK','CHUNGNAM','JEONBUK','JEONNAM','GYEUNGBUK','GYEUNGNAM','JEJU')),
                        primary key (id)
);

create table skill_category (
                                id bigint generated by default as identity,
                                parent_skill_category_id bigint,
                                name varchar(255) not null,
                                primary key (id)
);

alter table if exists bookmark
    add constraint FKbxrtrldxkuhkx7j9v5ptd3jn9
    foreign key (idea_id)
    references idea;

alter table if exists bookmark
    add constraint FK5bm7rup91j277mc7gg63akie2
    foreign key (member_id)
    references member;

alter table if exists comment
    add constraint FKmrrrpi513ssu63i2783jyiv9m
    foreign key (member_id)
    references member;

alter table if exists comment
    add constraint FKpo553b3rappx4h6o9lb6lr7xy
    foreign key (idea_id)
    references idea;

alter table if exists comment
    add constraint FKhvh0e2ybgg16bpu229a5teje7
    foreign key (parent_comment_id)
    references comment
    on delete cascade;

alter table if exists comment_like
    add constraint FKqlv8phl1ibeh0efv4dbn3720p
    foreign key (comment_id)
    references comment;

alter table if exists comment_like
    add constraint FKjtrao5djvpcj49cxcmbenif3g
    foreign key (member_id)
    references member;

alter table if exists hit
    add constraint FKsa302nd8mhaq4dabfpwtwtxrp
    foreign key (idea_id)
    references idea;

alter table if exists hit
    add constraint FKgqw2tpladikko7g7syvfiwtvf
    foreign key (member_id)
    references member;

alter table if exists idea
    add constraint FKgpeqyqt3tturpmjrc06ffmvab
    foreign key (member_id)
    references member;

alter table if exists idea_branch
    add constraint FKrwcs8eltbq5ihnmtkraya5c2m
    foreign key (branch_id)
    references branch;

alter table if exists idea_branch
    add constraint FKtqxy0v2yp41qc2tlu8k7jldw3
    foreign key (idea_id)
    references idea;

alter table if exists idea_like
    add constraint FKsp1mo36vkl3y5j3s6b6w89g8d
    foreign key (idea_id)
    references idea;

alter table if exists idea_like
    add constraint FKjmonb75khqygkkusrnhdxsw56
    foreign key (member_id)
    references member;

alter table if exists idea_purpose
    add constraint FK6h4yvcrxws5jn5ba02ho69nf
    foreign key (idea_id)
    references idea;

alter table if exists idea_purpose
    add constraint FK9qspw0avnmpdc8ltm8fm9kodw
    foreign key (purpose_id)
    references purpose;

alter table if exists idea_skill_category
    add constraint FKhi5bo35jyd3ta9gcojvb09s9s
    foreign key (idea_id)
    references idea;

alter table if exists idea_skill_category
    add constraint FKlypkdm37dsogajt1ccv6ovone
    foreign key (skill_category)
    references skill_category;

alter table if exists member
    add constraint FK9yapbdq06a05trcn6h86erd4u
    foreign key (main_skill_id)
    references skill_category;

alter table if exists member_branch
    add constraint FKoq3afwr2d7uw072a800jojwmf
    foreign key (branch_id)
    references branch;

alter table if exists member_branch
    add constraint FK83d83uupljilyc0k5dkbyt3lx
    foreign key (member_id)
    references member;

alter table if exists member_purpose
    add constraint FK6rv1gd9py7p02w1nhc7a7ubti
    foreign key (member_id)
    references member;

alter table if exists member_purpose
    add constraint FKfekv5ei0cwdxqurxr2cffckh8
    foreign key (purpose_id)
    references purpose;

alter table if exists member_skill_category
    add constraint FKpxa06ksaeegiugp0rbswg9acd
    foreign key (member_id)
    references member;

alter table if exists member_skill_category
    add constraint FKjxds6e09v33di5m98nt5o6m2b
    foreign key (skill_category)
    references skill_category;

alter table if exists skill_category
    add constraint FKrc360mgkpmuubi7qu4oub769n
    foreign key (parent_skill_category_id)
    references skill_category;


insert into skill_category(id, parent_skill_category_id, name) values (1, 1, '개발');
insert into skill_category(id, parent_skill_category_id, name) values (2, 1, 'BE');
insert into skill_category(id, parent_skill_category_id, name) values (3, 1, 'FE');

insert into skill_category(id, parent_skill_category_id, name) values (4, 4, '디자인');
insert into skill_category(id, parent_skill_category_id, name) values (5, 4, '게임디자인');
insert into skill_category(id, parent_skill_category_id, name) values (6, 4, '시각디자인');

insert into purpose(id, name) values (1, '사이드 프로젝트');
insert into purpose(id, name) values (2, '창업');
insert into purpose(id, name) values (3, '공모전');

insert into region(id, name) values (1, 'SEOUL');
insert into region(id, name) values (2, 'BUSAN');
insert into region(id, name) values (3, 'DAEGU');

insert into branch(id, name) values (1, 'IT');
insert into branch(id, name) values (2, '게임');
insert into branch(id, name) values (3, '웹툰');

-- insert into member(main_skill_id, email, nickname, oauth_server_id, oauth_server_type,
--                    profile_image_url)
-- values (1, 'conceptbe@gmail.com', '컨셉비', 'oauth_server_id', 'KAKAO', 'test');
--
-- insert into member(main_skill_id, email, nickname, oauth_server_id, oauth_server_type,
--                    profile_image_url)
-- values (2, 'conceptbe1@gmail.com', '컨셉비1', 'oauth_server_id1', 'KAKAO', 'test1');
--

-- insert into idea(member_id, title, cooperation_way, recruitment_place, introduce, created_at)
-- values(1, '테스트1', 'ONLINE', 1, '테스트', '2023-08-10 07:47:50');
--
-- insert into idea(member_id, title, cooperation_way, recruitment_place, introduce, created_at)
-- values(2, '테스트2', 'ONLINE', 1, '테스트', '2023-08-10 08:47:50');

-- insert into bookmark(member_id, idea_id)
-- values (1, 1);
--
-- insert into bookmark(member_id, idea_id)
-- values (2, 1);
--
-- insert into bookmark(member_id, idea_id)
-- values (1, 2);
