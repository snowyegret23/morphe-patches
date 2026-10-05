# S Chzzk Patches

치지직을 보면서 채팅창을 내 화면에 맞게 조절하는 Android 패치 모음입니다. 휴대폰과 태블릿의 채팅 폭을 각각 저장하고, 글자 크기와 채팅 표시 방식을 바꿀 수 있습니다.

**지원 앱: 치지직 3.14.0** · [Morphe](https://morphe.software)에서 적용합니다.

## 설치하기

최신 Morphe를 설치하고 전문가 모드를 켠 다음, Android 기기에서 아래 링크를 여세요.

**[Morphe에 S Chzzk Patches 추가](https://morphe.software/add-source?github=snowyegret23/morphe-patches)**

수동으로 추가하려면 Morphe의 **소스 → + → 원격**에 저장소 주소를 입력하세요: [snowyegret23/morphe-patches](https://github.com/snowyegret23/morphe-patches)

소스를 추가한 뒤에는 다음 순서로 설치합니다.

1. 기기에 맞는 치지직 **3.14.0 APKM** 파일을 선택합니다.
2. **S Chzzk Patches**에서 적용할 패치를 고릅니다.
3. 원본 앱과 함께 설치하려면 **Change package name**을 켭니다. **Custom app branding**을 켜면 앱 이름을 **치지직 S** 또는 원하는 이름으로 바꿀 수 있습니다.
4. 패치와 설치를 마친 뒤, 치지직의 **설정 → Morphe**에서 채팅과 시청 옵션을 조절합니다.

파일로 가져오려면 [Releases](https://github.com/snowyegret23/morphe-patches/releases/latest)에서 `.mpp`를 내려받아 Morphe의 **소스 → + → 로컬**에서 선택하세요. 파일 관리자에서 `.mpp`를 Morphe로 열어도 됩니다. 원격 소스와 다운로드 링크는 릴리스가 게시된 뒤 사용할 수 있습니다.

## 채팅창 맞추기

| 조절할 항목 | 설정할 수 있는 내용 |
| --- | --- |
| 채팅 폭 | 휴대폰·태블릿 각각 120–600dp, 또는 앱 기본값 |
| 글자와 아이콘 | 글자·이모티콘·배지 크기 80–200%, 줄 간격 80–160% |
| 표시 방식 | 전송 시각 표시, 닉네임과 본문 줄 분리, 닉네임·배지 숨기기 |
| 메시지 필터 | 단어 포함·닉네임 일치 필터, 후원·구독·선물·시스템 등 메시지 숨기기 |
| 채팅 주변 요소 | 후원·통나무 순위, 승부예측, 게임 프로모션, 팔로우 권유 숨기기 |

채팅 폭은 영상과 채팅이 나란히 표시될 때 적용되며 화면 너비에 따라 제한됩니다. 닉네임을 숨겨도 스트리머와 관리자의 표시는 유지합니다.

클린봇 및 임시차단·블라인드 메시지는 **앱에 원문이 남아 있는 경우** 원래 내용을 표시할 수 있습니다. 임시차단·블라인드 원문 표시는 기본으로 꺼져 있으며, 서버가 보내지 않은 내용은 복원하지 못합니다.

최근 표시한 일반 채팅을 최대 200개까지 모아 보는 옵션도 있습니다. 기본으로 꺼져 있고, 기록은 앱 실행 중 메모리에만 보관합니다.

## 함께 제공하는 패치

- **Chat customization**: 채팅 설정 메뉴와 설정 복사·붙여넣기
- **Playback without ads**: 플레이어 광고와 클립 피드 광고 카드 제거
- **CDN playback**: P2P 스트리밍 설정 비활성화
- **Home without promotions**: 홈 화면 프로모션 배너 숨기기
- **Auto claim TongPow**: 보상 팝업을 받으면 앱의 수령 버튼 동작 실행
- **Change package name**: 별도 패키지로 설치하기 위한 이름 지정
- **Custom app branding**: 홈 화면과 앱 목록에 표시할 이름 지정

광고 제거와 통나무 수령에는 채팅 설정 패치도 함께 적용됩니다. 통나무 자동 수령은 앱 안의 Morphe 설정에서 끌 수 있으며, 백그라운드 적립을 별도로 실행하지 않습니다.

## 설정 옮기기와 업데이트

**설정 → Morphe → 설정 관리**에서 설정을 복사한 뒤 다른 기기에 붙여넣을 수 있습니다. 채팅 폭·글자 크기·필터 등이 옮겨지며, 대상 기기의 기존 설정을 교체합니다. 채팅 기록과 패치할 때 지정한 앱 이름·패키지 이름은 포함하지 않습니다.

패키지 이름을 바꿔 설치하면 원본 앱을 지울 필요 없이 두 앱을 함께 사용할 수 있습니다. 각 앱의 로그인과 로컬 데이터는 별도로 저장됩니다. 패치 앱을 업데이트할 때는 처음 설치할 때와 **같은 패키지 이름, 같은 Morphe 서명 키**를 사용하세요.

## 지원 범위

3.14.0의 휴대폰용 480–640dpi APKM과 태블릿 등에 사용하는 120–480dpi APKM을 대상으로 만들었습니다. 120–480dpi 변형은 Android 12L 이상이 필요합니다.

두 변형 모두 패치 적용·병합·재서명과 서명 검증을 통과했습니다. 실제 기기의 동시 설치, 로그인, 설정 화면, 클립보드, 라이브 재생, 화면 회전과 통나무 수령은 아직 검증하지 않았습니다. 다른 앱 버전의 호환성은 보장하지 않습니다.

## 직접 빌드하기

JDK 21, Android SDK 36, Build Tools 36.0.0을 준비하고 `ANDROID_HOME`을 설정합니다. GitHub Packages 읽기 인증은 [Morphe 개발 환경 안내](https://github.com/MorpheApp/morphe-documentation/blob/main/docs/morphe-development/1_setup.md)를 참고하세요.

~~~powershell
.\gradlew.bat :patches:buildAndroid
~~~

빌드한 `.mpp` 파일은 `patches/build/libs/`에 생성됩니다. GitHub 배포는 `main`에 푸시할 때 실행되는 Release 워크플로가 처리합니다. `feat:`·`fix:` 등 커밋 메시지에 따라 버전이 정해지며, 배포가 성공하면 Releases의 번들과 Morphe용 소스 정보가 갱신됩니다.

## 라이선스

[GNU General Public License v3.0](LICENSE)으로 배포합니다. 재사용한 코드의 저작권·라이선스 고지는 [THIRD_PARTY_NOTICES](THIRD_PARTY_NOTICES)와 [NOTICE](NOTICE)에 있습니다.

## Special Thanks

- [Chzzk Platter](https://github.com/lirpa62/Chzzk-Platter): 채팅과 화면 조절 기능의 참고 프로젝트
- [Ample Patches](https://github.com/AmpleReVanced/revanced-patches): 치지직 패치의 출발점과 호환성 참고
- [Morphe Patches Template](https://github.com/MorpheApp/morphe-patches-template): 빌드·릴리스 구조
