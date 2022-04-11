-- 활동 지역
insert into region (region_id, area) values (null, '서울');
insert into region (region_id, area) values (null, '부산');
insert into region (region_id, area) values (null, '대구');
insert into region (region_id, area) values (null, '인천');
insert into region (region_id, area) values (null, '광주');
insert into region (region_id, area) values (null, '대전');
insert into region (region_id, area) values (null, '울산');
insert into region (region_id, area) values (null, '세종');
insert into region (region_id, area) values (null, '강원');
insert into region (region_id, area) values (null, '충북');
insert into region (region_id, area) values (null, '충남');
insert into region (region_id, area) values (null, '전북');
insert into region (region_id, area) values (null, '전남');
insert into region (region_id, area) values (null, '경북');
insert into region (region_id, area) values (null, '경남');
insert into region (region_id, area) values (null, '제주');

-- 포지션 타입
insert into position_type (position_type_name, position_type_id) values ('기획', 1);
insert into position_type (position_type_name, position_type_id) values ('디자인', 2);
insert into position_type (position_type_name, position_type_id) values ('개발', 3);

-- 포지션
-- 기획(1xx), 디자인(2xx) 개발(3xx)
insert into positions (position_name, position_type_id, position_id) values ('서비스 기획자', 1, 101);
insert into positions (position_name, position_type_id, position_id) values ('게임 기획자', 1, 102);
insert into positions (position_name, position_type_id, position_id) values ('PM/PO', 1, 103);
insert into positions (position_name, position_type_id, position_id) values ('데이터 분석가', 1, 104);
insert into positions (position_name, position_type_id, position_id) values ('UI/UX 디자이너', 2, 201);
insert into positions (position_name, position_type_id, position_id) values ('UX 디자이너', 2, 202);
insert into positions (position_name, position_type_id, position_id) values ('UI/GUI 디자이너', 2, 203);
insert into positions (position_name, position_type_id, position_id) values ('그래픽 디자이너', 2, 204);
insert into positions (position_name, position_type_id, position_id) values ('3D 디자이너', 2, 205);
insert into positions (position_name, position_type_id, position_id) values ('백엔드 개발자', 3, 301);
insert into positions (position_name, position_type_id, position_id) values ('프론트엔드 개발자', 3, 302);
insert into positions (position_name, position_type_id, position_id) values ('웹 퍼플리셔', 3, 303);
insert into positions (position_name, position_type_id, position_id) values ('서버 관리자', 3, 304);
insert into positions (position_name, position_type_id, position_id) values ('DB 관리자', 3, 305);

-- 스킬
-- 백엔드
insert into skill (skill_id, position_id, skill_name) values (null, 301, 'Java');
insert into skill (skill_id, position_id, skill_name) values (null, 301, 'PHP');
insert into skill (skill_id, position_id, skill_name) values (null, 301, 'Pyhton');
insert into skill (skill_id, position_id, skill_name) values (null, 301, 'Spring');
insert into skill (skill_id, position_id, skill_name) values (null, 301, 'Node.js');
insert into skill (skill_id, position_id, skill_name) values (null, 301, 'C#');
insert into skill (skill_id, position_id, skill_name) values (null, 301, 'C++');
-- 프론트
insert into skill (skill_id, position_id, skill_name) values (null, 302, 'HTML/CSS');
insert into skill (skill_id, position_id, skill_name) values (null, 302, 'JavaScript');
insert into skill (skill_id, position_id, skill_name) values (null, 302, 'Vue');
insert into skill (skill_id, position_id, skill_name) values (null, 302, 'React');
insert into skill (skill_id, position_id, skill_name) values (null, 302, 'JQuery');
-- 디자인
insert into skill (skill_id, position_id, skill_name) values (null, 201, 'Figma');
insert into skill (skill_id, position_id, skill_name) values (null, 201, 'Zeplin');

--서버 관리자
insert into skill (skill_id, position_id, skill_name) values (null, 304, 'AWS');
insert into skill (skill_id, position_id, skill_name) values (null, 304, 'GCP');
insert into skill (skill_id, position_id, skill_name) values (null, 304, 'Azure');
--DB 관리
insert into skill (skill_id, position_id, skill_name) values (null, 305, 'Mysql');
insert into skill (skill_id, position_id, skill_name) values (null, 305, 'Maria');
insert into skill (skill_id, position_id, skill_name) values (null, 305, 'Oracle');
insert into skill (skill_id, position_id, skill_name) values (null, 305, 'NoSql');

insert into tool (tool_id, tool_name) values (1, 'Git');
insert into tool (tool_id, tool_name) values (2, 'Jira');
insert into tool (tool_id, tool_name) values (3, 'Figma');

-- 멤버
insert into member (member_id, created_date, modified_date, bio, emai_check_token, email, email_check_token_generated_at, email_verified, nickname, on_off_status, password, password_find_token, password_find_token_generated_at, profile_img, public_profile, role, study_type)
values (null, NOW(), NOW(), '자기소개', NULL, 'admin@studit.co.kr', NULL, true, 'admin', 'ONOFF', '{noop}12345678', NULL, NULL, NULL, true, 'ADMIN', 'SHARE');

