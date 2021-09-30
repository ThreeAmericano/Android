## 1. Login Activity  
![image](https://user-images.githubusercontent.com/29484377/135442256-e3615b87-343e-413c-8a8a-7db44d24ecf8.png)  
1-1. 로그인을 진행하는 Activity  
1-2. 신규 사용자는 하단 문구를 클릭하여 회원가입이 가능함.  
1-3. 로그인 완료시 MQTT와 FireStore에 접근하여 필요한 데이터를 불러온다.  

## 2. Signup Activity 
![image](https://user-images.githubusercontent.com/29484377/135443246-8fd01387-f34b-451d-ac51-78fd65b973ed.png)  
2-1. 회원가입을 진행하는 Activity  
2-2. 이메일형식의 ID를 입력받으며, Firebase의 Authentication을 통해 회원가입이 진행된다.  

## 3. Main Fragment
![image](https://user-images.githubusercontent.com/29484377/135443274-3be29902-7b32-4f53-8a4c-13af5e93516a.png)  
3-1. 집의 상황 및 가전과 모드 제어를 할 수 있는 Main Fragment  
3-2. 파이어베이스 리스너를 통해 실시간으로 가전과 모드의 상태가 토글버튼에 반영된다.  
3-3. 서버가 날씨, 가전의 상태를 복합적으로 판단하여 안내문구를 보여준다. 특이사항이 없다면 "오늘도 좋은하루 되세요!"를 표시한다.  

## 4. Mode Fragment
![image](https://user-images.githubusercontent.com/29484377/135443738-bff41d49-9d3f-4df8-8689-f5c29373edb4.png)   
4-1. 가전을 한번에 제어할 수 있는 모드를 편집하는 Mode Fragment  
4-2. 모드는 여러개가 존재할 수 있다. 로그인시 불러온 Mode의 정보를 recyclerView로 불러와 표시한다.  
4-3. 모드별 가전 상세 제어는 에어컨은 ON/OFF와 바람세기, 무드등은 ON/OFF와 밝기, 색상, 모드 설정, 가스밸브는 ON/OFF, 창문은 OPEN/CLOSE로 설정할 수 있다.  

## 4. Schedule Fragment
![image](https://user-images.githubusercontent.com/29484377/135444853-e62f89ea-1092-49cb-a8d2-7a7f99220e8e.png)   
4-1. 저장된 스케줄을 표시하는 Schedule Fragment  
4-2. 스케줄도 여러개가 존재할 수 있다. 로그인시 불러온 스케줄의 정보를 recyclerView로 불러와 표시한다.  
4-3. ON/OFF 스위치를 통해 활성화 여부를 설정할 수 있고, 작성자가 누구인지 확인이 가능하다.  
4-4. 저장된 스케줄을 클릭하면 스케줄의 정보가 표시된 Activity을 표시한다.  
4-5. 오른쪽 하단의 추가버튼을 통해 새로운 스케줄을 추가할 수 있다.

## 5. Schedule Activity
![image](https://user-images.githubusercontent.com/29484377/135445200-44cf6778-6a3a-4bbb-b190-1832a0532e8a.png)   
5-1. 새로운 스케줄을 추가하거나 기존 스케줄을 편집하는 Schedule Activity  
5-2. 스케줄의 이름, 실행시간을 입력할 수 있다.
5-3. 스케줄 반복 스위치를 ON하면 원하는 요일을 Toggle하여 설정할 수 있다. 반복 스위치를 OFF시 단발성으로 실행하여 원하는 날짜를 입력하여 실행이 가능하다.
5-4. 모드 제어 스위츠를 ON하면 모드를 불러와 원하는 모드를 실행시킬 수 있다. OFF시 개별 가전을 하나씩 설정하여 수행할 수 있다.

## 6. Alarm Activity
![image](https://user-images.githubusercontent.com/29484377/135446053-6aa21057-c370-467d-9c6f-5dbee966e877.png)     
6-1. 가전이나 모드가 실행된 이력을 확인할 수 있는 Alarm Activity
6-2. 집에서 직접, 혹은 다른 기기에서 실행하였거나 스케줄에서 실행된 이력을 확인할 수 있다.
6-3. 로그인시 20개까지 불러와 표시하며, 파이어베이스 리스너를 통해 실시간으로 실행이력을 불러와 보여줄 수 있다.





