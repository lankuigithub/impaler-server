import socket

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.connect(('127.0.0.1', 8088))
s.send(b'I am Client.')
print(s.recv(1024).decode('utf-8'))

s.close()
