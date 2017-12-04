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
    - 0x00000001: COMMAND_HEART_BEAT
    - 0x00000002: COMMAND_MESSAGE  e.g. {“code”:0, “msg”:“请求成功”, “data”:{“content”:”hello world!”}}
    - 0x00000003: COMMAND_IMAGE
    - 0x00000004: COMMAND_SCREEN
    - 0x00000005: COMMAND_CAMERA
    - 0x00000006: COMMAND_REGISTER # 注册客户端，如果是服务器返回客户端ID  e.g. {“code”:0, “msg”:“请求成功”, “data”:{“id”:123}}
    - 0x00000007: COMMAND_CLIENT_LIST # 客户端列表，如果是服务器返回客户端列表  e.g. {“code”:0, “msg”:“请求成功”, “data”:{“name”:”客户端列表”, “list”:[0, 1, 2]}}


