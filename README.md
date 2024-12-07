# 요양시설 낙상 감지 모니터링 시스템

## 프로젝트 개요
본 프로젝트는 **요양시설 내 낙상 사고 예방 및 대응**을 목표로 한 **스마트 모니터링 시스템**입니다.  
**YOLOv8n-pose 모델**과 **RTSP 기반 CCTV 피드**를 활용하여 낙상을 감지하고, 환자의 위치 및 의류 색상을 분석하여 신원을 파악합니다.  
**모바일 앱** 및 **웹 대시보드**를 통해 실시간 알림을 제공하며, 관리자가 즉각적으로 상황을 파악할 수 있도록 지원합니다.

---
## 연구 배경
![image](https://github.com/user-attachments/assets/9c7d399c-fcbe-43da-b22a-4159c66b7580)
![image](https://github.com/user-attachments/assets/2ae0e007-2c0e-4804-9cd0-ee68cbc26de6)

점차 증가하는 대한민국의 고령화 지수와 2023년 6월 23일부터 요양시설 CCTV 의무화.
---
## 주요 기능
### 1. **낙상 감지 알고리즘**
- **YOLOv8n-pose**: 키포인트 추출을 기반으로 자세 분석 및 낙상 여부 판별.
- **임계값 분석**: 키포인트 범위를 비교하여 눕는 동작과 낙상을 구분.
- **실시간 분석**: 실시간 비디오 스트림을 처리하여 즉각적인 대응 가능.

### 2. **RTSP 및 OpenCV 통합**
- **RTSP 프로토콜**을 통한 실시간 영상 수집 및 처리.
- 의류 색상 감지를 활용한 환자 신원 파악 및 위치 추적.

### 3. **웹 및 앱 모니터링 시스템**
- **웹 관리자 대시보드**:
  - 실시간 낙상 감지 및 CCTV 피드 확인.
  
  ![image](https://github.com/user-attachments/assets/da52ca6d-a299-4379-8eab-4409df81deda)

    
  - 시니어 정보 등록 및 관리, 공지사항 및 문의사항 관리.
- **모바일 앱**:
  - 보호자와 관리자에게 실시간 낙상 알림 전송.
 
![image](https://github.com/user-attachments/assets/5f4cce22-b2bc-4e34-bd6f-2993d8f9e1d5)


![image](https://github.com/user-attachments/assets/20d58a72-74d4-4deb-8394-cf72b7b343da)


    
  - 보호자는 담당 환자의 정보와 상태를 확인 가능.

---

## 시스템 구성

전체 시스템 구상도
![image](https://github.com/user-attachments/assets/20af665b-deee-4d14-ba5f-6d09a5dc6db1)

### 1. **낙상 감지 모니터링 시스템 구조**
- **실시간 영상 수집**: RTSP CCTV를 통해 실시간 영상 전송.
- **알고리즘 적용**: YOLOv8n-pose 기반 키포인트 추출 및 분석.
- **알림 전송**: 낙상 감지 시 즉시 관리자 및 보호자에게 알림 발송.


![image](https://github.com/user-attachments/assets/20a0bbb9-a6a7-439e-a4ac-429d582b0152)

### 2. **낙상 감지 알고리즘 과정**
- 키포인트 기반 상태 정의:
  - 자세에 따라 키포인트의 분포 분석.
  - 임계값을 기준으로 서 있는 상태와 눕는 상태 구분.
- 추가 조건:
  - 낙상 후 경과 시간, 장애물 유무를 통해 정확도 향상.

### 3. **주요 기술 스택**
- **모델**: YOLOv8n-pose.
- **영상 처리**: OpenCV.
- **백엔드**: Flask, Firebase.
- **모바일**: Kotlin(Android).
- **웹**: HTML, CSS, JavaScript.

---

## 결과물
### 1. **앱**
- 관리자를 위한 요양시설 업무 지원 시스템:
  - 시니어 정보 확인 및 공지/문의사항 관리.
 
  ![image](https://github.com/user-attachments/assets/497027fb-a222-46ee-9768-fbd216230be6)

  ![image](https://github.com/user-attachments/assets/28103c12-a782-4710-8b68-471fbe327b08)

  ![image](https://github.com/user-attachments/assets/688b490f-a9dc-43f6-a9a9-15d48aa41578)


  - 낙상 발생 시 경고 알림 발송.
    
- 보호자를 위한 앱:
  - 담당 환자의 상태 및 알림 확인.

### 2. **웹**
- 사용자 로그인
- 실시간 CCTV 모니터링 시스템:
  - 낙상 발생 시 상황 확인 및 즉각적인 대처.
    
    ![image](https://github.com/user-attachments/assets/acbd6e7b-da0d-4cae-87ae-68251044a11b)


- 공지사항 및 문의사항 등 관리.

    ![image](https://github.com/user-attachments/assets/3c1741a4-a986-4103-9581-073f2c64fd5a)

    ![image](https://github.com/user-attachments/assets/94693cca-177b-4709-93dd-80e6695f2036)

    ![image](https://github.com/user-attachments/assets/b43c237f-a2f5-4136-969c-af71b82bc10f)


### 3. **Firebase Realtime Database, Storage**
- 임의로 지정한 Firebase Realtime Database 및 Storage에 낙상 로그와 낙상 중인 객체의 크롭 이미지와 낙상 상황의 비디오를 저장.

  ![image](https://github.com/user-attachments/assets/3d7dbe81-3bd2-4f76-a1c3-cab77b1f0a96)

  
### 4. **사용자의 로컬 환경(Image + Video)**
- 사용자의 로컬 환경에 낙상 중인 객체의 크롭 이미지와 낙상 상황의 비디오를 저장.

   ![image](https://github.com/user-attachments/assets/4abf148a-9725-4b39-8a29-39e649368a5e)

---

## 관련대외 활동
![image](https://github.com/user-attachments/assets/1e99774a-14dc-404e-b8f6-d8ecd906ad6e)
![image](https://github.com/user-attachments/assets/25070560-410b-4a3d-aa92-551346ba5cc2)

---

## 기대 효과
- **즉각적인 낙상 사고 대응**: 낙상 사고 발생 시 관리자와 보호자에게 즉시 알림 전송.
- **정확한 낙상 감지**: YOLOv8n-pose 모델을 활용한 키포인트 분석으로 정확도 향상.
- **확장 가능성**: 어린이집, 호스피스 등 사회적 약자 시설로의 응용 가능.

---
