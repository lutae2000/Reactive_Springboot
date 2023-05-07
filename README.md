스프링 실전 활용마스터 책 스터디<br>
<img src="https://image.aladin.co.kr/product/27182/44/cover500/k292731352_1.jpg">

<hr>
Mongo DB + 스프링부트로 진행

JDK17

BlockHound를 사용하면 블로킹 메소드를 모두 찾아줌 
BlockHound 사용시 허용정책 등록해서 검사 안하게 하는 방법도 있으나 범위를 좁혀서 일부 지점만 허용하는게 바람직

## Docker

docker hub jdk : https://hub.docker.com/_/openjdk/tags

이미지 빌드 방법
```
# docker build . --tag hacking-with-spring-boot
```
Dockerfile 없이 도커 이미지 만들기
* paketo build pack 프로젝트에서 빌드팫을 가져와 docker 컨테이너 이미지 생성
```
# ./mvnw spring-boot:build-image
```
만들어진 docker 이미지로 컨테이너 실행
```
# docker run -it -p 8080:8080 hacking-with-spring-boot:latest
```


## RestDoc 적용하면 아래 사진과 같이 API 템플릿 자동 생성
- Restdoc 적용시 아래와 같이 패키지 빌드 재시작 필요(mvn일때)
- 템플릿은 {project_home}/main/asciidoc/index.adoc 에 정의 => 실제 파일생성시엔 target/generated-docs/index.html에 생성
### 참고문서
- 우아한형제들 테크블로그: https://tecoble.techcourse.co.kr/post/2020-08-18-spring-rest-docs/
- 마켓컬리: https://github.com/thefarmersfront/spring-rest-docs-guide/blob/main/src/docs/asciidoc/index.adoc

```
# ./mvnw clean prepare-package
```
![img_1.png](img_1.png)