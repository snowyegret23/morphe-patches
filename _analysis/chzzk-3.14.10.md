# CHZZK 3.14.10 호환성 확인

확인일: 2026-10-12. 번들: 1.2.0. 기존 지원 버전 3.14.0은 유지한다.

## 원본

[APKMirror 3.14.10 배포 목록](https://www.apkmirror.com/apk/naver-corp/치지직-chzzk-네이버-게임의-새-이름/치지직-chzzk-3-14-10-release/)에서 받은 NAVER 서명 원본이다. versionCode는 257이며, 기존 3.14.0은 256이다.

| 변형 | 파일 크기 | APKM SHA-256 |
| --- | ---: | --- |
| universal, 480–640dpi, Android 8.0+ | 128837069 | `61dca871baed4fa2377671c3bd9d1050f642d6f5fad5f1cce22c2d6fb064ffc8` |
| universal, 120–480dpi, Android 12L+ | 130748612 | `eac06d8a3cdef816fea22aa8563baf2e28a1b9e6b59ea6dbb2d39d6ccc120c58` |

두 base.apk의 인증서 SHA-256은 기존과 같은 `0b8b8523bb4aeffa346e4bdd4fbf7d193450569aa14aaad4adfd94a3f7b227bb`이다. `apksigner verify --print-certs`로 확인했다. 각 앱 버전 안에서 휴대폰·태블릿의 원본 DEX 6개는 SHA-256이 모두 같다.

원본 보관 경로는 `_external/apk/chzzk-3.14.10-{phone,tablet}.apkm`, 추출본은 `_external/apk/3.14.10/{phone,tablet}/base.apk`이다. 원본은 수정하지 않았다.

## 확인한 변경점

1. 기존 1.1.2 번들을 신버전에 강제 적용한 진단 실행에서 `Unexpected chat width owner: Laq0;` 오류가 발생했다. 호환 버전 목록만 추가해서는 해결되지 않는다.
2. DEX의 `invoke` 메서드 중 float 280과 350을 함께 사용하는 채팅 기본 폭 계산은 다음과 같이 이동했다.

| 앱 버전 | 화면의 폭 계산 | 상태의 폭 계산 |
| --- | --- | --- |
| 3.14.0 | `Low0;` | `Lzp0;` |
| 3.14.10 | `Lpw0;` | `Laq0;` |

`Lpw0;`의 원본 invoke 명령 107·109가 각각 350f·280f 상수이다. `Laq0;`의 case 6도 태블릿 여부에 따라 350f·280f를 반환한다. 두 계산의 필드 타입과 역할을 기존 계산과 대조했다. 패치는 `packageMetadata.versionName`으로 해당 버전의 두 소유 클래스를 선택하며, 예상하지 못한 클래스와 개수는 계속 거부한다.

3. `Lh7;->invoke()`의 명령은 두 버전에서 동일하다. case 4·5는 채팅 폭을 창의 절반으로 제한하고, case 6은 남는 영상 폭을 계산한다. 기존 제한 해제 훅을 유지한다.
4. 설정 목록 `Lu0;`의 `시청 환경 설정` 문자열과 SettingsScreenKt 호출, 방송 메뉴 `Lq1;`의 DepthSelect 생성 지점이 유지된다. 패치는 문자열·생성자 호출로 위치를 찾으므로 메서드 내부 명령 수 변화에 대응한다.
5. BaseLiveChatMessage의 원문·시각·클린봇 getter, StreamingChatMessage의 닉네임·기기 정보 필드, 채팅 스타일 getter와 캐시, 닉네임/배지 연결부를 대조했다. 신버전용 메서드 이름 변경은 필요하지 않았다.
6. 플레이어의 BasePlayerViewModel.R, PrismUiContext.w, Media.z, PrismPlayerCompatKt.h와 버튼 c/e/g/k의 연결이 유지된다. 동심원 버튼에 사용하는 SemanticImageKt.c의 매개변수도 같다.
7. 기기 아이콘 리소스는 `ds_ico_pc=0x7f080496`, `ds_ico_phone=0x7f080497`, `ds_ico_ios=0x7f08045a`로 유지된다. 원본의 `aapt2 dump resources`로 확인했다.
8. PeerNetworkConfiguration 생성자에는 Integer 타입 engineLogLevel이 추가됐다. P2P 활성 필드는 여전히 유일한 boolean 필드 `a`이다. CDN 패치는 필드 타입과 생성자 반환 지점을 찾으므로 기존 코드를 유지한다.
9. 통나무 수령 collector의 레지스터 수 16과 MutableStateFlow.setValue의 v15/v4, 기존 수령 버튼 생성자 연결이 유지된다. 광고·홈 배너 패치도 신버전에서 원래의 타입/매개변수 조건으로 해석된다.

## 검증 결과

| 원본 | 7개 패치 | APKM 병합·재빌드 | 서명 검증 |
| --- | --- | --- | --- |
| 3.14.0 phone | 성공 | 성공 | 성공 |
| 3.14.0 tablet | 성공 | 성공 | 성공 |
| 3.14.10 phone | 성공 | 성공 | 성공 |
| 3.14.10 tablet | 성공 | 성공 | 성공 |

최종 실행에는 버전 검사를 건너뛰는 `--force`나 실패를 무시하는 `--continue-on-error`를 사용하지 않았다. 네 결과 JSON 모두 appliedPatches=7, failedPatches=0이며 PATCHING/REBUILDING/SIGNING 단계가 성공했다. 각 버전의 패치 산출물도 휴대폰·태블릿 간 DEX 7개가 모두 동일하다.

패키지 이름은 `com.navercorp.game.android.community.morphe`, 앱 이름은 `치지직 S`, 버전은 입력 원본과 같다. 네 APK 모두 기존 PC 검증 키로 서명했다. 인증서 SHA-256: `f942cb7b4595c1141b250a1a3ece53d93bcf50414cb4691d8d0b5ef0ad598aa0`.

신버전의 확장 코드와 확장 코드 호출부에 추가된 참조 956개를 검사했다. APK 안에서 해석되는 메서드·필드·타입에 누락이나 static/instance 불일치가 없었다. Android 상속 멤버 33개는 SDK 외부 참조로 구분했다. 이 정적 검사는 ART 실행이나 실기기 확인을 대체하지 않는다.

Gradle 빌드와 generatePatchesList가 성공했으며, 생성 목록의 7개 패치는 모두 기본 선택이고 지원 대상에 3.14.10/3.14.0이 함께 들어 있다.

번들 SHA-256: `dc89366162bd225ad08fd66d2c058be105526705e808bb1bb18763cab97a689c`.

## 재현 명령

JDK 21의 java와 Android SDK 36 도구를 사용했다. 아래 예시에서 원본 버전과 phone/tablet를 바꿔 네 조합을 실행한다.

~~~powershell
.\gradlew.bat :patches:clean :patches:buildAndroid :patches:generatePatchesList --no-daemon
java -Xmx6g -jar _external/tools/morphe-desktop.jar patch _external/apk/chzzk-3.14.10-phone.apkm -p patches/build/libs/patches-1.2.0.mpp -o _external/apk/s-chzzk-1.2.0-3.14.10-phone.apk -r _external/apk/s-chzzk-1.2.0-3.14.10-phone-result.json -t _external/apk/validation-1.2.0-3.14.10-phone --striplibs arm64-v8a
_external/android-sdk/build-tools/36.0.0/apksigner.bat verify --print-certs _external/apk/s-chzzk-1.2.0-3.14.10-phone.apk
_external/android-sdk/build-tools/36.0.0/aapt2.exe dump badging _external/apk/s-chzzk-1.2.0-3.14.10-phone.apk
~~~

최초 병렬 실행에서는 CLI가 같은 밀리초 이름의 임시 디렉터리를 만들어 리소스가 충돌했다. 각 실행에 고유한 `-t` 경로를 지정한 재실행은 모두 통과했다. 이 실패는 패치의 버전 호환성 오류와 구분한다.

이번 검증 시 `adb devices -l`과 `adb mdns services`에 연결 기기가 없었다. 3.14.10 실기기 실행은 미검증이다. 기존 3.14.0의 샤오신패드 실기기 확인 결과는 README에 유지했다.
