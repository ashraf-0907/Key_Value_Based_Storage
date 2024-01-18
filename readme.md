# High Performance Key Value Store

## Some Notes before we start with the project
1) We are using TCP sockets.
2) Before sending anything to the other socket convert it into the byte stream as we have designed our own protocol.
3) All bytes are stored in little endian format.
4) 


## File Structure 
1) Client.java
2) Server.java
3) config.properties
4) .gitignore
5) readme.md
6) utils
- Property


## Streams Study

### InputStream 
1) An InputStream in Java is like a stream or a flow of data that you can read from. It's commonly used when you want to take information from a source, like a file or network, and bring that data into your Java program.
2) **Methods**
- int read(): Reads the next byte of data from the input stream. Returns -1 if the end of the stream is reached.
- int read(byte[] b): Reads up to b.length bytes of data from the input stream into an array of bytes.
- int read(byte[] b, int off, int len): Reads up to len bytes of data from the input stream into an array of bytes, starting at the specified offset.
### OutputStream
1) An OutputStream in Java is an abstract class representing an output stream of bytes. It is used for writing byte-oriented data.
2) **Methods**
- write(int b): Writes a byte to the output stream.
- write(byte[] b): Writes an array of bytes to the output stream.
- flush(): Flushes the stream, meaning any buffered data is written out.
- close(): Closes the output stream.

3) There are various output streams 
- Socket.getOutputStream().
- FileOutputStream();

### FileInputStream
1) The FileInputStream class in Java is used to create an input stream from a file. It's part of the java.io

```
InputStream input = new FileInputStream(filePath);
```
2) This line creates a new FileInputStream object named input by specifying the path of the file (filePath).
3) The FileInputStream is an InputStream that reads bytes from a file.


## Socket and its constructors
1) A server runs on a specific computer and has a socket that is bound to a specific port number. The server just waits, listening to the socket for a client to make a connection request.
2) The client knows the hostname of the machine on which the server is running and the port number on which the server is listening. To make a connection request, the client tries to rendezvous with the server on the server's machine and port.
3) Def:- A socket is one endpoint of a two-way communication link between two programs running on the network. A socket is bound to a port number so that the TCP layer can identify the application that data is destined to be sent to.
4) Socket sc = new Socket() // created an unconnected socket 
5) Socket sc = new Socket (InetAddress addr,int port) // created a stream socket and connect it to the specified port no.
6) Socket(InetAddress address, int port, InetAddress localAddr, int localPort) // Creates a socket and connects it to the specified remote address on the specified remote port.


### Socket(sc) and its functions 
1) void bind(SocketAddress bindpoint) //Binds the socket to a local address.
2) void connect(SocketAddress endpoint) // connects this socket to the server 
3) connect(SocketAddress endpoint, int timeout) //Connects this socket to the server with a specified timeout value.
4) InetAddress serveraddr = sc.getInetAddress() //Returns the address to which the socket is connected.
5) InputStream input = sc.getInputStream() //Returns an input stream for this socket.
6) OutputStream out = sc.getOutputStream() //Returns an output stream for the socket.

## Conversions

1) String to byte

```
byte[] messageBytes = stringMessage.getBytes(StandardCharsets.UTF_8);
```

2) Int to byte

```
byte[] numberBytes = ByteBuffer.allocate(4).putInt(number(int)).array();
```

3) byte[] to int

```
int intValue = ByteBuffer.wrap(byteArray).getInt();
```

4) byte[] to string 

```
String clientMessage = new String(byteArray);
```


