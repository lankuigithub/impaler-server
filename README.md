# impaler-server


### Command
  - __command protocol__
    - |-- 4 bytes (command type) --|-- 4 bytes (data length) --|-- data --|-- \r\n --|
  - __command type & data length__
    - max: 0x7FFFFFFF
    - min: 0x00000000
