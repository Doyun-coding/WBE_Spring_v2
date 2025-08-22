# 📝 WBE_Spring_v2

Spring Boot 기반으로 개발된 **맞춤법 검사 API 서버**입니다.  
사용자가 입력한 텍스트를 분석하여 맞춤법 교정 결과를 반환합니다.  

---

## 📖 개요
- 텍스트 맞춤법 검사 API 제공  
- 교정된 문장 및 오류 리스트 반환  
- RESTful API 기반 설계  
- JSON 형식 요청/응답 지원  

---

## 🚀 주요 기능
- ✅ 문장의 맞춤법 검사  
- ✅ 띄어쓰기, 오탈자 교정  
- ✅ 교정된 결과 및 오류 정보 JSON 응답  
- ✅ Controller / Service / DTO 구조로 확장성 확보  

---

## 📂 디렉토리 구조
```
src/main/java/com/example/spellcheck/
├── controller/
│   └── SpellCheckController.java
├── service/
│   └── SpellCheckService.java
├── dto/
│   ├── SpellCheckRequest.java
│   ├── SpellCheckResponse.java
│   ├── SpellCheckResult.java
│   └── SpellCheckCommand.java
└── api/
    └── SpectatorCurrentGameInfoApiResponse.java
```
