# **[체크 리스트]**

### 네이버클라우드플랫폼을 통한 배포, 도메인, HTTPS 까지 적용
- 링크: https://www.suho.info/
- [x] https://도메인/ 형태로 접속이 가능
- [x] 운영서버에서 각종 소셜로그인, 인스타 아이디 연결이 잘 되어야 합니다.


### 내가 받은 호감리스트(/usr/likeablePerson/toList)에서 옵션별 필터링, 정렬 기능 구현

- [x] 내가 받은 호감리스트에서 특정 성별을 가진 사람에게서 받은 호감만 필터링해서 볼 수 있다.
- [x] 내가 받은 호감리스트에서 특정 호감사유의 호감만 필터링해서 볼 수 있다.
- [x] 아래 케이스 대로 작동해야 한다.

- 최신순(기본)
  - 최근에 받은 호감표시를 우선적으로 표시
- 날짜순
  - 오래전에 받은 호감표시를 우선적으로 표시
- 인기 많은 순
  - 인기가 많은 사람의 호감표시를 우선적으로 표시
- 인기 적은 순
  - 인기가 적은 사람의 호감표시를 우선적으로 표시
- 성별순
  - 여성에게 받은 호감표시를 먼저 표시하고, 그 다음 남자에게 받은 호감표시를 후에 표시
- 2순위 정렬조건으로는 최신순
- 호감사유순
  - 외모 때문에 받은 호감표시를 먼저 표시하고, 그 다음 성격 때문에 받은 호감표시를 후에 표시, 마지막으로 능력 때문에 받은 호감표시를 후에 표시
- 2순위 정렬조건으로는 최신순

# **[접근 방법]**

### 내가 받은 호감리스트(/usr/likeablePerson/toList)에서 옵션별 필터링, 정렬 기능 구현
처음에는 querydsl로 쿼리를 구성하여 성별, 호감사유별로 쿼리가 날라가도록 설정했었습니다.
위 경우까지 구현하는 것은 그렇게 어렵지는 않았는데, 문제는 바로 정렬이었습니다.

정렬에서는 orderBy 메소드로 커스텀한 정렬기준을 생성해야하는데, querydsl에서 그 기능을 구현하려니 심히 어려웠습니다.
따라서 기간내에 미션을 수행하기 위해선 querydsl을 사용하지 않고 작성하는 것이 최선이어서 동작 로직을 완전히 바꿔버렸습니다.

우선은 각각의 값들이 showToList 메소드로 넘어올 수 있도록 @RequestParam값으로 설정하여 파라미터값들을 받았습니다.
그러나 그저 파라미터값으로만 설정하면 기본값을 가져오지 못하기 때문에 defaultValue를 기본 문자값으로 설정하여 받았습니다.

이후에 orderBy 메소드로 서비스 비즈니스 로직을 실행하게 되는데요. 
그 후에 성별, 호감사유 별로 현재 선택되어 있는 필터링 옵션이 있는지 확인하여 likeablePeople의 조건 배열을 가져왔습니다.
만약 선택되어진 옵션값이 없다면 likeablePeople은 모든 값들을 가져오게 됩니다.

이후 정렬을 하게되는데요. 이 경우 자바에서 지원하는 Comparator기능을 사용해서 각각의 LikeablePerson 객체들을 특정 값별로 비교하였는데요.
Comparator 커스텀 메소드는 implement로 받아와서 커스텀 정렬클래스를 만들게되면, 필자가 원하는 조건의 맞게 정렬 기준을 만들 수 있는 기능입니다.
이 기능을 통해 sortType의 값이 들어오면 각 번호에 맞는 정렬 기준에 따라 likeablePeople 값을 정렬하게 됩니다.

이후에 값들은 다시 Model에 입력되어 toList에 표시될 수 있도록 저장하였습니다.

# **[특이사항]**

- 특이 사항은 없습니다.

# **[Refactoring]**
현재는 Comparator의 기능을 사용해서 간단하게 정렬기준을 설정할 수 있었지만, 원래 하려고 계획했던 querydsl의 동적정렬을 구현하지 못했습니다.
리팩토링 시간에 이 기능을 다시 구현해보고 싶습니다.