
# 📝 S-Tab
![Alt text](./docs/assets/logo-space.png)
###  <strong>실시간 그룹 음성 통화와 데이터 공유가 가능한 태블릿 필기 어플리케이션

<br>

# 📚 목차

1. [개요](#-개요)
2. [기술 스택](#-기술-스택)
3. [설계](#-설계)
4. [서비스 및 기능 소개](#-서비스-및-기능-소개)
5. [팀원 소개](#-팀원-소개)


<br><br>

# 📘 개요

> **💻프로젝트 기간** : 2024.04.08 (월) ~ 2024.05.20 (월) <br> **서비스 사용** : exec의 README.md(포팅매뉴얼) 참고   <br> **참고 영상** : [UCC](https://youtu.be/zuS3Bdn8KKA)


<br>

# 🔧 기술 스택
||이슈관리|형상관리|코드리뷰|커뮤니케이션|디자인|
|---|------|---|---|---|---|
|TOOL|<img src="https://img.shields.io/badge/jira-0052CC?style=for-the-badge&logo=jira&logoColor=white">|<img src="https://img.shields.io/badge/gitlab-FC6D26?style=for-the-badge&logo=gitlab&logoColor=white">|<img src="https://img.shields.io/badge/gerrit-EEEEEE?style=for-the-badge&logo=gerrit&logoColor=black">|<img src="https://img.shields.io/badge/mattermost-0058CC?style=for-the-badge&logo=mattermost&logoColor=white">|<img src="https://img.shields.io/badge/figma-F24E1E?style=for-the-badge&logo=figma&logoColor=white">|

## 💻 IDE
<img src="https://img.shields.io/badge/android%20studio-346ac1?style=for-the-badge&logo=android%20studio&logoColor=white">
<img src="https://img.shields.io/badge/IntelliJIDEA-000000.svg?style=for-the-badge&logo=intellij-idea&logoColor=white">
<img src="https://img.shields.io/badge/Visual%20Studio%20Code-0078d7.svg?style=for-the-badge&logo=visual-studio-code&logoColor=white">


<br>

## 🛢 Back-End
<img src="https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white">
<img src="https://img.shields.io/badge/spring%20boot-6DB33F.svg?style=for-the-badge&logo=spring%20boot&logoColor=white">
<img src="https://img.shields.io/badge/spring%20security-6DB33F.svg?style=for-the-badge&logo=spring%20security&logoColor=white">
<img src="https://img.shields.io/badge/Neo4j-008CC1?style=for-the-badge&logo=neo4j&logoColor=white">
<img src="https://img.shields.io/badge/redis-%23DD0031.svg?style=for-the-badge&logo=redis&logoColor=white">
<img src="https://img.shields.io/badge/node.js-6DA55F?style=for-the-badge&logo=node.js&logoColor=white">
<img src="https://img.shields.io/badge/Socket.io-black?style=for-the-badge&logo=socket.io&badgeColor=010101">
<img src="https://img.shields.io/badge/python-3670A0?style=for-the-badge&logo=python&logoColor=ffdd54">
<img src="https://img.shields.io/badge/FastAPI-005571?style=for-the-badge&logo=fastapi">


<br>

### 📱 Front-End
<img src="https://img.shields.io/badge/kotlin-%237F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white">
<img src="https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white">
<img src="https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white">

<br>

### 🌐 Server
<img src="https://img.shields.io/badge/Ubuntu-E95420?style=for-the-badge&logo=ubuntu&logoColor=white">
<img src="https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white">
<img src="https://img.shields.io/badge/nginx-%23009639.svg?style=for-the-badge&logo=nginx&logoColor=white">

<br><br>

# ✏ 설계
### - 아키텍처
![아키텍처](./docs/assets/architecture.png)

<br><br>
### - ERD
![ERD](./docs/assets/erd.PNG)

### - 와이어프레임
![와이어프레임](./docs/assets/figma.PNG)






# 📖 서비스 및 기능 소개 
![로그인 페이지](./docs/assets/login.gif)
- 소셜 로그인을 지원합니다 
<br><br>

### 스페이스
#### 공유 스페이스
![공유 스페이스](./docs/assets/createspace.gif)
![공유 스페이스](./docs/assets/shareinvite.gif)
- 공유 스페이스를 만들고, 초대 링크를 통해 다른 사용자를 스페이스에 초대할 수 있습니다.


![공유 스페이스](./docs/assets/sharespace.gif)
- 공유 스페이스에서 MD editor로 표지를 꾸밀 수 있습니다.
- 공유 스페이스에서 서로의 필기 파일을 함께 보고, 편집할 수 있습니다.
- 공유 스페이스에서 그룹 음성 통화가 가능합니다. 

#### 스페이스 공통 기능 
![공통 기능](./docs/assets/createfile.gif)
- 폴더 및 노트의 생성이 가능합니다


![공통 기능](./docs/assets/bookmark.gif)
- 폴더, 노트, 페이지에 즐겨찾기 등록이 가능합니다.
- 즐겨찾기 해둔 파일들을 모아볼 수 있고, 원하는 파일에 바로 접근이 가능합니다.

![공통 기능](./docs/assets/delete.gif)
- 원하는 폴더나 노트를 삭제할 수 있습니다.
- 삭제된 파일은 휴지통에서 복원할 수 있습니다.


### 노트 에디터 
![공통 기능](./docs/assets/note.gif)
- 원하는 내지 디자인을 선택하여 노트를 만들 수 있습니다.
- 노트에서 S펜을 통하여 필기를 할 수 있습니다.


![공통 기능](./docs/assets/chatbot.gif)
- 필기 중인 노트 화면에서 AI 챗봇을 이용할 수 있습니다. 





<br><br>


# 👨‍👨‍👧 팀원 소개
|          | 탁윤희            | 김연빈            | 김정민            | 김해인            | 이승집            | 정창휘            |
|----------|-------------------|-------------------|-------------------|-------------------|-------------------|-------------------|
| 프로필   | ![img](./docs/assets/profile1.png)     |![img](./docs/assets/profile2.png)       | ![img](./docs/assets/profile4.png)      | ![img](./docs/assets/profile3.png)       | ![img](./docs/assets/profile5.png)       | ![img](./docs/assets/profile6.png)       |
| 역할     | 팀장<br> Frontend    | Backend | Backend     | Frontend   | Frontend           | Backend    |
| 세부 역할 | UI/UX<br>WebRTC<br>Socket<br>Jira 관리        | MSA 설계<br>노트 페이지 <br>UI/UX 보조     |MSA 설계<br>인프라<br>Auth<br>GPT<br>S3<br>WebRTC<br>Socket  |UI/UX <br>노트 에디터<br>Socket     | UI/UX <br>회원 관리 <br>스페이스/폴더/노트        |  스페이스/폴더/노트           |
| 깃헙 주소 | [Github](https://github.com/TakYunhui) | [Github](https://github.com/kyb99) |[Github](https://github.com/jm0nn) |[Github](https://github.com/pengisblue) | [Github](https://github.com/SeungjipLee) | [Github](https://github.com/JungChnagHwi) |

