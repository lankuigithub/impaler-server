# impaler-server


### Command
  - __command protocol__
    - |-- 4 bytes (command type) --|-- 4 bytes (data length) --|-- data --|-- \r\n --|
  - __command type & data length__
    - max: 0x7FFFFFFF
    - min: 0x00000000
  - __command__
    - 0x00000000: PING
    - 0x00000001: PONG
    - 0x00000002: MESSAGE
    - 0x7FFFFFFF: OK