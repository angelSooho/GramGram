# **[체크 리스트]**

### 네이버 클라우드 플랫폼을 통한 배포(도메인 없이, IP로 접속)

- [ ] https://서버IP:포트/ 형태로 접속이 가능
- [ ] 운영서버에서는 각종 소셜로그인, 인스타 아이디 연결이 안되어도 됩니다.

### 호감표시/호감사유변경 후, 개별 호감표시건에 대해서, 3시간 동안은 호감취소와 호감사유변경을 할 수 없도록 작업

- [x] 호감표시를 한 후 개별호감표시건에 대해서, 3시간 동안은 호감취소와 호감사유변경을 할 수 없도록 작업
- [x] 호감사유변경을 한 후 개별호감표시건에 대해서, 3시간 동안은 호감취소와 호감사유변경을 할 수 없도록 작업

###  알림기능 구현

- [x] 호감표시를 받았거나, 본인에 대한 호감사유가 변경된 경우에 알림페이지에서 확인이 가능하도록 해주세요.
- [x] 각각의 알림은 생성시에 readDate 가 null 이고, 사용자가 알림을 읽으면 readDate 가 현재날짜 로 세팅되어야 합니다.

# **[접근 방법]**

### 네이버 클라우드 플랫폼을 통한 배포(도메인 없이, IP로 접속)

테스트 작업에서 오류가 발생하여 아직 빌드를 하지 못해 배포작업을 하지 못하고 있습니다.
테스트 오류를 수정해서 배포 작업을 다시 해보도록 하겠습니다.

### 호감표시/호감사유변경 후, 개별 호감표시건에 대해서, 3시간 동안은 호감취소와 호감사유변경을 할 수 없도록 작업

강사님께서 UI에는 구현을 해두셔서 많이 작성해야 했던 것은 없었습니다.
제한시간을 적용해야할 메소드는 총 2가지 경우 입니다. **변경**, **삭제**가 그 대상인데요.

변경의 경우에는 제한시간을 2곳에서 적용되어야 합니다.

1. 호감 표시시, 중복되지 않은 사유로 호감인원을 저장하고자 하는 경우
2. 호감 목록에서 제한시간 이전에 변경과 삭제 기능을 수행하려고 하는 경우

두 경우 모두 제한 시간을 객체별로 관리하기 위해 likeablePerson 객체에 한가지 변수를 추가하였는데요. 바로 modifyUnlockDate 변수입니다.

modifyUnlockDate 변수는 각 객체별 최종 수정,삭제 가능시간을 나타냅니다. 호감이 표시되거나 변경되었을 때,<br> 
표시, 변경되었던 시간에서 따로 프로퍼티값으로 설정해두었던 3시간을 현재시간에 더해주서 저장하므로 <br>
제한 시간 이후부터 변경, 삭제 로직 수행이 가능하도록 구성하였습니다. 추가적으로 이 시간을 메소드 별로 확인해야하기 때문에 제한시간 이내에 진행하려고 하면 예외처리가 되도록 작성했습니다.

###  알림기능 구현

알림 기능의 경우, 다른 사용자를 생성해서 저(본인)에게 보내게 해서 알림을 확인하고 싶었지만, 인스타를 연동하는 이상 다른 사용자를 생성하기에는 무리가 있었습니다.
따라서 Notification의 더미데이터를 생성해서 기능 로직이 정상적으로 작동하는지 확인하였습니다.

먼저 Notification이 생성되는 조건은 다음과 같습니다.

1. 호감표시가 이루어 졌을 경우
2. 호감사유에 변동이 생겼을 경우

다음 2가지가 알림 객체가 생성되는 순간인데요. 저는 그래서 like 메소드에서 호감 표시용 더미 알림 객체, modify 메소드에서 사유 변경용 더미 알림 객체를 각각 생성해서 <br>
각각 상황에서 알림이 정상적으로 사용자에게 표시되는지 확인하였습니다. 이때 사용자별 알림 조회 확인으로는 Querydsl로 동적쿼리를 구성하여 pullInstaMember, 즉 받은 유저에게 알림이 가야하기 때문에<br> pullInstaMember가 사용자일때만 Notification을 조회하도록 쿼리를 작성하였습니다.

또한 강사님께서는 페이징 기능을 넣지 않아도 된다고 하셨지만, 여유시간이 되어 같이 구현해보았습니다.

#### 부가기능
알림이 보여지는 로직은 작성했지만, 일반적으로 알림은 전체삭제, 부분삭제 기능이 동반됩니다. 그래서 저 또한 해당 기능을 구현하고자 했습니다.

부분 삭제기능은 알림별로 li태그에 존재하게 되는데, 해당 태그안에 하이퍼링크를 같이 넣어서 하이퍼링크를 눌렀을 경우 li의 Notification 객체를 삭제하도록 했습니다.

전체 삭제 기능은 조회 기능과 구성 쿼리를 같습니다. 그러나 select가 아닌 delete로 조회하여 같은 id의 객체는 삭제하여 알림을 한번에 삭제하도록 하였습니다.


# **[특이사항]**

- 특이 사항은 없습니다.

# **[Refactoring]**
- 아직 배포작업을 완료하지 않았습니다. 테스트 로직에서 문제가 발생한 것같은데, 빠르게 오류를 수정하고 배포작업을 완료하겠습니다.
- Notification 도메인 조회 로직에 dto가 빠진 채로 조회하여 데이터 유출에 민감한 상태로 작성되었는데, response dto를 추가하여 상황을 개선시켜보려합니다.