# impaler-server


### Command
  
  - __command protocol__
    
    - |-- 4 bytes (command type) --|-- 4 bytes (client id) -- | -- 4 bytes (data length) --|-- data --|-- IMPALER --|
  
  - __split word__
  
    - IMPALER
  
  - __command type & data length__
  
    - max: 0x7FFFFFFF
    - min: 0x00000000
  
  - __client id__
  
    - 0x00000000: NONE    表示群发或发送到服务端
  
  - __command__
    - 0x00000000: PING
    - 0x00000001: PONG
    - 0x00000002: MESSAGE
    - 0x00000003: IMAGE
    - 0x10000001: REGISTER # 请求注册客户端，数据为字符串
    - 0x10000002: CLIENT_LIST_REQUEST # 请求获取客户端列表
    - 0X10000003: CLIENT_LIST_RESPONSE # 返回客户端列表，数据为JSON字符串 e.g. [{clientId:20171129, name:"mark-mac"}]
    - 0x7FFFFFFE: ERROR
    - 0x7FFFFFFF: OK