-- insert into member (member_id, created_date, modified_date, bio, emai_check_token, email, email_check_token_generated_at, email_verified, nickname, on_off_status, password, password_find_token, password_find_token_generated_at, profile_img, public_profile, role, study_type)
-- values (null,  NOW(), NOW(), '자기소개', NULL, 'test@test.com', NULL, false, 'test', NULL, '{noop}12345678' , NULL, NULL, NULL, false, 'ADMIN', NULL);
--
-- insert into member (member_id, created_date, modified_date, bio, emai_check_token, email, email_check_token_generated_at, email_verified, nickname, on_off_status, password, password_find_token, password_find_token_generated_at, profile_img, public_profile, role, study_type)
-- values (null,  NOW(), NOW(), '자기소개', NULL, 'test2@test.com', NULL, false, 'test2', NULL, '{noop}12345678', NULL, NULL, NULL, false, 'USER', NULL);
--
-- insert into member (member_id, created_date, modified_date, bio, emai_check_token, email, email_check_token_generated_at, email_verified, nickname, on_off_status, password, password_find_token, password_find_token_generated_at, profile_img, public_profile, role, study_type)
-- values (null,  NOW(), NOW(), '자기소개', NULL, 'test3@test.com', NULL, false, 'test3', NULL, '{noop}12345678', NULL, NULL, NULL, false, 'USER', NULL);

-- insert into member (member_id, created_date, modified_date, bio, emai_check_token, email, email_check_token_generated_at, email_verified, nickname, on_off_status, password, password_find_token, password_find_token_generated_at, profile_img, public_profile, role, study_type)
-- values (null, NOW(), NOW(), 'user0입니다', NULL, 'studit0@studit.co.kr', NULL, false, 'user0', 'ON', '{noop}12345678', NULL, NULL, NULL, true, 'USER', 'SHARE');
--
-- insert into member (member_id, created_date, modified_date, bio, emai_check_token, email, email_check_token_generated_at, email_verified, nickname, on_off_status, password, password_find_token, password_find_token_generated_at, profile_img, public_profile, role, study_type)
-- values (null, NOW(), NOW(), 'user1입니다', NULL, 'studit1@studit.co.kr', NULL, false, 'user1', 'ON', '{noop}12345678', NULL, NULL, NULL, true, 'USER', 'SHARE');
--
-- insert into member (member_id, created_date, modified_date, bio, emai_check_token, email, email_check_token_generated_at, email_verified, nickname, on_off_status, password, password_find_token, password_find_token_generated_at, profile_img, public_profile, role, study_type)
-- values (null, NOW(), NOW(), 'user2입니다', NULL, 'studit2@studit.co.kr', NULL, false, 'user2', 'ON', '{noop}12345678', NULL, NULL, NULL, true, 'USER', 'SHARE');
--
-- insert into member (member_id, created_date, modified_date, bio, emai_check_token, email, email_check_token_generated_at, email_verified, nickname, on_off_status, password, password_find_token, password_find_token_generated_at, profile_img, public_profile, role, study_type)
-- values (null, NOW(), NOW(), 'user3입니다', NULL, 'studit3@studit.co.kr', NULL, false, 'user3', 'ON', '{noop}12345678', NULL, NULL, NULL, true, 'USER', 'SHARE');
--
-- insert into member (member_id, created_date, modified_date, bio, emai_check_token, email, email_check_token_generated_at, email_verified, nickname, on_off_status, password, password_find_token, password_find_token_generated_at, profile_img, public_profile, role, study_type)
-- values (null, NOW(), NOW(), 'user4입니다', NULL, 'studit4@studit.co.kr', NULL, false, 'user4', 'ON', '{noop}12345678', NULL, NULL, NULL, true, 'USER', 'SHARE');
--
-- insert into member (member_id, created_date, modified_date, bio, emai_check_token, email, email_check_token_generated_at, email_verified, nickname, on_off_status, password, password_find_token, password_find_token_generated_at, profile_img, public_profile, role, study_type)
-- values (null, NOW(), NOW(), 'user5입니다', NULL, 'studit5@studit.co.kr', NULL, false, 'user5', 'ON', '{noop}12345678', NULL, NULL, NULL, true, 'USER', 'SHARE');
--
-- insert into member (member_id, created_date, modified_date, bio, emai_check_token, email, email_check_token_generated_at, email_verified, nickname, on_off_status, password, password_find_token, password_find_token_generated_at, profile_img, public_profile, role, study_type)
-- values (null, NOW(), NOW(), 'user6입니다', NULL, 'studit6@studit.co.kr', NULL, false, 'user6', 'ON', '{noop}12345678', NULL, NULL, NULL, true, 'USER', 'SHARE');
--
-- insert into member (member_id, created_date, modified_date, bio, emai_check_token, email, email_check_token_generated_at, email_verified, nickname, on_off_status, password, password_find_token, password_find_token_generated_at, profile_img, public_profile, role, study_type)
-- values (null, NOW(), NOW(), 'user7입니다', NULL, 'studit7@studit.co.kr', NULL, false, 'user7', 'ON', '{noop}12345678', NULL, NULL, NULL, true, 'USER', 'SHARE');