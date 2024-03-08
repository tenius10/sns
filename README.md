# SNS 프로젝트

<br/>웹 기반으로 SNS 기능을 제공하는 API 서버입니다.<br/><br/>

__인원__ : 1명 <br/><br/>
__구현 기간__ : 2023/7/21 ~ 2024/3/2 (현재) <br/><br/>
__개발 환경__ : SpringBoot, JPA, MariaDB, Redis, Swagger, React <br/><br/>
__키워드__ : RestfulAPI, JWT<br/><br/>
<br/>

## ◼️ 핵심 기능
- __게시글 CRUD__
- __커서 기반 페이징__
- __JWT 인증 (회원가입, 로그인, 자동로그인, 로그아웃, 토큰 밴)__
- __작성자 프로필__
- __조회수, 좋아요__
- __댓글__
- __게시글 첨부파일 (사진)__
- __유저 페이지 조회__
- __게시글 검색 (by. 내용 / 작성자)__
- __팔로우__

<br/><br/>

## ◼️ 프론트 서버
https://github.com/tenius10/sns-front

<br/>

![image](https://github.com/tenius10/sns/assets/108507183/6ee2507f-6364-4963-9a31-ba2918dd1cb8)
<br/><br/>
![image](https://github.com/tenius10/sns/assets/108507183/1f24abd9-36f4-4678-8d6f-b38425d2022a)
<br/><br/>
![image](https://github.com/tenius10/sns/assets/108507183/decd9427-9a92-481e-b737-8d5259b7b678)
<br/>
![image](https://github.com/tenius10/sns/assets/108507183/899c099d-0f44-4591-a051-6356a7e1dc9f)
<br/>
![image](https://github.com/tenius10/sns/assets/108507183/2acba8c3-a50b-4f6b-8cb1-0631df5cd47b)

![image](https://github.com/tenius10/sns/assets/108507183/b60be552-f325-44a5-8a8d-065308391059)
<br/><br/><br/>

## ◼️ API 명세서

![image](https://github.com/tenius10/sns/assets/108507183/beb917c0-54fd-49c7-a6f7-22176b6a26c7)

![image](https://github.com/tenius10/sns/assets/108507183/89a954fb-f3e5-493f-86ce-72788f5a81bc)

![image](https://github.com/tenius10/sns/assets/108507183/1b36a908-dd4b-428d-aae3-d607274a533b)

![image](https://github.com/tenius10/sns/assets/108507183/a3cd90bb-d9b8-47f2-95fa-aac068a3bf73)

![image](https://github.com/tenius10/sns/assets/108507183/ab77b32e-e0fa-4260-9cfe-0390abf455eb)

<br/><br/><br/>

## ◼️ DB 설계
![image](https://github.com/tenius10/sns/assets/108507183/d17eb73a-cf19-4d6d-9b56-d8af7040cc62)

follow 테이블은 user_info 테이블의 uid를 외래키로 가집니다.<br/>
Follow 엔티티와 UserInfo 엔티티 사이에 연관관계를 설정하면 Follow 엔티티 생성시, UserInfo 엔티티를 할당해야 합니다.<br/>
이 과정에서 DB에 접근하며 발생하는 성능 저하를 막고자 Follow 엔티티에는 연관관계를 설정하지 않았습니다.<br/>
<br/>

## ◼️ 이후 추가할 기능
- __해시태그__
- __알림__
- __유저 검색 (by. 닉네임 / 유저ID)__
- __피드 시스템__
- __소셜 로그인__
- __회원 탈퇴__

<br/><br/><br/>
