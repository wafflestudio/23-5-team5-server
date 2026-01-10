# 🤝 팀 개발 컨벤션 (Team Convention)

우리 팀의 원활한 협업을 위한 브랜치 및 커밋 규칙 가이드입니다.

---

## 1. 🌿 브랜치 전략 (Branch Strategy)

우리는 **GitHub Flow**를 따릅니다.
모든 작업은 `main`에서 브랜치를 따서 시작하고, PR을 통해 병합합니다.

### 📌 브랜치 명명 규칙 (Naming Convention)
**포맷: `타입/이슈번호-설명`**
* 모두 **소문자**를 사용합니다.
* 브랜치 이름에 **`#` 기호를 포함하지 않습니다.** (숫자만 기입)
* 띄어쓰기는 하이픈(`-`)으로 대체합니다.

| 타입 (Prefix) | 설명 | 예시 |
| :--- | :--- | :--- |
| **`feat`** | 새로운 기능 개발 | `feat/10-login-api` |
| **`fix`** | 버그 수정 | `fix/12-jwt-error` |
| **`refactor`** | 기능 변경 없는 코드 개선 | `refactor/user-dto` |
| **`docs`** | 문서(README, Swagger) 수정 | `docs/api-spec` |
| **`chore`** | 빌드 설정, 라이브러리 추가 | `chore/5-add-actuator` |
| **`test`** | 테스트 코드 작성 | `test/login-service` |

---

## 2. 💬 커밋 메시지 규칙 (Commit Message)

**포맷: `[태그] 작업 내용`**

| 태그 | 설명 |
| :--- | :--- |
| `[FEAT]` | 새로운 기능 구현 |
| `[FIX]` | 버그, 오류 수정 |
| `[REFACTOR]`| 코드 리팩토링 |
| `[CHORE]` | 빌드, 설정 파일 변경 |
| `[DOCS]` | 문서 수정 |
| `[TEST]` | 테스트 코드 추가/수정 |

**✅ 예시**
* `[FEAT] 회원가입 비즈니스 로직 구현`
* `[FIX] 토큰 만료 시간 버그 수정`
* `[CHORE] 브랜치 명명 규칙 가이드라인 추가`

---

## 3. 🚀 협업 워크플로우 (Workflow)

1. **Issue 생성:** 작업할 내용을 이슈 탭에 등록하고 이슈 번호를 확인합니다.
2. **Branch 생성:** `main`에서 위 규칙에 맞춰 브랜치를 생성합니다.
3. **Dev & Push:** 작업 후 커밋 메시지 규칙을 지켜 푸시합니다.
4. **PR 생성:** Pull Request를 올리고 본문에 `Closes #이슈번호`를 적어 이슈를 연결합니다.
5. **Review & Merge:** 1명 이상의 승인을 받은 후, **Squash and Merge** 합니다.